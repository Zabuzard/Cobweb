package de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.landmark;

import org.junit.Assert;
import org.junit.Test;

import de.unifreiburg.informatik.cobweb.routing.model.graph.BasicEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.BasicGraph;
import de.unifreiburg.informatik.cobweb.routing.model.graph.BasicNode;

/**
 * Test for the class {@link GreedyFarthestLandmarks}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class GreedyFarthestLandmarksTest {

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.landmark.GreedyFarthestLandmarks#getLandmarks(int)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetLandmarks() {
    final BasicGraph graph = new BasicGraph();
    final BasicNode first = new BasicNode(1);
    final BasicNode second = new BasicNode(2);
    final BasicNode third = new BasicNode(3);
    final BasicNode fourth = new BasicNode(4);
    graph.addNode(first);
    graph.addNode(second);
    graph.addNode(third);
    graph.addNode(fourth);
    graph.addEdge(new BasicEdge<>(1, first, second, 1.0));
    graph.addEdge(new BasicEdge<>(1, second, third, 1.0));
    graph.addEdge(new BasicEdge<>(1, third, fourth, 1.0));
    graph.addEdge(new BasicEdge<>(1, fourth, first, 1.0));

    final GreedyFarthestLandmarks<BasicNode, BasicEdge<BasicNode>, BasicGraph> landmarks =
        new GreedyFarthestLandmarks<>(graph);

    Assert.assertEquals(0, landmarks.getLandmarks(0).size());
    Assert.assertEquals(1, landmarks.getLandmarks(1).size());
    Assert.assertEquals(4, landmarks.getLandmarks(4).size());
    Assert.assertEquals(4, landmarks.getLandmarks(10).size());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.landmark.GreedyFarthestLandmarks#GreedyFarthestLandmarks(de.unifreiburg.informatik.cobweb.routing.model.graph.IGraph)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testGreedyFarthestLandmarks() {
    try {
      new GreedyFarthestLandmarks<>(new BasicGraph());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
