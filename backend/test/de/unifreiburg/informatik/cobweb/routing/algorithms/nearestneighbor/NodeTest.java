package de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link Node}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NodeTest {
  /**
   * The node used for testing.
   */
  private Node<Integer> mNode;

  /**
   * Setups a node instance for testing.
   */
  @Before
  public void setUp() {
    mNode = new Node<>(new Node<>(null, 1), 2);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node#addChild(de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node)}.
   */
  @Test
  public void testAddChild() {
    final Node<Integer> firstChild = new Node<>(null, 3);
    mNode.addChild(firstChild);
    Assert.assertEquals(1, mNode.getChildren().size());
    Assert.assertTrue(mNode.getChildren().contains(firstChild));

    final Node<Integer> secondChild = new Node<>(null, 4);
    mNode.addChild(secondChild);
    Assert.assertEquals(2, mNode.getChildren().size());
    Assert.assertTrue(mNode.getChildren().contains(firstChild));
    Assert.assertTrue(mNode.getChildren().contains(secondChild));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node#compareTo(de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node)}.
   */
  @Test
  public void testCompareTo() {
    final Node<Integer> other = new Node<>(null, 3);
    mNode.setDistance(1.0);
    other.setDistance(1.0);
    Assert.assertEquals(0.0, mNode.compareTo(other), 0.0001);

    other.setDistance(2.0);
    Assert.assertTrue(mNode.compareTo(other) < 0);

    Assert.assertTrue(other.compareTo(mNode) > 0);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node#getChildren()}.
   */
  @Test
  public void testGetChildren() {
    final Node<Integer> firstChild = new Node<>(null, 3);
    mNode.addChild(firstChild);
    Assert.assertEquals(1, mNode.getChildren().size());
    Assert.assertTrue(mNode.getChildren().contains(firstChild));

    final Node<Integer> secondChild = new Node<>(null, 4);
    mNode.addChild(secondChild);
    Assert.assertEquals(2, mNode.getChildren().size());
    Assert.assertTrue(mNode.getChildren().contains(firstChild));
    Assert.assertTrue(mNode.getChildren().contains(secondChild));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node#getDistance()}.
   */
  @Test
  public void testGetDistance() {
    mNode.setDistance(1.0);
    Assert.assertEquals(1.0, mNode.getDistance(), 0.0001);

    mNode.setDistance(5.4);
    Assert.assertEquals(5.4, mNode.getDistance(), 0.0001);

    mNode.setDistance(0.0);
    Assert.assertEquals(0.0, mNode.getDistance(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node#getElement()}.
   */
  @Test
  public void testGetElement() {
    Assert.assertEquals(2, mNode.getElement().intValue());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node#getParent()}.
   */
  @Test
  public void testGetParent() {
    Assert.assertEquals(1, mNode.getParent().getElement().intValue());
    Assert.assertNull(mNode.getParent().getParent());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node#Node(de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node, java.lang.Object)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNode() {
    try {
      new Node<>(null, 1);
      new Node<>(null, -1);
      new Node<>(null, 0);
      new Node<>(new Node<>(null, 1), 2);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node#removeChild(de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node)}.
   */
  @Test
  public void testRemoveChild() {
    final Node<Integer> firstChild = new Node<>(null, 3);
    mNode.addChild(firstChild);
    Assert.assertEquals(1, mNode.getChildren().size());

    final Node<Integer> secondChild = new Node<>(null, 4);
    mNode.addChild(secondChild);
    Assert.assertEquals(2, mNode.getChildren().size());

    mNode.removeChild(firstChild);
    Assert.assertEquals(1, mNode.getChildren().size());
    Assert.assertTrue(mNode.getChildren().contains(secondChild));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node#removeChildren()}.
   */
  @Test
  public void testRemoveChildren() {
    final Node<Integer> firstChild = new Node<>(null, 3);
    mNode.addChild(firstChild);
    Assert.assertEquals(1, mNode.getChildren().size());

    final Node<Integer> secondChild = new Node<>(null, 4);
    mNode.addChild(secondChild);
    Assert.assertEquals(2, mNode.getChildren().size());

    mNode.removeChildren();
    Assert.assertFalse(mNode.getChildren().contains(firstChild));
    Assert.assertFalse(mNode.getChildren().contains(secondChild));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node#setDistance(double)}.
   */
  @Test
  public void testSetDistance() {
    mNode.setDistance(1.0);
    Assert.assertEquals(1.0, mNode.getDistance(), 0.0001);

    mNode.setDistance(5.4);
    Assert.assertEquals(5.4, mNode.getDistance(), 0.0001);

    mNode.setDistance(0.0);
    Assert.assertEquals(0.0, mNode.getDistance(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node#setParent(de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.Node)}.
   */
  @Test
  public void testSetParent() {
    final Node<Integer> other = new Node<>(null, 3);
    mNode.setParent(other);
    Assert.assertEquals(other, mNode.getParent());
  }

}
