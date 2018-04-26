package de.tischner.cobweb.routing.parsing.osm;

import de.tischner.cobweb.parsing.ParseException;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;

public final class OsmRoadBuilder<G extends IGraph<RoadNode, RoadEdge<RoadNode>> & ICanGetNodeById<RoadNode>>
    implements IOsmRoadBuilder<RoadNode, RoadEdge<RoadNode>> {

  private final G mGraph;

  public OsmRoadBuilder(final G graph) {
    mGraph = graph;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.routing.parsing.osm.IRoadBuilder#buildEdge(de.topobyte.
   * osm4j.core.model.iface.OsmWay, long, long)
   */
  @Override
  public RoadEdge<RoadNode> buildEdge(final OsmWay way, final long sourceId, final long destinationId)
      throws ParseException {
    final RoadNode source = mGraph.getNodeById(sourceId).orElseThrow(() -> new ParseException());
    final RoadNode destination = mGraph.getNodeById(destinationId).orElseThrow(() -> new ParseException());
    // TODO Compute cost
    final double cost = 0.0;
    return new RoadEdge<>(way.getId(), source, destination, cost);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.routing.parsing.osm.IRoadBuilder#buildNode(de.topobyte.
   * osm4j.core.model.iface.OsmNode)
   */
  @Override
  public RoadNode buildNode(final OsmNode node) {
    return new RoadNode(node.getId(), node.getLatitude(), node.getLongitude());
  }

}
