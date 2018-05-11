package de.tischner.cobweb.routing.model.graph;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link BasicNode}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class BasicNodeTest {

  /**
   * The node used for testing.
   */
  private BasicNode mNode;

  /**
   * Setups a node instance for testing.
   */
  @Before
  public void setUp() {
    mNode = new BasicNode(1L);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.BasicNode#BasicNode(long)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testBasicNode() {
    try {
      new BasicNode(1L);
      new BasicNode(0L);
      new BasicNode(-1L);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.BasicNode#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mNode, new BasicNode(1L));
    Assert.assertNotEquals(mNode, new BasicNode(2L));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.BasicNode#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(1L, mNode.getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.BasicNode#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mNode.hashCode(), new BasicNode(1L).hashCode());
    Assert.assertNotEquals(mNode.hashCode(), new BasicNode(2L).hashCode());
  }

}
