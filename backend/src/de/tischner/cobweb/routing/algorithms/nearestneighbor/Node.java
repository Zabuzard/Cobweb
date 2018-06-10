package de.tischner.cobweb.routing.algorithms.nearestneighbor;

import java.util.List;

import org.eclipse.collections.impl.list.mutable.FastList;

/**
 * @author Nils Loehndorf
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E>
 */
public final class Node<E> implements Comparable<Node<E>> {
  private final List<Node<E>> mChildren;
  private double mDistance;
  private final E mElement;
  private Node<E> mParent;

  public Node(final Node<E> parent, final E element) {
    mParent = parent;
    mChildren = FastList.newList();
    mElement = element;
  }

  public void addChild(final Node<E> child) {
    mChildren.add(child);
  }

  @Override
  public int compareTo(final Node<E> other) {
    return Double.compare(mDistance, other.mDistance);
  }

  public List<Node<E>> getChildren() {
    if (mChildren.isEmpty()) {
      final Node<E> child = new Node<>(this, mElement);
      addChild(child);
    }
    return mChildren;
  }

  public double getDistance() {
    return mDistance;
  }

  public E getElement() {
    return mElement;
  }

  public Node<E> getParent() {
    return mParent;
  }

  public void removeChild(final Node<E> child) {
    mChildren.remove(child);
  }

  public void removeChildren() {
    mChildren.clear();
  }

  public void setDistance(final double distance) {
    mDistance = distance;
  }

  public void setParent(final Node<E> parent) {
    mParent = parent;
  }

}