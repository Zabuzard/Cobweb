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
import de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById;
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
 * using a given builder.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 * @param <G> Type of the graph
 */
public final class OsmRoadHandler<N extends INode & IHasId & ISpatial, E extends IEdge<N> & IHasId,
    G extends IGraph<N, E> & ICanGetNodeById<N>> implements IOsmFileHandler {
  /**
   * The size of the node ID buffer. If the buffer reaches the limit spatial
   * node data from all buffered node IDs is requested from the database.
   */
  private static final int BUFFER_SIZE = 100_000;
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(OsmRoadHandler.class);
  /**
   * The buffer to use for buffering node IDs for which spatial node data is to
   * be requested from the database. The buffer is used to avoid requesting data
   * for every node in a single connection to the database.
   */
  private final long[] mBufferedRequests;
  /**
   * The current index to use in the node ID buffer. It points to the index
   * where the next node ID can be inserted. So it is always one greater than
   * the index of the last inserted node ID. By that it represents the current
   * size of the buffer.
   */
  private int mBufferIndex;
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
   * The handler to use which determines the OSM files that contain more recent
   * or new data than the data already stored in the graph. Will only be used if
   * the configuration has set the use of a graph cache.
   */
  private final RecentHandler mRecentHandler;
  /**
   * Whether or not a graph cache is to be used. This determines if OSM files
   * should be filtered by a {@link RecentHandler} or not.
   */
  private final boolean mUseGraphCache;

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
    mBufferedRequests = new long[BUFFER_SIZE];

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
    // Submit buffer
    submitBufferedRequests();

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

    // Iterate all nodes
    long sourceId = -1;
    for (int i = 0; i < way.getNumberOfNodes(); i++) {
      final long destinationId = way.getNodeId(i);
      // Attempt to add the current node, spatial data is unknown at first
      final Node destinationNode = new Node(destinationId, 0.0, 0.0);
      final boolean wasAdded = mGraph.addNode(mBuilder.buildNode(destinationNode));
      // Request spatial data of the node
      if (wasAdded) {
        queueSpatialNodeRequest(destinationId);
      }

      // Update and yield the first iteration
      if (sourceId == -1) {
        sourceId = destinationId;
        continue;
      }

      // Create an edge
      if (wayDirection >= 0) {
        mGraph.addEdge(mBuilder.buildEdge(way, sourceId, destinationId));
      }
      if (wayDirection <= 0) {
        mGraph.addEdge(mBuilder.buildEdge(way, destinationId, sourceId));
      }

      // Update for the next iteration
      sourceId = destinationId;
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
   * Queues a spatial node data request for the given node. The request is
   * buffered and the buffer is submitted using
   * {@link #submitBufferedRequests()}.
   *
   * @param nodeId The unique ID of the node to queue a request for
   */
  private void queueSpatialNodeRequest(final long nodeId) {
    // If buffer is full, submit it
    if (mBufferIndex >= mBufferedRequests.length) {
      submitBufferedRequests();
    }

    // Collect the node, index has changed due to submit
    mBufferedRequests[mBufferIndex] = nodeId;

    // Increase index
    mBufferIndex++;
  }

  /**
   * Submits the buffered requests. The requests are send to the given database
   * and the spatial node data are inserted into the graph using
   * {@link #insertSpatialData(SpatialNodeData)}.<br>
   * <br>
   * Afterwards, the buffer index is reset to implicitly clear the buffer.
   * Ideally, this method is only used when the buffer is full.
   */
  private void submitBufferedRequests() {
    // Send all buffered requests up to the current index
    final int size = mBufferIndex;
    if (size == 0) {
      return;
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Submitting buffered requests of size: {}", size);
    }
    final Collection<SpatialNodeData> nodeData =
        mDatabase.getSpatialNodeData(Arrays.stream(mBufferedRequests, 0, size), size);
    nodeData.forEach(this::insertSpatialData);

    // Reset index since buffer is empty again
    mBufferIndex = 0;
  }
}
