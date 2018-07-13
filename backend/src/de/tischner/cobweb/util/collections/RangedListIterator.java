package de.tischner.cobweb.util.collections;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * Iterator for a list with random access that iterates over a given range.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> The type of the elements
 */
public final class RangedListIterator<E> implements Iterator<E> {
  /**
   * The elements to iterate over.
   */
  private final List<E> mElements;
  /**
   * The index of the element to return next.
   */
  private int mNextIndex;
  /**
   * The index one after the last element to return.
   */
  private final int mTo;

  /**
   * Creates a new ranged list iterator that iterates over the given list on the
   * given range.
   *
   * @param elements The elements to iterate over, must provide random access
   * @param from     The index of the first element to iterate, i.e. an
   *                 inclusive bound
   * @param to       The index one after the last element to return, i.e. an
   *                 exclusive bound
   * @throws IllegalArgumentException If any bound of the range is below
   *                                  <tt>0</tt> or greater than the size of the
   *                                  list (greater equals for <tt>from</tt>).
   *                                  Or if <tt>from</tt> is greater equals
   *                                  <tt>to</tt>. Or the list does not
   *                                  implement {@link RandomAccess}.
   */
  public RangedListIterator(final List<E> elements, final int from, final int to) throws IllegalArgumentException {
    if (from < 0 || to < 0 || from >= elements.size() || to > elements.size() || from >= to
        || !(elements instanceof RandomAccess)) {
      throw new IllegalArgumentException();
    }
    mElements = elements;
    mTo = to;
    mNextIndex = from;
  }

  @Override
  public boolean hasNext() {
    return mNextIndex != mTo;
  }

  @Override
  public E next() {
    if (mNextIndex == mTo) {
      throw new NoSuchElementException();
    }
    return mElements.get(mNextIndex++);
  }

}
