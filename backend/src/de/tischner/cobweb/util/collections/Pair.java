package de.tischner.cobweb.util.collections;

/**
 * Object for generic pairs which hold two objects of given types.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E1> Type of the first element
 * @param <E2> Type of the second element
 */
public final class Pair<E1, E2> {
  /**
   * First element of the pair.
   */
  private final E1 mFirstElement;
  /**
   * Second element of the pair.
   */
  private final E2 mSecondElement;

  /**
   * Creates a new pair holding the two given objects.
   *
   * @param first  First object of the pair
   * @param second Second object of the pair
   */
  public Pair(final E1 first, final E2 second) {
    mFirstElement = first;
    mSecondElement = second;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Pair)) {
      return false;
    }
    final Pair<?, ?> other = (Pair<?, ?>) obj;
    if (this.mFirstElement == null) {
      if (other.mFirstElement != null) {
        return false;
      }
    } else if (!this.mFirstElement.equals(other.mFirstElement)) {
      return false;
    }
    if (this.mSecondElement == null) {
      if (other.mSecondElement != null) {
        return false;
      }
    } else if (!this.mSecondElement.equals(other.mSecondElement)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the first element of the pair.
   *
   * @return The first element of the pair
   */
  public E1 getFirst() {
    return mFirstElement;
  }

  /**
   * Gets the second element of the pair.
   *
   * @return The second element of the pair
   */
  public E2 getSecond() {
    return mSecondElement;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.mFirstElement == null) ? 0 : this.mFirstElement.hashCode());
    result = prime * result + ((this.mSecondElement == null) ? 0 : this.mSecondElement.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + mFirstElement + ", " + mSecondElement + "]";
  }
}
