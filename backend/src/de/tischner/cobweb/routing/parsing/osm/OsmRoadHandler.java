package de.tischner.cobweb.routing.parsing.osm;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import de.tischner.cobweb.db.IRoutingDatabase;
import de.tischner.cobweb.db.SpatialNodeData;
import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.tischner.cobweb.parsing.osm.IOsmFilter;
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

public final class OsmRoadHandler<N extends INode & IHasId & ISpatial, E extends IEdge<N> & IHasId, G extends IGraph<N, E> & ICanGetNodeById<N>>
    implements IOsmFileHandler {
  private static final int BUFFER_SIZE = 100_000;
  private final long[] mBufferedRequests;
  private int mBufferIndex;
  private final IOsmRoadBuilder<N, E> mBuilder;
  private final IRoutingDatabase mDatabase;
  private final IOsmFilter mFilter;
  private final G mGraph;

  public OsmRoadHandler(final G graph, final IOsmFilter filter, final IOsmRoadBuilder<N, E> builder,
      final IRoutingDatabase database) {
    mGraph = graph;
    mFilter = filter;
    mBuilder = builder;
    mDatabase = database;
    mBufferedRequests = new long[BUFFER_SIZE];
    mBufferIndex = 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.tischner.cobweb.parsing.IFileHandler#acceptFile(java.nio.file.Path)
   */
  @Override
  public boolean acceptFile(final Path file) {
    // TODO Check cache to see which files are needed
    // We are interested in all OSM files
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.topobyte.osm4j.core.access.OsmHandler#complete()
   */
  @Override
  public void complete() throws IOException {
    // Submit buffer
    submitBufferedRequests();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.model.
   * iface.OsmBounds)
   */
  @Override
  public void handle(final OsmBounds bounds) throws IOException {
    // Ignore
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.model.
   * iface.OsmNode)
   */
  @Override
  public void handle(final OsmNode node) throws IOException {
    // Ignore, we read nodes from ways instead since we do not want to read in
    // unnecessary nodes
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.model.
   * iface.OsmRelation)
   */
  @Override
  public void handle(final OsmRelation relation) throws IOException {
    // Ignore, we are only interested on ways
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.model.
   * iface.OsmWay)
   */
  @Override
  public void handle(final OsmWay way) throws IOException {
    // Return if filter does not accept
    if (!mFilter.filter(way)) {
      return;
    }

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
      mGraph.addEdge(mBuilder.buildEdge(way, sourceId, destinationId));

      // Update for the next iteration
      sourceId = destinationId;
    }
  }

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

  private void submitBufferedRequests() {
    // Send all buffered requests up to the current index
    final Set<SpatialNodeData> nodeData = mDatabase
        .getSpatialNodeData(Arrays.stream(mBufferedRequests).limit(mBufferIndex + 1));
    nodeData.forEach(this::insertSpatialData);

    // Reset index since buffer is empty again
    mBufferIndex = 0;
  }
}
