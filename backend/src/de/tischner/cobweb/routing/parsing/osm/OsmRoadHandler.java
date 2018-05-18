package de.tischner.cobweb.routing.parsing.osm;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.IRoutingConfigProvider;
import de.tischner.cobweb.db.IRoutingDatabase;
import de.tischner.cobweb.db.SpatialNodeData;
import de.tischner.cobweb.parsing.RecentHandler;
import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.tischner.cobweb.parsing.osm.IOsmFilter;
import de.tischner.cobweb.parsing.osm.OsmParseUtil;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.road.IGetNodeById;
import de.tischner.cobweb.routing.model.graph.road.IHasId;
import de.tischner.cobweb.routing.model.graph.road.ISpatial;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

/**
 * Implementation of an {@link IOsmFileHandler} which constructs a graph that
 * consists of road nodes and edges out of the given OSM data.<br>
 * <br>
 * The graph can be cached, then the handler will only parse files that provide
 * data the graph does not already contain. The handler will only parse OSM ways
 * which are accepted by a given road filter, nodes and relations are rejected.
 * Nodes will be constructed from ways instead and meta data of nodes will be
 * fetched from a given database. The node and edge instances itself are created
 * using a given builder.<br>
 * <br>
 * <i>Implementation note</i>: In order to ensure that, when requesting spatial
 * data, no nodes are lost, it must be ensured that node mappings are already in
 * the database when pushing the requests. The current implementation ensures
 * this by giving both a buffer of the same size, always flushing both at the
 * same time. First the mappings, directly after the spatial data requests.
 * Therefore, it is also important that node and way ID mappings are kept in
 * different buffer. Else it would not be ensured that the ID mapping buffer is
 * full exactly when the spatial data buffer is full.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 * @param <G> Type of the graph
 */
