package de.tischner.cobweb.routing.parsing.osm;

import java.util.Map;

import de.tischner.cobweb.parsing.ParseException;
import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.parsing.osm.OsmParseUtil;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

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

    // Get information about the highway type
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(way);
    final EHighwayType type = OsmParseUtil.parseHighwayType(tagToValue.get(OsmParseUtil.HIGHWAY_TAG));
    final int maxSpeed = OsmParseUtil.parseMaxSpeed(tagToValue.get(OsmParseUtil.MAXSPEED_TAG));

    return new RoadEdge<>(way.getId(), source, destination, type, maxSpeed);
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

  @Override
  public void complete() {
    // Spatial data of nodes are now known, update all edge costs
    mGraph.getEdges().forEach(RoadEdge::updateCost);
  }

}
