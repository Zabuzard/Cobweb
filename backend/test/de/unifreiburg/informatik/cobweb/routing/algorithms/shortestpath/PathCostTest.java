package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link PathCost}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class PathCostTest {
  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.PathCost#getPathCost()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetPathCost() {
    Assert.assertEquals(10.0, new PathCost(10.0).getPathCost(), 0.0001);
    Assert.assertEquals(0.0, new PathCost(0.0).getPathCost(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.PathCost#PathCost(double)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testPathCost() {
    try {
      new PathCost(10.0);
      new PathCost(0.0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
