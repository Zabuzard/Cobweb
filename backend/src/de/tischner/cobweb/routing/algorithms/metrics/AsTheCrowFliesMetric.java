package de.tischner.cobweb.routing.algorithms.metrics;

import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.road.ISpatial;

public class AsTheCrowFliesMetric<N extends INode & ISpatial> implements IMetric<N> {

  @Override
  public double distance(final N first, final N second) {
    // TODO Compute the distance between both coordinates, use average road speed
    return 0.0;
  }

}
