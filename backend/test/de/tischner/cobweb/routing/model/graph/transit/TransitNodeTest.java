package de.tischner.cobweb.routing.model.graph.transit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link TransitNode}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TransitNodeTest {
  /**
   * The node used for testing.
   */
  private TransitNode mNode;

  /**
   * Setups a node instance for testing.
   */
  @Before
  public void setUp() {
    mNode = new TransitNode(1, 1.0F, 1.0F, 1);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitNode#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mNode, new TransitNode(1, 1.0F, 1.0F, 1));
    Assert.assertNotEquals(mNode, new TransitNode(2, 1.0F, 1.0F, 1));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitNode#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(1, mNode.getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitNode#getLatitude()}.
   */
  @Test
  public void testGetLatitude() {
    Assert.assertEquals(1.0F, mNode.getLatitude(), 0.0001F);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitNode#getLongitude()}.
   */
  @Test
  public void testGetLongitude() {
    Assert.assertEquals(1.0F, mNode.getLongitude(), 0.0001F);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitNode#getTime()}.
   */
  @Test
  public void testGetTime() {
    Assert.assertEquals(1, mNode.getTime());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitNode#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mNode.hashCode(), new TransitNode(1, 1.0F, 1.0F, 1).hashCode());
    Assert.assertNotEquals(mNode.hashCode(), new TransitNode(2, 1.0F, 1.0F, 1).hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitNode#setLatitude(float)}.
   */
  @Test
  public void testSetLatitude() {
    Assert.assertEquals(1.0F, mNode.getLatitude(), 0.0001F);
    mNode.setLatitude(2.0F);
    Assert.assertEquals(2.0F, mNode.getLatitude(), 0.0001F);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitNode#setLongitude(float)}.
   */
  @Test
  public void testSetLongitude() {
    Assert.assertEquals(1.0F, mNode.getLongitude(), 0.0001F);
    mNode.setLongitude(2.0F);
    Assert.assertEquals(2.0F, mNode.getLongitude(), 0.0001F);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitNode#TransitNode(int, float, float, int)}.
   */
  @SuppressWarnings({ "static-method", "unused" })
  @Test
  public void testTransitNode() {
    try {
      new TransitNode(1, 1.0F, 1.0F, 1);
      new TransitNode(0, 0.0F, 0.0F, 0);
      new TransitNode(-1, -1.0F, -1.0F, -1);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
