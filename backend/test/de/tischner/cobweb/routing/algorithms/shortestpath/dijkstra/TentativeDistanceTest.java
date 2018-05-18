package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.BasicEdge;
import de.tischner.cobweb.routing.model.graph.BasicNode;

/**
 * Test for the class {@link TentativeDistance}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TentativeDistanceTest {

  /**
   * The tentative distance used for testing.
   */
  private TentativeDistance<BasicNode, BasicEdge<BasicNode>> mElement;

  /**
   * Setups a tentative distance instance for testing.
   */
  @Before
  public void setUp() {
    final BasicNode first = new BasicNode(1);
    final BasicNode second = new BasicNode(2);
    mElement = new TentativeDistance<>(second, new BasicEdge<>(1, first, second, 1.0), 1.0, 3.0);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance#compareTo(de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance)}.
   */
  @Test
  public void testCompareTo() {
    TentativeDistance<BasicNode, BasicEdge<BasicNode>> other =
        new TentativeDistance<>(new BasicNode(3), null, 2.0, 1.0);
    Assert.assertTrue(mElement.compareTo(other) > 0);
    Assert.assertTrue(other.compareTo(mElement) < 0);

    other = new TentativeDistance<>(new BasicNode(3), null, 2.0, 2.0);

    Assert.assertEquals(0, mElement.compareTo(other));
    Assert.assertEquals(0, other.compareTo(mElement));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance#getEstimatedDistance()}.
   */
  @Test
  public void testGetEstimatedDistance() {
    Assert.assertEquals(3.0, mElement.getEstimatedDistance(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance#getNode()}.
   */
  @Test
  public void testGetNode() {
    Assert.assertEquals(2, mElement.getNode().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance#getParentEdge()}.
   */
  @Test
  public void testGetParentEdge() {
    Assert.assertEquals(1L, mElement.getParentEdge().getId());
    Assert.assertNull(new TentativeDistance<>(new BasicNode(3), null, 0.0, 3.0).getParentEdge());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance#getPathCost()}.
   */
  @Test
  public void testGetPathCost() {
    Assert.assertEquals(1.0, mElement.getPathCost(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance#getTentativeDistance()}.
   */
  @Test
  public void testGetTentativeDistance() {
    Assert.assertEquals(1.0, mElement.getTentativeDistance(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance#TentativeDistance(de.tischner.cobweb.routing.model.graph.INode, de.tischner.cobweb.routing.model.graph.IEdge, double)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTentativeDistanceNEDouble() {
    final BasicNode first = new BasicNode(1);
    final BasicNode second = new BasicNode(2);
    try {
      new TentativeDistance<>(second, new BasicEdge<>(1, first, second, 1.0), 1.0);
      new TentativeDistance<>(second, null, 0.0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance#TentativeDistance(de.tischner.cobweb.routing.model.graph.INode, de.tischner.cobweb.routing.model.graph.IEdge, double, double)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTentativeDistanceNEDoubleDouble() {
    final BasicNode first = new BasicNode(1);
    final BasicNode second = new BasicNode(2);
    try {
      new TentativeDistance<>(second, new BasicEdge<>(1, first, second, 1.0), 1.0, 3.0);
      new TentativeDistance<>(second, null, 0.0, 3.0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
