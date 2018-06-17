package de.tischner.cobweb.routing.model.graph;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link EdgeCost}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class EdgeCostTest {
  /**
   * The edge cost used for testing.
   */
  private EdgeCost<BasicNode, BasicEdge<BasicNode>> mEdgeCost;

  /**
   * Setups an edge cost instance for testing.
   */
  @Before
  public void setUp() {
    mEdgeCost = new EdgeCost<>(new BasicEdge<>(1, new BasicNode(1), new BasicNode(2), 2.0), 10.0);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.EdgeCost#EdgeCost(de.tischner.cobweb.routing.model.graph.IEdge, double)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testEdgeCost() {
    try {
      new EdgeCost<>(new BasicEdge<>(1, new BasicNode(1), new BasicNode(2), 2.0), 10.0);
      new EdgeCost<>(new BasicEdge<>(1, new BasicNode(1), new BasicNode(2), 2.0), 0.0);
      new EdgeCost<>(new BasicEdge<>(1, new BasicNode(1), new BasicNode(2), 10.0), 10.0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.EdgeCost#getCost()}.
   */
  @Test
  public void testGetCost() {
    Assert.assertEquals(10.0, mEdgeCost.getCost(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.EdgeCost#getEdge()}.
   */
  @Test
  public void testGetEdge() {
    Assert.assertEquals(1, mEdgeCost.getEdge().getId());
  }

}
