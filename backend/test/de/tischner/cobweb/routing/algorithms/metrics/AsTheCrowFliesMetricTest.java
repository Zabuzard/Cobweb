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
    final RoadNode first = new RoadNode(1L, 10.0, 10.0);
    final RoadNode second = new RoadNode(2L, 20.0, 20.0);

    Assert.assertEquals(0.0, metric.distance(first, first), 0.0001);
    Assert.assertEquals(0.0, metric.distance(second, second), 0.0001);
    Assert.assertNotEquals(0.0, metric.distance(first, second), 0.0001);
  }

}
