package de.tischner.cobweb.routing.algorithms.shortestpath;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.BasicEdge;
import de.tischner.cobweb.routing.model.graph.BasicNode;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;

/**
 * Test for the class {@link EmptyPath}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class EmptyPathTest {

  /**
   * The empty path used for testing.
   */
  private EmptyPath<BasicNode, BasicEdge<BasicNode>> mPath;

  /**
   * Setups an empty path instance for testing.
   */
  @Before
  public void setUp() {
    mPath = new EmptyPath<>(new BasicNode(1));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EmptyPath#EmptyPath(de.tischner.cobweb.routing.model.graph.INode)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testEmptyPath() {
    try {
      new EmptyPath<>(new RoadNode(1, 1.0F, 1.0F));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EmptyPath#getDestination()}.
   */
  @Test
  public void testGetDestination() {
    Assert.assertEquals(1, mPath.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EmptyPath#getSource()}.
   */
  @Test
  public void testGetSource() {
    Assert.assertEquals(1, mPath.getSource().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EmptyPath#getTotalCost()}.
   */
  @Test
  public void testGetTotalCost() {
    Assert.assertEquals(0.0, mPath.getTotalCost(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EmptyPath#iterator()}.
   */
  @Test
  public void testIterator() {
    Assert.assertFalse(mPath.iterator().hasNext());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EmptyPath#length()}.
   */
  @Test
  public void testLength() {
    Assert.assertEquals(0, mPath.length());
  }

}
