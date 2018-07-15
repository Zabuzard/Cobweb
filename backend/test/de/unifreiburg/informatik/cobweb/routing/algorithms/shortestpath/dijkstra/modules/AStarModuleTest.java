package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import org.junit.Assert;
import org.junit.Test;

import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric;
import de.unifreiburg.informatik.cobweb.routing.model.graph.road.RoadNode;

/**
 * Test for the class {@link AStarModule}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class AStarModuleTest {

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules.AStarModule#AStarModule(de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.IMetric)}.
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
