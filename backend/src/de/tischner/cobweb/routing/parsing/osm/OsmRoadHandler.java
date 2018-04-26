package de.tischner.cobweb.routing.parsing.osm;

import java.io.IOException;

import de.tischner.cobweb.parsing.osm.IOsmFilter;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.road.IHasId;
import de.topobyte.osm4j.core.access.OsmHandler;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

public final class OsmRoadHandler<N extends INode & IHasId, E extends IEdge<N> & IHasId> implements OsmHandler {
  private final IRoadBuilder<N, E> mBuilder;
  private final IOsmFilter mFilter;
  private final IGraph<N, E> mGraph;

  public OsmRoadHandler(final IGraph<N, E> graph, final IOsmFilter filter, final IRoadBuilder<N, E> builder) {
    mGraph = graph;
    mFilter = filter;
    mBuilder = builder;
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
    // Ignore, we read nodes from ways instead since spatial data is not needed
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
      mGraph.addNode(mBuilder.buildNode(destinationId));

      // Yield the first iteration
      if (destinationId == -1) {
        continue;
      }

      // Create an edge
      mGraph.addEdge(mBuilder.buildEdge(way, sourceId, destinationId));

      // Update for the next iteration
      sourceId = destinationId;
    }
  }
}
