package de.tischner.cobweb.util;

/**
 * Object for generic triples which hold three objects of given types.
 *
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 * @param <E1> First type
 * @param <E2> Second type
 * @param <E3> Third type
 */
public final class Triple<E1, E2, E3> {
  /**
   * First element of the triple.
   */
  private final E1 mFirstElement;
  /**
   * Second element of the triple.
   */
  private final E2 mSecondElement;
  /**
   * Third element of the triple.
   */
  private final E3 mThirdElement;

  /**
   * Creates a new triple holding the three given objects.
   *
   * @param first  First object of the triple
   * @param second Second object of the triple
   * @param third  Third object of the triple
   */
  public Triple(final E1 first, final E2 second, final E3 third) {
    mFirstElement = first;
    mSecondElement = second;
    mThirdElement = third;
  }

  /*
   * (non-Javadoc)
   *
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
    if (!(obj instanceof Triple)) {
      return false;
    }
    final Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
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
    if (this.mThirdElement == null) {
      if (other.mThirdElement != null) {
        return false;
      }
    } else if (!this.mThirdElement.equals(other.mThirdElement)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the first element of the triple.
   *
   * @return The first element of the triple
   */
  public E1 getFirst() {
    return mFirstElement;
  }

  /**
   * Gets the second element of the triple.
   *
   * @return The second element of the triple
   */
  public E2 getSecond() {
    return mSecondElement;
  }

  /**
   * Gets the third element of the triple.
   *
   * @return The third element of the triple
   */
  public E3 getThird() {
    return mThirdElement;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.mFirstElement == null) ? 0 : this.mFirstElement.hashCode());
    result = prime * result + ((this.mSecondElement == null) ? 0 : this.mSecondElement.hashCode());
    result = prime * result + ((this.mThirdElement == null) ? 0 : this.mThirdElement.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + mFirstElement + ", " + mSecondElement + ", " + mThirdElement + "]";
  }
}
