package de.tischner.cobweb.routing.algorithms.metrics;

import org.junit.Assert;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.road.RoadNode;

/**
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class AsTheCrowFliesMetricTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric#distance(de.tischner.cobweb.routing.model.graph.road.ISpatial, de.tischner.cobweb.routing.model.graph.road.ISpatial)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public final void testDistance() {
    final AsTheCrowFliesMetric<RoadNode> metric = new AsTheCrowFliesMetric<>();
    final RoadNode first = new RoadNode(1, 10.0F, 10.0F);
    final RoadNode second = new RoadNode(2, 20.0F, 20.0F);

    Assert.assertEquals(0.0, metric.distance(first, first), 0.0001);
    Assert.assertEquals(0.0, metric.distance(second, second), 0.0001);
    Assert.assertNotEquals(0.0, metric.distance(first, second), 0.0001);
  }

}
