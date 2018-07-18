package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unifreiburg.informatik.cobweb.routing.model.graph.BasicEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.BasicNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.EdgeCost;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IHasId;

/**
 * Test for the class {@link TripletonPath}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TripletonPathTest {

  /**
   * The edge path used for testing.
   */
  private TripletonPath<BasicNode, BasicEdge<BasicNode>> mPath;

  /**
   * Setups an edge path instance for testing.
   */
  @Before
  public void setUp() {
    final EdgePath<BasicNode, BasicEdge<BasicNode>> first = new EdgePath<>();
    final EdgePath<BasicNode, BasicEdge<BasicNode>> second = new EdgePath<>();
    final EdgePath<BasicNode, BasicEdge<BasicNode>> third = new EdgePath<>();

    final BasicNode firstNode = new BasicNode(1);
    final BasicNode secondNode = new BasicNode(2);
    final BasicNode thirdNode = new BasicNode(3);
    final BasicNode fourthNode = new BasicNode(4);
    final BasicNode fifthNode = new BasicNode(5);
    final BasicNode sixthNode = new BasicNode(6);

    first.addEdge(new BasicEdge<>(1, firstNode, secondNode, 1.0), 1.0);
    first.addEdge(new BasicEdge<>(2, secondNode, thirdNode, 2.0), 2.0);

    second.addEdge(new BasicEdge<>(3, thirdNode, fourthNode, 1.0), 1.0);
    second.addEdge(new BasicEdge<>(4, fourthNode, fifthNode, 1.0), 1.0);

    third.addEdge(new BasicEdge<>(5, fifthNode, sixthNode, 1.0), 1.0);

    mPath = new TripletonPath<>(first, second, third);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.TripletonPath#getDestination()}.
   */
  @Test
  public void testGetDestination() {
    Assert.assertEquals(6, mPath.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.TripletonPath#getSource()}.
   */
  @Test
  public void testGetSource() {
    Assert.assertEquals(1, mPath.getSource().getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.TripletonPath#getTotalCost()}.
   */
  @Test
  public void testGetTotalCost() {
    Assert.assertEquals(6.0, mPath.getTotalCost(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.TripletonPath#iterator()}.
   */
  @Test
  public void testIterator() {
    final List<Integer> expectedIds = Arrays.asList(1, 2, 3, 4, 5);
    final List<Integer> ids = StreamSupport.stream(mPath.spliterator(), false).map(EdgeCost::getEdge).map(IHasId::getId)
        .collect(Collectors.toList());
    Assert.assertEquals(expectedIds, ids);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.TripletonPath#length()}.
   */
  @Test
  public void testLength() {
    Assert.assertEquals(5, mPath.length());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.TripletonPath#TripletonPath(de.unifreiburg.informatik.cobweb.routing.model.graph.IPath, de.unifreiburg.informatik.cobweb.routing.model.graph.IPath, de.unifreiburg.informatik.cobweb.routing.model.graph.IPath)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTripletonPath() {
    try {
      new TripletonPath<>(null, null, null);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
