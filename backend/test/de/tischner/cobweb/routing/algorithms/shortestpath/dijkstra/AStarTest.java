package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra;

import org.junit.Assert;
import org.junit.Test;

import de.tischner.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.AStarModule;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;

/**
 * Test for the class {@link AStarModule}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class AStarTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.AStarModule#AStarModule(de.tischner.cobweb.routing.algorithms.metrics.IMetric)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testAStar() {
    try {
      new AStarModule<>(new AsTheCrowFliesMetric<RoadNode>());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
