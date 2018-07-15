package de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitNode;

/**
 * Test for the class {@link CoverTree}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class CoverTreeTest {
  /**
   * The tree used for testing.
   */
  private CoverTree<TransitNode> mTree;

  /**
   * Setups a tree instance for testing.
   */
  @Before
  public void setUp() {
    mTree = new CoverTree<>(new AsTheCrowFliesMetric<>());
    mTree.insert(new TransitNode(1, 1.0F, 1.0F, 1));
    mTree.insert(new TransitNode(2, 2.0F, 2.0F, 2));
    mTree.insert(new TransitNode(3, 3.0F, 3.0F, 3));
    mTree.insert(new TransitNode(4, 4.0F, 4.0F, 4));
    mTree.insert(new TransitNode(5, 5.0F, 5.0F, 5));
    mTree.insert(new TransitNode(6, 6.0F, 6.0F, 6));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#CoverTree(de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.IMetric)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testCoverTreeIMetricOfE() {
    // TODO Test all public methods of the class
    try {
      new CoverTree<>(new AsTheCrowFliesMetric<>());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#getNearestNeighbor(de.unifreiburg.informatik.cobweb.routing.model.graph.ISpatial)}.
   */
  @Test
  public void testGetNearestNeighbor() {
    Assert.assertEquals(1, mTree.getNearestNeighbor(new TransitNode(-1, 1.0F, 1.0F, 1)).get().getId());
    Assert.assertEquals(1, mTree.getNearestNeighbor(new TransitNode(-1, 1.1F, 1.2F, 1)).get().getId());
    Assert.assertEquals(5, mTree.getNearestNeighbor(new TransitNode(-1, 4.9F, 4.9F, 1)).get().getId());
    Assert.assertEquals(6, mTree.getNearestNeighbor(new TransitNode(-1, 10.0F, 6.0F, 1)).get().getId());

    Assert.assertFalse(new CoverTree<>(new AsTheCrowFliesMetric<>())
        .getNearestNeighbor(new TransitNode(-1, 1.0F, 1.0F, 1)).isPresent());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#insert(de.unifreiburg.informatik.cobweb.routing.model.graph.ISpatial)}.
   */
  @Test
  public void testInsertE() {
    Assert.assertEquals(6, mTree.size());
    mTree.insert(new TransitNode(7, 7.0F, 7.0F, 7));
    Assert.assertEquals(7, mTree.size());
    mTree.insert(new TransitNode(8, 8.0F, 8.0F, 8));
    Assert.assertEquals(8, mTree.size());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#setBounds(float, float, float, float)}.
   */
  @Test
  public void testSetBounds() {
    mTree.setBounds(0.0F, 0.0F, 10.0F, 10.0F);
    mTree.insert(new TransitNode(7, 11.0F, 7.0F, 7));
    Assert.assertEquals(6, mTree.size());
    mTree.insert(new TransitNode(8, 8.0F, -20.0F, 8));
    Assert.assertEquals(6, mTree.size());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#size()}.
   */
  @Test
  public void testSize() {
    Assert.assertEquals(6, mTree.size());
    mTree.insert(new TransitNode(7, 7.0F, 7.0F, 7));
    Assert.assertEquals(7, mTree.size());
    mTree.insert(new TransitNode(8, 8.0F, 8.0F, 8));
    Assert.assertEquals(8, mTree.size());
  }

}