public final class OsmRoadHandler<N extends INode & IHasId & ISpatial, E extends IEdge<N> & IHasId,
    G extends IGraph<N, E> & IGetNodeById<N>> implements IOsmFileHandler {
  /**
   * The size of the buffers. If a buffer reaches the limit the buffered
   * entities are submitted to the database.
   */
  private static final int BUFFER_SIZE = 100_000;
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(OsmRoadHandler.class);
  /**
   * The buffer to use for buffering node ID mappings which are to be submitted
   * to the database. The buffer is used to avoid submitting data for every
   * mapping in a single connection to the database.
   */
  private final IdMapping[] mBufferedNodeMappings;
  /**
   * The buffer to use for buffering OSM node IDs for which spatial node data is
   * to be requested from the database. The buffer is used to avoid requesting
   * data for every node in a single connection to the database.
   */
  private final long[] mBufferedSpatialRequests;
  /**
   * The buffer to use for buffering way ID mappings which are to be submitted
   * to the database. The buffer is used to avoid submitting data for every
   * mapping in a single connection to the database.
   */
  private final IdMapping[] mBufferedWayMappings;
  /**
   * Builder to use for constructing edges and nodes that are to be inserted
   * into the graph.
   */
  private final IOsmRoadBuilder<N, E> mBuilder;
  /**
   * The database used for requesting spatial node data.
   */
  private final IRoutingDatabase mDatabase;
  /**
   * The OSM filter used to filter road ways.
   */
  private final IOsmFilter mFilter;
  /**
   * The graph to insert parsed nodes and edges into.
   */
  private final G mGraph;
  /**
   * The current index to use in the node mapping buffer. It points to the index
   * where the next node mapping can be inserted. So it is always one greater
   * than the index of the last inserted node mapping. By that it represents the
   * current size of the buffer.
   */
  private int mNodeMappingBufferIndex;
  /**
   * The handler to use which determines the OSM files that contain more recent
   * or new data than the data already stored in the graph. Will only be used if
   * the configuration has set the use of a graph cache.
   */
  private final RecentHandler mRecentHandler;
  /**
   * The current index to use in the OSM node ID buffer. It points to the index
   * where the next node ID can be inserted. So it is always one greater than
   * the index of the last inserted node ID. By that it represents the current
   * size of the buffer.
   */
  private int mSpatialRequestBufferIndex;
  /**
   * Whether or not a graph cache is to be used. This determines if OSM files
   * should be filtered by a {@link RecentHandler} or not.
   */
  private final boolean mUseGraphCache;
  /**
   * The current index to use in the way mapping buffer. It points to the index
   * where the next way mapping can be inserted. So it is always one greater
   * than the index of the last inserted way mapping. By that it represents the
   * current size of the buffer.
   */
  private int mWayMappingBufferIndex;

  /**
   * Creates a new OSM road handler which operates on the given graph using the
   * given configuration.<br>
   * <br>
   * The filter is used to filter OSM road ways. The builder will be used to
   * construct the nodes and edges that are to be inserted into the graph. The
   * database offers spatial node data for nodes.
   *
   * @param graph    The graph to insert nodes and edges into
   * @param filter   The OSM filter used to filter road ways
   * @param builder  Builder to use for constructing edges and nodes that are to
   *                 be inserted into the graph.
   * @param database The database used for requesting spatial node data.
   * @param config   Configuration provider which provides graph cache
   *                 information
   * @throws IOException If an I/O exception occurred while reading the graph
   *                     cache information
   */
  public OsmRoadHandler(final G graph, final IOsmFilter filter, final IOsmRoadBuilder<N, E> builder,
      final IRoutingDatabase database, final IRoutingConfigProvider config) throws IOException {
    mGraph = graph;
    mFilter = filter;
    mBuilder = builder;
    mDatabase = database;
    // It is important that both buffer have the same size to ensure
    // mappings are inside the database when requesting spatial data
    mBufferedNodeMappings = new IdMapping[BUFFER_SIZE];
    mBufferedWayMappings = new IdMapping[BUFFER_SIZE];
    mBufferedSpatialRequests = new long[BUFFER_SIZE];

    mUseGraphCache = config.useGraphCache();
    if (mUseGraphCache) {
      mRecentHandler = new RecentHandler(config.getGraphCacheInfo());
    } else {
      mRecentHandler = null;
    }
  }

  /*
   * (non-Javadoc)
   * @see de.topobyte.osm4j.core.access.OsmHandler#complete()
   */
  @Override
  public void complete() throws IOException {
    // Submit buffers, note that the order is important
    submitBufferedNodeMappings();
    submitBufferedWayMappings();
    submitBufferedSpatialRequests();

    mBuilder.complete();
    if (mUseGraphCache) {
      mRecentHandler.updateInfo();
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model. iface.OsmBounds)
   */
  @Override
  public void handle(final OsmBounds bounds) throws IOException {
    // Ignore
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model. iface.OsmNode)
   */
  @Override
  public void handle(final OsmNode node) throws IOException {
    // Ignore, we read nodes from ways instead since we do not want to read in
    // unnecessary nodes
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model. iface.OsmRelation)
   */
  @Override
  public void handle(final OsmRelation relation) throws IOException {
    // Ignore, we are only interested on ways
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model. iface.OsmWay)
   */
  @Override
  public void handle(final OsmWay way) throws IOException {
    // Return if filter does not accept
    if (!mFilter.filter(way)) {
      return;
    }

    // Determine way direction
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(way);
    final int wayDirection = OsmParseUtil.parseWayDirection(tagToValue);

    Integer internalWayId = null;

    // Iterate all nodes
    long sourceIdOsm = -1;
    boolean isFirstIteration = true;
    for (int i = 0; i < way.getNumberOfNodes(); i++) {
      final long destinationIdOsm = way.getNodeId(i);
      // Attempt to add the current node, spatial data is unknown at first
      final Node destinationNode = new Node(destinationIdOsm, 0.0, 0.0);
      final N node = mBuilder.buildNode(destinationNode);
      final boolean wasAdded = mGraph.addNode(node);
      // Request spatial data of the node and register the ID mapping
      if (wasAdded) {
        // It is important that the mappings are in the database when
        // requesting spatial data.
        queueNodeIdMapping(destinationIdOsm, node.getId());
        queueSpatialNodeRequest(destinationIdOsm);
      }

      // Update and yield the first iteration
      if (isFirstIteration) {
        sourceIdOsm = destinationIdOsm;
        isFirstIteration = false;
        continue;
      }

      // Create an edge
      if (wayDirection >= 0) {
        final E edge = mBuilder.buildEdge(way, sourceIdOsm, destinationIdOsm);
        internalWayId = edge.getId();
        mGraph.addEdge(edge);
      }
      if (wayDirection <= 0) {
        final E edge = mBuilder.buildEdge(way, destinationIdOsm, sourceIdOsm);
        internalWayId = edge.getId();
        mGraph.addEdge(edge);
      }

      // Update for the next iteration
      sourceIdOsm = destinationIdOsm;
    }

    // Queue way ID mapping
    if (internalWayId != null) {
      queueWayIdMapping(way.getId(), internalWayId);
    }
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.parsing.IFileHandler#acceptFile(java.nio.file.Path)
   */
  @Override
  public boolean isAcceptingFile(final Path file) {
    // Check if the files content is not already included in the cache
    if (mUseGraphCache && !mRecentHandler.isAcceptingFile(file)) {
      return false;
    }

    // Accept all OSM files
    LOGGER.info("Accepts file {}", file);
    return true;
  }

  /**
   * Inserts the given spatial node data into the graph. That is, it finds the
   * node and updates its spatial data according to the given data.<br>
   * <br>
   * The node represented by the data must exist in the graph.
   *
   * @param data The data to insert
   */
  private void insertSpatialData(final SpatialNodeData data) {
    final Optional<N> possibleNode = mGraph.getNodeById(data.getId());
    // Node must be present since we added it before requesting
    if (!possibleNode.isPresent()) {
      throw new AssertionError();
    }
    // Set data to node
    final N node = possibleNode.get();
    node.setLatitude(data.getLatitude());
    node.setLongitude(data.getLongitude());
  }

  /**
   * Queues a node ID mapping which is to be pushed to the database. The data is
   * buffered and the buffer is submitted using
   * {@link #submitBufferedNodeMappings()}.
   *
   * @param osmId      The unique OSM ID of the mapping to queue
   * @param internalId The internal ID of the mapping to queue
   */
  private void queueNodeIdMapping(final long osmId, final int internalId) {
    // If buffer is full, submit it
    if (mNodeMappingBufferIndex >= mBufferedNodeMappings.length) {
      submitBufferedNodeMappings();
    }

    // Collect the mapping, index has changed due to submit
    mBufferedNodeMappings[mNodeMappingBufferIndex] = new IdMapping(osmId, internalId, true);

    // Increase index
    mNodeMappingBufferIndex++;
  }

  /**
   * Queues a spatial node data request for the given node. The request is
   * buffered and the buffer is submitted using
   * {@link #submitBufferedSpatialRequests()}.
   *
   * @param nodeIdOsm The unique OSM ID of the node to queue a request for
   */
  private void queueSpatialNodeRequest(final long nodeIdOsm) {
    // If buffer is full, submit it
    if (mSpatialRequestBufferIndex >= mBufferedSpatialRequests.length) {
      submitBufferedSpatialRequests();
    }

    // Collect the node, index has changed due to submit
    mBufferedSpatialRequests[mSpatialRequestBufferIndex] = nodeIdOsm;

    // Increase index
    mSpatialRequestBufferIndex++;
  }

  /**
   * Queues a way ID mapping which is to be pushed to the database. The data is
   * buffered and the buffer is submitted using
   * {@link #submitBufferedWayMappings()}.
   *
   * @param osmId      The unique OSM ID of the mapping to queue
   * @param internalId The internal ID of the mapping to queue
   */
  private void queueWayIdMapping(final long osmId, final int internalId) {
    // If buffer is full, submit it
    if (mWayMappingBufferIndex >= mBufferedWayMappings.length) {
      submitBufferedWayMappings();
    }

    // Collect the mapping, index has changed due to submit
    mBufferedWayMappings[mWayMappingBufferIndex] = new IdMapping(osmId, internalId, false);

    // Increase index
    mWayMappingBufferIndex++;
  }

  /**
   * Submits the buffered node ID mappings. The mappings are send to the given
   * database.<br>
   * <br>
   * Afterwards, the buffer index is reset to implicitly clear the buffer.
   * Ideally, this method is only used when the buffer is full.
   */
  private void submitBufferedNodeMappings() {
    // Send all buffered mappings up to the current index
    final int size = mNodeMappingBufferIndex;
    if (size == 0) {
      return;
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Submitting node ID mappings of size: {}", size);
    }
    mDatabase.offerIdMappings(Arrays.stream(mBufferedNodeMappings, 0, size), size);

    // Reset index since buffer is empty again
    mNodeMappingBufferIndex = 0;
  }

  /**
   * Submits the buffered requests. The requests are send to the given database
   * and the spatial node data are inserted into the graph using
   * {@link #insertSpatialData(SpatialNodeData)}.<br>
   * <br>
   * Afterwards, the buffer index is reset to implicitly clear the buffer.
   * Ideally, this method is only used when the buffer is full.
   */
  private void submitBufferedSpatialRequests() {
    // Send all buffered requests up to the current index
    final int size = mSpatialRequestBufferIndex;
    if (size == 0) {
      return;
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Submitting buffered requests of size: {}", size);
    }
    final Collection<SpatialNodeData> nodeData =
        mDatabase.getSpatialNodeData(Arrays.stream(mBufferedSpatialRequests, 0, size), size);
    if (nodeData.size() < size) {
      LOGGER.error("Database did not deliver spatial data for all {} nodes, lost: {}", size, size - nodeData.size());
    }
    nodeData.forEach(this::insertSpatialData);

    // Reset index since buffer is empty again
    mSpatialRequestBufferIndex = 0;
  }

  /**
   * Submits the buffered way ID mappings. The mappings are send to the given
   * database.<br>
   * <br>
   * Afterwards, the buffer index is reset to implicitly clear the buffer.
   * Ideally, this method is only used when the buffer is full.
   */
  private void submitBufferedWayMappings() {
    // Send all buffered mappings up to the current index
    final int size = mWayMappingBufferIndex;
    if (size == 0) {
      return;
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Submitting way ID mappings of size: {}", size);
    }
    mDatabase.offerIdMappings(Arrays.stream(mBufferedWayMappings, 0, size), size);

    // Reset index since buffer is empty again
    mWayMappingBufferIndex = 0;
  }
}
