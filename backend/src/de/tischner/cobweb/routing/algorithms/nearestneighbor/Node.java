package de.tischner.cobweb.routing.algorithms.nearestneighbor;

import java.util.List;

import org.eclipse.collections.impl.list.mutable.FastList;

/**
 * Node class for a {@link CoverTree}.
 *
 * @author Nils Loehndorf
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> Type of the elements contained by the node
 */
public final class Node<E> implements Comparable<Node<E>> {
  /**
   * The children of the node.
   */
  private final List<Node<E>> mChildren;
  /**
   * The current distance of the node.
   */
  private double mDistance;
  /**
   * The element contained in the node.
   */
  private final E mElement;
  /**
   * The parent of the node.
   */
  private Node<E> mParent;

  /**
   * Creates a new node with a given parent and element.
   *
   * @param parent  The parent of the node
   * @param element The element to wrap around
   */
  public Node(final Node<E> parent, final E element) {
    mParent = parent;
    mChildren = FastList.newList();
    mElement = element;
  }

  /**
   * Adds the given child to this node.
   *
   * @param child The child to add
   */
  public void addChild(final Node<E> child) {
    mChildren.add(child);
  }

  /**
   * Compares nodes ascending based on their distance.
   */
  @Override
  public int compareTo(final Node<E> other) {
    return Double.compare(mDistance, other.mDistance);
  }

  /**
   * Gets a list of all children of this node. If the node did not contain
   * children already it will add a new child with the same element to
   * itself.<br>
   * <br>
   * Note that the list is backed by the node, so changes are reflected to the
   * node.
   *
   * @return A list of all children of this node
   */
  public List<Node<E>> getChildren() {
    if (mChildren.isEmpty()) {
      final Node<E> child = new Node<>(this, mElement);
      addChild(child);
    }
    return mChildren;
  }

  /**
   * Gets the distance of the node.
   *
   * @return The distance of the node
   */
  public double getDistance() {
    return mDistance;
  }

  /**
   * Gets the element of the node.
   *
   * @return The element of the node
   */
  public E getElement() {
    return mElement;
  }

  /**
   * Gets the parent of the node.
   *
   * @return The parent of the node
   */
  public Node<E> getParent() {
    return mParent;
  }

  /**
   * Removes the given child from the node.<br>
   * <br>
   * Note that this operation may take some time since the node maintains
   * children in a list.
   *
   * @param child The child to remove
   */
  public void removeChild(final Node<E> child) {
    mChildren.remove(child);
  }

  /**
   * Removes all children of this node.
   */
  public void removeChildren() {
    mChildren.clear();
  }

  /**
   * Sets the distance of this node.
   *
   * @param distance The distance to set
   */
  public void setDistance(final double distance) {
    mDistance = distance;
  }

  /**
   * Sets the parent of this node.
   *
   * @param parent The parent to set
   */
  public void setParent(final Node<E> parent) {
    mParent = parent;
  }

}