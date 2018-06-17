package de.tischner.cobweb.util.collections;

import java.util.Iterator;

/**
 * Implementation of an iterator that iterates two given iterators.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> The type of the elements to be iterated
 */
public final class DoubletonIterator<E> implements Iterator<E> {
  /**
   * The first iterator.
   */
  private final Iterator<E> mFirst;
  /**
   * The second iterator.
   */
  private final Iterator<E> mSecond;

  /**
   * Creates a new doubleton iterator wrapping the two given iterator.
   *
   * @param first  The first iterator
   * @param second The second iterator
   */
  public DoubletonIterator(final Iterator<E> first, final Iterator<E> second) {
    mFirst = first;
    mSecond = second;
  }

  @Override
  public boolean hasNext() {
    return mFirst.hasNext() || mSecond.hasNext();
  }

  @Override
  public E next() {
    if (mFirst.hasNext()) {
      return mFirst.next();
    }
    return mSecond.next();
  }

}
