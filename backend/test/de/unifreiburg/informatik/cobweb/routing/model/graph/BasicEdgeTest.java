package de.unifreiburg.informatik.cobweb.routing.model.graph;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link BasicEdge}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class BasicEdgeTest {

  /**
   * The edge used for testing.
   */
  private BasicEdge<BasicNode> mEdge;

  /**
   * Setups an edge instance for testing.
   */
  @Before
  public void setUp() {
    mEdge = new BasicEdge<>(1, new BasicNode(1), new BasicNode(2), 1.0);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicEdge#BasicEdge(int, de.unifreiburg.informatik.cobweb.routing.model.graph.INode, de.unifreiburg.informatik.cobweb.routing.model.graph.INode, double)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testBasicEdge() {
    try {
      new BasicEdge<>(1, new BasicNode(1), new BasicNode(2), 1.0);
      new BasicEdge<>(1, new BasicNode(1), new BasicNode(2), 0.0);
      new BasicEdge<>(1, new BasicNode(1), new BasicNode(2), -1.0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicEdge#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    BasicEdge<BasicNode> other = new BasicEdge<>(1, new BasicNode(2), new BasicNode(3), 3.0);
    Assert.assertEquals(mEdge, other);

    other = new BasicEdge<>(2, new BasicNode(1), new BasicNode(2), 1.0);
    Assert.assertNotEquals(mEdge, other);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicEdge#getCost()}.
   */
  @Test
  public void testGetCost() {
    Assert.assertEquals(1.0, mEdge.getCost(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicEdge#getDestination()}.
   */
  @Test
  public void testGetDestination() {
    Assert.assertEquals(2, mEdge.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicEdge#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(1, mEdge.getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicEdge#getSource()}.
   */
  @Test
  public void testGetSource() {
    Assert.assertEquals(1, mEdge.getSource().getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicEdge#hashCode()}.
   */
  @Test
  public void testHashCode() {
    BasicEdge<BasicNode> other = new BasicEdge<>(1, new BasicNode(2), new BasicNode(3), 3.0);
    Assert.assertEquals(mEdge.hashCode(), other.hashCode());

    other = new BasicEdge<>(2, new BasicNode(1), new BasicNode(2), 1.0);
    Assert.assertNotEquals(mEdge.hashCode(), other.hashCode());
  }

}
