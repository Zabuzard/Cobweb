package de.tischner.cobweb.routing.algorithms.metrics.landmark;

import org.junit.Assert;
import org.junit.Test;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;

/**
 * Test for the class {@link RandomLandmarks}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public class RandomLandmarksTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.metrics.landmark.RandomLandmarks#getLandmarks(int)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetLandmarks() {
    final RoadGraph<RoadNode, RoadEdge<RoadNode>> graph = new RoadGraph<>();
    final RoadNode first = new RoadNode(1L, 10.0, 10.0);
    final RoadNode second = new RoadNode(2L, 20.0, 20.0);
    final RoadNode third = new RoadNode(3L, 30.0, 30.0);
    final RoadNode fourth = new RoadNode(4L, 40.0, 40.0);
    graph.addNode(first);
    graph.addNode(second);
    graph.addNode(third);
    graph.addNode(fourth);
    graph.addEdge(new RoadEdge<>(1L, first, second, EHighwayType.MOTORWAY, 100));
    graph.addEdge(new RoadEdge<>(1L, second, third, EHighwayType.MOTORWAY, 100));
    graph.addEdge(new RoadEdge<>(1L, third, fourth, EHighwayType.MOTORWAY, 100));
    graph.addEdge(new RoadEdge<>(1L, fourth, first, EHighwayType.MOTORWAY, 100));

    final RandomLandmarks<RoadNode, RoadGraph<RoadNode, RoadEdge<RoadNode>>> landmarks = new RandomLandmarks<>(graph);

    Assert.assertEquals(0, landmarks.getLandmarks(0).size());
    Assert.assertEquals(1, landmarks.getLandmarks(1).size());
    Assert.assertEquals(4, landmarks.getLandmarks(4).size());
    Assert.assertEquals(4, landmarks.getLandmarks(10).size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.metrics.landmark.RandomLandmarks#RandomLandmarks(de.tischner.cobweb.routing.model.graph.IGraph)}.
   */
  @SuppressWarnings({ "static-method", "unused" })
  @Test
  public void testRandomLandmarks() {
    try {
      final RoadGraph<RoadNode, RoadEdge<RoadNode>> graph = new RoadGraph<>();
      new RandomLandmarks<>(graph);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
