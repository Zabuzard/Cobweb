package de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric;
import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.IMetric;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IHasId;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitNode;

/**
 * Test for the class {@link CoverTree}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class CoverTreeTest {
  /**
   * The first tree used for testing.
   */
  private CoverTree<TransitNode> mTreeFirst;
  /**
   * The second tree used for testing.
   */
  private CoverTree<TransitNode> mTreeSecond;

  /**
   * Setups a tree instance for testing.
   */
  @Before
  public void setUp() {
    mTreeFirst = new CoverTree<>(new AsTheCrowFliesMetric<>());
    mTreeFirst.insert(new TransitNode(1, 1.0F, 1.0F, 1));
    mTreeFirst.insert(new TransitNode(2, 2.0F, 2.0F, 2));
    mTreeFirst.insert(new TransitNode(3, 3.0F, 3.0F, 3));
    mTreeFirst.insert(new TransitNode(4, 4.0F, 4.0F, 4));
    mTreeFirst.insert(new TransitNode(5, 5.0F, 5.0F, 5));
    mTreeFirst.insert(new TransitNode(6, 6.0F, 6.0F, 6));

    // Interpret coordinates as 2D euclidean space
    final IMetric<TransitNode> metric = (first, second) -> {
      final float xDiff = second.getLatitude() - first.getLatitude();
      final float yDiff = second.getLongitude() - first.getLongitude();
      return Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
    };
    // Use the modified metric, with a fixed base of 2
    mTreeSecond = new CoverTree<>(2.0, metric);
    mTreeSecond.insert(new TransitNode(1, 50.0F, 50.0F, 1));
    mTreeSecond.insert(new TransitNode(2, 30.0F, 30.0F, 1));
    mTreeSecond.insert(new TransitNode(3, 30.0F, 70.0F, 1));
    mTreeSecond.insert(new TransitNode(4, 70.0F, 30.0F, 1));
    mTreeSecond.insert(new TransitNode(5, 70.0F, 70.0F, 1));
    mTreeSecond.insert(new TransitNode(6, 30.0F, 15.0F, 1));
    mTreeSecond.insert(new TransitNode(7, 20.0F, 30.0F, 1));
    mTreeSecond.insert(new TransitNode(8, 70.0F, 15.0F, 1));
    mTreeSecond.insert(new TransitNode(9, 85.0F, 30.0F, 1));
    mTreeSecond.insert(new TransitNode(10, 20.0F, 70.0F, 1));
    mTreeSecond.insert(new TransitNode(11, 10.0F, 80.0F, 1));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#CoverTree(de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.IMetric)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testCoverTreeIMetricOfE() {
    try {
      new CoverTree<>(new AsTheCrowFliesMetric<>());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#getCover(int)}.
   */
  @Test
  public void testGetCover() {
    Assert.assertEquals(Stream.of(1).collect(Collectors.toSet()),
        mTreeSecond.getCover(6).stream().map(IHasId::getId).collect(Collectors.toSet()));

    Assert.assertEquals(Stream.of(11, 1).collect(Collectors.toSet()),
        mTreeSecond.getCover(5).stream().map(IHasId::getId).collect(Collectors.toSet()));

    Assert.assertEquals(Stream.of(11, 1, 2, 3, 4, 5).collect(Collectors.toSet()),
        mTreeSecond.getCover(4).stream().map(IHasId::getId).collect(Collectors.toSet()));

    final Set<Integer> allElements = Stream.of(11, 1, 2, 6, 7, 3, 10, 4, 8, 9, 5).collect(Collectors.toSet());
    Assert.assertEquals(allElements, mTreeSecond.getCover(3).stream().map(IHasId::getId).collect(Collectors.toSet()));
    Assert.assertEquals(allElements, mTreeSecond.getCover(2).stream().map(IHasId::getId).collect(Collectors.toSet()));
    Assert.assertEquals(allElements, mTreeSecond.getCover(1).stream().map(IHasId::getId).collect(Collectors.toSet()));
    Assert.assertEquals(allElements, mTreeSecond.getCover(0).stream().map(IHasId::getId).collect(Collectors.toSet()));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#getNearestNeighbor(de.unifreiburg.informatik.cobweb.routing.model.graph.ISpatial)}.
   */
  @Test
  public void testGetNearestNeighbor() {
    Assert.assertEquals(1, mTreeFirst.getNearestNeighbor(new TransitNode(-1, 1.0F, 1.0F, 1)).get().getId());
    Assert.assertEquals(1, mTreeFirst.getNearestNeighbor(new TransitNode(-1, 1.1F, 1.2F, 1)).get().getId());
    Assert.assertEquals(5, mTreeFirst.getNearestNeighbor(new TransitNode(-1, 4.9F, 4.9F, 1)).get().getId());
    Assert.assertEquals(6, mTreeFirst.getNearestNeighbor(new TransitNode(-1, 10.0F, 6.0F, 1)).get().getId());

    Assert.assertEquals(10, mTreeSecond.getNearestNeighbor(new TransitNode(-1, 20.0F, 70.0F, 1)).get().getId());
    Assert.assertEquals(10, mTreeSecond.getNearestNeighbor(new TransitNode(-1, 24.0F, 70.0F, 1)).get().getId());
    Assert.assertEquals(3, mTreeSecond.getNearestNeighbor(new TransitNode(-1, 26.0F, 70.0F, 1)).get().getId());
    Assert.assertEquals(6, mTreeSecond.getNearestNeighbor(new TransitNode(-1, 0.0F, 0.0F, 1)).get().getId());

    Assert.assertFalse(new CoverTree<>(new AsTheCrowFliesMetric<>())
        .getNearestNeighbor(new TransitNode(-1, 1.0F, 1.0F, 1)).isPresent());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#getNeighborhood(de.unifreiburg.informatik.cobweb.routing.model.graph.ISpatial, double)}.
   */
  @Test
  public void testGetNeighborhood() {
    TransitNode point = new TransitNode(1, 20.0F, 70.0F, 1);
    Set<Integer> expected = new HashSet<>();

    expected.add(10);
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 0.1).stream().map(IHasId::getId).collect(Collectors.toSet()));
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 9.0).stream().map(IHasId::getId).collect(Collectors.toSet()));

    // 3 has a distance of 10
    expected.add(3);
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 10.0).stream().map(IHasId::getId).collect(Collectors.toSet()));
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 14.0).stream().map(IHasId::getId).collect(Collectors.toSet()));

    // 11 has a distance of 14.14
    expected.add(11);
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 15.0).stream().map(IHasId::getId).collect(Collectors.toSet()));
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 36.0).stream().map(IHasId::getId).collect(Collectors.toSet()));

    // 1 has a distance of 36.05
    expected.add(1);
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 37.0).stream().map(IHasId::getId).collect(Collectors.toSet()));

    // 7 has a distance of 40
    expected.add(7);
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 40.0).stream().map(IHasId::getId).collect(Collectors.toSet()));

    // 2 has a distance of 41.23
    expected.add(2);
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 42.0).stream().map(IHasId::getId).collect(Collectors.toSet()));

    // Farthest node is 9 with 76.32
    expected = new HashSet<>(Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }));
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 77.0).stream().map(IHasId::getId).collect(Collectors.toSet()));

    point = new TransitNode(1, 0.0F, 0.0F, 1);
    expected = new HashSet<>();
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 0.0).stream().map(IHasId::getId).collect(Collectors.toSet()));
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 33.0).stream().map(IHasId::getId).collect(Collectors.toSet()));

    // 6 has a distance of 33.54
    expected.add(6);
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 34.0).stream().map(IHasId::getId).collect(Collectors.toSet()));

    // 7 has a distance of 36.05
    expected.add(7);
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 37.0).stream().map(IHasId::getId).collect(Collectors.toSet()));

    // 2 has a distance of 42.42
    expected.add(2);
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 43.0).stream().map(IHasId::getId).collect(Collectors.toSet()));

    // Farthest is 5 with 98.99
    expected = new HashSet<>(Arrays.asList(new Integer[] { 1, 2, 3, 4, 6, 7, 8, 9, 10, 11 }));
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 98.0).stream().map(IHasId::getId).collect(Collectors.toSet()));
    expected.add(5);
    Assert.assertEquals(expected,
        mTreeSecond.getNeighborhood(point, 99.0).stream().map(IHasId::getId).collect(Collectors.toSet()));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#insert(de.unifreiburg.informatik.cobweb.routing.model.graph.ISpatial)}.
   */
  @Test
  public void testInsertE() {
    Assert.assertEquals(6, mTreeFirst.size());
    mTreeFirst.insert(new TransitNode(7, 7.0F, 7.0F, 7));
    Assert.assertEquals(7, mTreeFirst.size());
    mTreeFirst.insert(new TransitNode(8, 8.0F, 8.0F, 8));
    Assert.assertEquals(8, mTreeFirst.size());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#maxLevel()}.
   */
  @Test
  public void testMaxLevel() {
    Assert.assertEquals(6, mTreeSecond.maxLevel());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#minLevel()}.
   */
  @Test
  public void testMinLevel() {
    Assert.assertEquals(0, mTreeSecond.minLevel());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#setBounds(float, float, float, float)}.
   */
  @Test
  public void testSetBounds() {
    mTreeFirst.setBounds(0.0F, 0.0F, 10.0F, 10.0F);
    mTreeFirst.insert(new TransitNode(7, 11.0F, 7.0F, 7));
    Assert.assertEquals(6, mTreeFirst.size());
    mTreeFirst.insert(new TransitNode(8, 8.0F, -20.0F, 8));
    Assert.assertEquals(6, mTreeFirst.size());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree#size()}.
   */
  @Test
  public void testSize() {
    Assert.assertEquals(6, mTreeFirst.size());
    mTreeFirst.insert(new TransitNode(7, 7.0F, 7.0F, 7));
    Assert.assertEquals(7, mTreeFirst.size());
    mTreeFirst.insert(new TransitNode(8, 8.0F, 8.0F, 8));
    Assert.assertEquals(8, mTreeFirst.size());

    Assert.assertEquals(11, mTreeSecond.size());
  }
}
