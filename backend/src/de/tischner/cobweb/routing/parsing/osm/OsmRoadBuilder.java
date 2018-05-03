package de.tischner.cobweb.routing.parsing.osm;

import java.util.Map;

import de.tischner.cobweb.parsing.ParseException;
import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.parsing.osm.OsmParseUtil;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.tischner.cobweb.util.RoutingUtil;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public final class OsmRoadBuilder<G extends IGraph<RoadNode, RoadEdge<RoadNode>> & ICanGetNodeById<RoadNode>>
    implements IOsmRoadBuilder<RoadNode, RoadEdge<RoadNode>> {

  private static double getSpeedOfWay(final OsmWay way) {
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(way);

    // Use the max speed property if present
    final Integer maxSpeed = OsmParseUtil.parseMaxSpeed(tagToValue.get(OsmParseUtil.MAXSPEED_TAG));
    if (maxSpeed != null) {
      return maxSpeed;
    }

    // Use the highway type if present
    final EHighwayType type = OsmParseUtil.parseHighwayType(tagToValue.get(OsmParseUtil.HIGHWAY_TAG));
    if (type != null) {
      return type.getAverageSpeed();
    }

    // Use a default speed value
    return EHighwayType.RESIDENTIAL.getAverageSpeed();
  }

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

    // Compute the cost
    final double speed = OsmRoadBuilder.getSpeedOfWay(way);
    final double distance = RoutingUtil.distanceEquiRect(source, destination);
    final double cost = RoutingUtil.travelTime(distance, speed);

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
