package de.tischner.cobweb.routing.model.graph.transit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.BasicNode;

/**
 * Test for the class {@link NodeTime}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NodeTimeTest {
  /**
   * The highway data used for testing.
   */
  private NodeTime<BasicNode> mNodeTime;

  /**
   * Setups a node time instance for testing.
   */
  @Before
  public void setUp() {
    mNodeTime = new NodeTime<>(new BasicNode(1), 1);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.NodeTime#compareTo(de.tischner.cobweb.routing.model.graph.transit.NodeTime)}.
   */
  @Test
  public void testCompareTo() {
    final NodeTime<BasicNode> other = new NodeTime<>(new BasicNode(2), 2);
    Assert.assertEquals(0, mNodeTime.compareTo(mNodeTime));
    Assert.assertTrue(mNodeTime.compareTo(other) < 0);
    Assert.assertTrue(other.compareTo(mNodeTime) > 0);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.NodeTime#getNode()}.
   */
  @Test
  public void testGetNode() {
    Assert.assertEquals(1, mNodeTime.getNode().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.NodeTime#getTime()}.
   */
  @Test
  public void testGetTime() {
    Assert.assertEquals(1, mNodeTime.getTime());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.NodeTime#NodeTime(de.tischner.cobweb.routing.model.graph.INode, int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNodeTime() {
    try {
      new NodeTime<>(new BasicNode(1), 1);
      new NodeTime<>(new BasicNode(0), 0);
      new NodeTime<>(null, -1);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.NodeTime#setTime(int)}.
   */
  @Test
  public void testSetTime() {
    mNodeTime.setTime(2);
    Assert.assertEquals(2, mNodeTime.getTime());
  }

}
