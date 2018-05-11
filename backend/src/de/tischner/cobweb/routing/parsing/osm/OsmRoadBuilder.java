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

/**
 * Implementation of an {@link IOsmRoadBuilder} which builds instances of
 * {@link RoadNode}s and {@link RoadEdge}s.<br>
 * <br>
 * Edges are build incomplete at first and are updated once {@link #complete()}
 * is called.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <G> The type of the graph which must be able to get nodes by their ID
 */
public final class OsmRoadBuilder<G extends IGraph<RoadNode, RoadEdge<RoadNode>> & ICanGetNodeById<RoadNode>>
    implements IOsmRoadBuilder<RoadNode, RoadEdge<RoadNode>> {
  /**
   * The graph to operate on.
   */
  private final G mGraph;

  /**
   * Creates a new OSM road builder which operates on the given graph.
   *
   * @param graph The graph to operate on
   */
  public OsmRoadBuilder(final G graph) {
    mGraph = graph;
  }

  /**
   * Builds an edge which connects the source and destination nodes with the
   * given unique IDs.<br>
   * <br>
   * The edge is build incomplete at first and are updated once
   * {@link #complete()} is called.
   */
  @Override
  public RoadEdge<RoadNode> buildEdge(final OsmWay way, final long sourceId, final long destinationId)
      throws ParseException {
    final RoadNode source = mGraph.getNodeById(sourceId).orElseThrow(() -> new ParseException());
    final RoadNode destination = mGraph.getNodeById(destinationId).orElseThrow(() -> new ParseException());

    // Get information about the highway type
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(way);
    final EHighwayType type = OsmParseUtil.parseHighwayType(tagToValue);
    final int maxSpeed = OsmParseUtil.parseMaxSpeed(tagToValue);

    return new RoadEdge<>(way.getId(), source, destination, type, maxSpeed);
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.parsing.osm.IOsmRoadBuilder#buildNode(de.
   * topobyte. osm4j.core.model.iface.OsmNode)
   */
  @Override
  public RoadNode buildNode(final OsmNode node) {
    return new RoadNode(node.getId(), node.getLatitude(), node.getLongitude());
  }

  /**
   * Callback to be used once construction has been finished.<br>
   * <br>
   * Will update all constructed edges since they are build incomplete at first.
   */
  @Override
  public void complete() {
    // Spatial data of nodes are now known, update all edge costs
    mGraph.getEdges().forEach(RoadEdge::updateCost);
  }

}
