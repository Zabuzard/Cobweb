package de.tischner.cobweb.routing.algorithms.shortestpath;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.BasicEdge;
import de.tischner.cobweb.routing.model.graph.BasicNode;
import de.tischner.cobweb.routing.model.graph.road.IHasId;

/**
 * Test for the class {@link EdgePath}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class EdgePathTest {

  /**
   * Builds a path instance for testing.
   *
   * @param reversely Whether or not the path should be built reversely
   * @return The constructed path
   */
  private static EdgePath<BasicNode, BasicEdge<BasicNode>> buildPath(final boolean reversely) {
    final EdgePath<BasicNode, BasicEdge<BasicNode>> path = new EdgePath<>(reversely);

    final BasicNode firstNode = new BasicNode(1);
    final BasicNode secondNode = new BasicNode(2);
    final BasicNode thirdNode = new BasicNode(3);
    final BasicNode fourthNode = new BasicNode(4);

    if (reversely) {
      path.addEdge(new BasicEdge<>(1, secondNode, firstNode, 1.0));
      path.addEdge(new BasicEdge<>(2, thirdNode, secondNode, 2.0));
      path.addEdge(new BasicEdge<>(3, fourthNode, thirdNode, 1.0));
    } else {
      path.addEdge(new BasicEdge<>(1, firstNode, secondNode, 1.0));
      path.addEdge(new BasicEdge<>(2, secondNode, thirdNode, 2.0));
      path.addEdge(new BasicEdge<>(3, thirdNode, fourthNode, 1.0));
    }

    return path;
  }

  /**
   * The edge path used for testing.
   */
  private EdgePath<BasicNode, BasicEdge<BasicNode>> mPath;

  /**
   * Setups an edge path instance for testing.
   */
  @Before
  public void setUp() {
    mPath = EdgePathTest.buildPath(false);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath#addEdge(de.tischner.cobweb.routing.model.graph.IEdge)}.
   */
  @Test
  public void testAddEdge() {
    Assert.assertEquals(3, mPath.length());
    mPath.addEdge(new BasicEdge<>(4, new BasicNode(10), new BasicNode(11), 1.0));
    Assert.assertEquals(4, mPath.length());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath#EdgePath()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testEdgePath() {
    try {
      new EdgePath<>();
    } catch (final Exception e) {
      Assert.fail();
    }
    Assert.assertFalse(new EdgePath<>().isBuildReversely());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath#EdgePath(boolean)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testEdgePathBoolean() {
    try {
      new EdgePath<>(true);
      new EdgePath<>(false);
    } catch (final Exception e) {
      Assert.fail();
    }
    Assert.assertFalse(new EdgePath<>(false).isBuildReversely());
    Assert.assertTrue(new EdgePath<>(true).isBuildReversely());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath#getDestination()}.
   */
  @Test
  public void testGetDestination() {
    Assert.assertEquals(4, mPath.getDestination().getId());
    final EdgePath<BasicNode, BasicEdge<BasicNode>> reversePath = EdgePathTest.buildPath(true);
    Assert.assertEquals(1, reversePath.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath#getSource()}.
   */
  @Test
  public void testGetSource() {
    Assert.assertEquals(1, mPath.getSource().getId());
    final EdgePath<BasicNode, BasicEdge<BasicNode>> reversePath = EdgePathTest.buildPath(true);
    Assert.assertEquals(4, reversePath.getSource().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath#getTotalCost()}.
   */
  @Test
  public void testGetTotalCost() {
    Assert.assertEquals(4.0, mPath.getTotalCost(), 0.0001);

    mPath.addEdge(new BasicEdge<>(4, new BasicNode(10), new BasicNode(11), 3.0));
    Assert.assertEquals(7.0, mPath.getTotalCost(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath#isBuildReversely()}.
   */
  @Test
  public void testIsBuildReversely() {
    Assert.assertFalse(mPath.isBuildReversely());
    Assert.assertTrue(new EdgePath<>(true).isBuildReversely());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath#iterator()}.
   */
  @Test
  public void testIterator() {
    final List<Integer> expectedIds = Arrays.asList(1, 2, 3);
    final List<Integer> ids =
        StreamSupport.stream(mPath.spliterator(), false).map(IHasId::getId).collect(Collectors.toList());
    Assert.assertEquals(expectedIds, ids);

    final List<Integer> reverseExpectedIds = Arrays.asList(3, 2, 1);
    final List<Integer> reverseIds = StreamSupport.stream(EdgePathTest.buildPath(true).spliterator(), false)
        .map(IHasId::getId).collect(Collectors.toList());
    Assert.assertEquals(reverseExpectedIds, reverseIds);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath#length()}.
   */
  @Test
  public void testLength() {
    Assert.assertEquals(3, mPath.length());
    mPath.addEdge(new BasicEdge<>(4, new BasicNode(10), new BasicNode(11), 1.0));
    Assert.assertEquals(4, mPath.length());
  }

}
