package de.unifreiburg.informatik.cobweb.benchmark;

/**
 * Container for an element and a cost. The containers natural ordering is
 * ascending in cost.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> The type of the contained element
 */
public final class CostContainer<E> implements Comparable<CostContainer<E>> {
  /**
   * The cost of the element.
   */
  private final double mCost;
  /**
   * The contained element.
   */
  private final E mElement;

  /**
   * Creates a new container with the given element and cost.
   *
   * @param element The element of the container
   * @param cost    The cost of the element
   */
  public CostContainer(final E element, final double cost) {
    mElement = element;
    mCost = cost;
  }

  @Override
  public int compareTo(final CostContainer<E> other) {
    return Double.compare(mCost, other.mCost);
  }

  /**
   * Gets the cost of the element.
   *
   * @return The cost to get
   */
  public double getCost() {
    return mCost;
  }

  /**
   * Gets the contained element.
   *
   * @return The element to get
   */
  public E getElement() {
    return mElement;
  }
}
