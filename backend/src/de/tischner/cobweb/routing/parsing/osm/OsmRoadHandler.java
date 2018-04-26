package de.tischner.cobweb.routing.parsing.osm;

import java.io.IOException;
import java.nio.file.Path;

import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.tischner.cobweb.parsing.osm.IOsmFilter;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.road.IHasId;
import de.tischner.cobweb.routing.model.graph.road.ISpatial;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;

public final class OsmRoadHandler<N extends INode & IHasId & ISpatial, E extends IEdge<N> & IHasId>
    implements IOsmFileHandler {
  private final IOsmRoadBuilder<N, E> mBuilder;
  private final IOsmFilter mFilter;
  private final IGraph<N, E> mGraph;

  public OsmRoadHandler(final IGraph<N, E> graph, final IOsmFilter filter, final IOsmRoadBuilder<N, E> builder) {
    mGraph = graph;
    mFilter = filter;
    mBuilder = builder;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.tischner.cobweb.parsing.IFileHandler#acceptFile(java.nio.file.Path)
   */
  @Override
  public boolean acceptFile(final Path file) {
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
    // Ignore
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
      // Attempt to add the current node
      // TODO Get Node coordinates from database
      final double latitude = 0.0;
      final double longitude = 0.0;
      final Node destinationNode = new Node(destinationId, longitude, latitude);
      mGraph.addNode(mBuilder.buildNode(destinationNode));

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
}
