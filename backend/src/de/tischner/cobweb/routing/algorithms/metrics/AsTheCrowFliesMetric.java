package de.tischner.cobweb.routing.algorithms.metrics;

import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.road.ISpatial;
import de.tischner.cobweb.util.RoutingUtil;

public class AsTheCrowFliesMetric<N extends INode & ISpatial> implements IMetric<N> {

  @Override
  public double distance(final N first, final N second) {
    final double distance = RoutingUtil.distanceEquiRect(first, second);
    final double maximalSpeed = RoutingUtil.maximalRoadSpeed();
    return RoutingUtil.travelTime(distance, maximalSpeed);
  }

}
