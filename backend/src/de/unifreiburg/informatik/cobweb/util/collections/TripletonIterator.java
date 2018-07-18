package de.unifreiburg.informatik.cobweb.util.collections;

import java.util.Iterator;

/**
 * Implementation of an iterator that iterates three given iterators.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> The type of the elements to be iterated
 */
public final class TripletonIterator<E> implements Iterator<E> {
  /**
   * The first iterator.
   */
  private final Iterator<E> mFirst;
  /**
   * The second iterator.
   */
  private final Iterator<E> mSecond;
  /**
   * The third iterator.
   */
  private final Iterator<E> mThird;

  /**
   * Creates a new tripleton iterator wrapping the three given iterator.
   *
   * @param first  The first iterator
   * @param second The second iterator
   * @param third  The third iterator
   */
  public TripletonIterator(final Iterator<E> first, final Iterator<E> second, final Iterator<E> third) {
    mFirst = first;
    mSecond = second;
    mThird = third;
  }

  @Override
  public boolean hasNext() {
    return mFirst.hasNext() || mSecond.hasNext() || mThird.hasNext();
  }

  @Override
  public E next() {
    if (mFirst.hasNext()) {
      return mFirst.next();
    } else if (mSecond.hasNext()) {
      return mSecond.next();
    } else {
      return mThird.next();
    }
  }

}
