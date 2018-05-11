package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra;

import org.junit.Assert;
import org.junit.Test;

import de.tischner.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric;
import de.tischner.cobweb.routing.model.graph.road.RoadGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;

/**
 * Test for the class {@link AStar}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class AStarTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.AStar#AStar(de.tischner.cobweb.routing.model.graph.IGraph, de.tischner.cobweb.routing.algorithms.metrics.IMetric)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testAStar() {
    try {
      new AStar<>(new RoadGraph<>(), new AsTheCrowFliesMetric<RoadNode>());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
