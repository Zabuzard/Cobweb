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
    mNode = new RoadNode(1L, 1.0, 1.0);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mNode, new RoadNode(1L, 1.0, 1.0));
    Assert.assertNotEquals(mNode, new RoadNode(2L, 1.0, 1.0));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(1L, mNode.getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#getLatitude()}.
   */
  @Test
  public void testGetLatitude() {
    Assert.assertEquals(1.0, mNode.getLatitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#getLongitude()}.
   */
  @Test
  public void testGetLongitude() {
    Assert.assertEquals(1.0, mNode.getLongitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mNode.hashCode(), new RoadNode(1L, 1.0, 1.0).hashCode());
    Assert.assertNotEquals(mNode.hashCode(), new RoadNode(2L, 1.0, 1.0).hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#RoadNode(long, double, double)}.
   */
  @SuppressWarnings({ "static-method", "unused" })
  @Test
  public void testRoadNode() {
    try {
      new RoadNode(1L, 1.0, 1.0);
      new RoadNode(0L, 0.0, 0.0);
      new RoadNode(-1L, -1.0, -1.0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#setLatitude(double)}.
   */
  @Test
  public void testSetLatitude() {
    Assert.assertEquals(1.0, mNode.getLatitude(), 0.0001);
    mNode.setLatitude(2.0);
    Assert.assertEquals(2.0, mNode.getLatitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadNode#setLongitude(double)}.
   */
  @Test
  public void testSetLongitude() {
    Assert.assertEquals(1.0, mNode.getLongitude(), 0.0001);
    mNode.setLongitude(2.0);
    Assert.assertEquals(2.0, mNode.getLongitude(), 0.0001);
  }

}
