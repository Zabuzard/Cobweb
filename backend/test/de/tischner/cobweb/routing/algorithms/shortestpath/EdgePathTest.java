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

    final BasicNode firstNode = new BasicNode(1L);
    final BasicNode secondNode = new BasicNode(2L);
    final BasicNode thirdNode = new BasicNode(3L);
    final BasicNode fourthNode = new BasicNode(4L);

    if (reversely) {
      path.addEdge(new BasicEdge<>(1L, secondNode, firstNode, 1.0));
      path.addEdge(new BasicEdge<>(2L, thirdNode, secondNode, 2.0));
      path.addEdge(new BasicEdge<>(3L, fourthNode, thirdNode, 1.0));
    } else {
      path.addEdge(new BasicEdge<>(1L, firstNode, secondNode, 1.0));
      path.addEdge(new BasicEdge<>(2L, secondNode, thirdNode, 2.0));
      path.addEdge(new BasicEdge<>(3L, thirdNode, fourthNode, 1.0));
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
    mPath.addEdge(new BasicEdge<>(4L, new BasicNode(10L), new BasicNode(11L), 1.0));
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
    Assert.assertEquals(4L, mPath.getDestination().getId());
    final EdgePath<BasicNode, BasicEdge<BasicNode>> reversePath = EdgePathTest.buildPath(true);
    Assert.assertEquals(1L, reversePath.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath#getSource()}.
   */
  @Test
  public void testGetSource() {
    Assert.assertEquals(1L, mPath.getSource().getId());
    final EdgePath<BasicNode, BasicEdge<BasicNode>> reversePath = EdgePathTest.buildPath(true);
    Assert.assertEquals(4L, reversePath.getSource().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath#getTotalCost()}.
   */
  @Test
  public void testGetTotalCost() {
    Assert.assertEquals(4.0, mPath.getTotalCost(), 0.0001);

    mPath.addEdge(new BasicEdge<>(4L, new BasicNode(10L), new BasicNode(11L), 3.0));
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
    final List<Long> expectedIds = Arrays.asList(1L, 2L, 3L);
    final List<Long> ids =
        StreamSupport.stream(mPath.spliterator(), false).map(IHasId::getId).collect(Collectors.toList());
    Assert.assertEquals(expectedIds, ids);

    final List<Long> reverseExpectedIds = Arrays.asList(3L, 2L, 1L);
    final List<Long> reverseIds = StreamSupport.stream(EdgePathTest.buildPath(true).spliterator(), false)
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
    mPath.addEdge(new BasicEdge<>(4L, new BasicNode(10L), new BasicNode(11L), 1.0));
    Assert.assertEquals(4, mPath.length());
  }

}
