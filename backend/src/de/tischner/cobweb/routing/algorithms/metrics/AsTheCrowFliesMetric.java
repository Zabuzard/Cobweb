package de.tischner.cobweb.routing.algorithms.metrics;

import de.tischner.cobweb.routing.model.graph.ISpatial;
import de.tischner.cobweb.util.RoutingUtil;

/**
 * Implements the <i>as-the-crow-flies</i> metric for {@link ISpatial}
 * objects.<br>
 * <br>
 * Given two objects it computes the direct, straight-line, distance of both
 * objects based on their coordinates. The distance is measured in
 * <tt>metres</tt>.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of objects the metric operates on, must implement
 *        {@link ISpatial}
 */
public final class AsTheCrowFliesMetric<N extends ISpatial> implements IMetric<N> {

  /**
   * The distance between both given objects, measured in <tt>metres</tt>.
   */
  @Override
  public double distance(final N first, final N second) {
    final double distance = RoutingUtil.distanceEquiRect(first, second);
    final double maximalSpeed = RoutingUtil.maximalRoadSpeed();
    return RoutingUtil.travelTime(distance, maximalSpeed);
  }

}
