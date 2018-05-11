package de.tischner.cobweb.routing.algorithms.metrics.landmark;

import org.junit.Assert;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.BasicEdge;
import de.tischner.cobweb.routing.model.graph.BasicGraph;
import de.tischner.cobweb.routing.model.graph.BasicNode;

/**
 * Test for the class {@link RandomLandmarks}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public class RandomLandmarksTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.metrics.landmark.RandomLandmarks#getLandmarks(int)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetLandmarks() {
    final BasicGraph graph = new BasicGraph();
    final BasicNode first = new BasicNode(1L);
    final BasicNode second = new BasicNode(2L);
    final BasicNode third = new BasicNode(3L);
    final BasicNode fourth = new BasicNode(4L);
    graph.addNode(first);
    graph.addNode(second);
    graph.addNode(third);
    graph.addNode(fourth);
    graph.addEdge(new BasicEdge<>(1L, first, second, 1.0));
    graph.addEdge(new BasicEdge<>(1L, second, third, 1.0));
    graph.addEdge(new BasicEdge<>(1L, third, fourth, 1.0));
    graph.addEdge(new BasicEdge<>(1L, fourth, first, 1.0));

    final RandomLandmarks<BasicNode, BasicGraph> landmarks = new RandomLandmarks<>(graph);

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
      new RandomLandmarks<>(new BasicGraph());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
