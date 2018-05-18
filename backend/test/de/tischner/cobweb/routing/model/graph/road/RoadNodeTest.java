package de.tischner.cobweb.routing.model.graph.road;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link RoadNode}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RoadNodeTest {
  /**
   * The node used for testing.
   */
  private RoadNode mNode;

  /**
   * Setups a node instance for testing.
   */
  @Before
  public void setUp() {
    mNode = new RoadNode(1, 1.0F, 1.0F);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mNode, new RoadNode(1, 1.0F, 1.0F));
    Assert.assertNotEquals(mNode, new RoadNode(2, 1.0F, 1.0F));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(1, mNode.getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#getLatitude()}.
   */
  @Test
  public void testGetLatitude() {
    Assert.assertEquals(1.0F, mNode.getLatitude(), 0.0001F);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#getLongitude()}.
   */
  @Test
  public void testGetLongitude() {
    Assert.assertEquals(1.0F, mNode.getLongitude(), 0.0001F);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mNode.hashCode(), new RoadNode(1, 1.0F, 1.0F).hashCode());
    Assert.assertNotEquals(mNode.hashCode(), new RoadNode(2, 1.0F, 1.0F).hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#RoadNode(int, float, float)}.
   */
  @SuppressWarnings({ "static-method", "unused" })
  @Test
  public void testRoadNode() {
    try {
      new RoadNode(1, 1.0F, 1.0F);
      new RoadNode(0, 0.0F, 0.0F);
      new RoadNode(-1, -1.0F, -1.0F);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#setLatitude(float)}.
   */
  @Test
  public void testSetLatitude() {
    Assert.assertEquals(1.0F, mNode.getLatitude(), 0.0001F);
    mNode.setLatitude(2.0F);
    Assert.assertEquals(2.0F, mNode.getLatitude(), 0.0001F);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#setLongitude(float)}.
   */
  @Test
  public void testSetLongitude() {
    Assert.assertEquals(1.0F, mNode.getLongitude(), 0.0001F);
    mNode.setLongitude(2.0F);
    Assert.assertEquals(2.0F, mNode.getLongitude(), 0.0001F);
  }

}
