package de.tischner.cobweb.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementation of an iterator which iterates over a given array. The iterator
 * does not support element removal.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> Type of the elements to iterate over
 */
public final class ArrayIterator<E> implements Iterator<E> {
  /**
   * Array to iterate over.
   */
  private final Object[] mElements;
  /**
   * The index in the array which represents the last object to return by
   * {@link #next()}.
   */
  private final int mEndIndex;
  /**
   * The index in the array which represents the next object to return by a call
   * of {@link #next()}.
   */
  private int mIndex;

  /**
   * Creates an iterator which iterates over the given array.<br>
   * <br>
   * The array <b>must only contain</b> elements of the generic type <tt>E</tt>
   * as specified. For performance reasons the iterator does not check the type
   * and will simply attempt to cast when using {@link #next()}.
   *
   * @param elements The array to iterate over which must only contain elements
   *                 of type <tt>E</tt> as specified
   */
  public ArrayIterator(final Object[] elements) {
    this(elements, 0, elements.length);
  }

  /**
   * Creates an iterator which iterates over the given array in the given
   * range.<br>
   * <br>
   * The array <b>must only contain</b> elements of the generic type <tt>E</tt>
   * as specified. For performance reasons the iterator does not check the type
   * and will simply attempt to cast when using {@link #next()}.<br>
   * <br>
   * For performance reasons the iterator does not check if the given indices
   * are within array bounds.
   *
   * @param elements The array to iterate over which must only contain elements
   *                 of type <tt>E</tt> as specified
   * @param from     The index of the first element to return. This bound is
   *                 inclusive.
   * @param to       One after the index of the last element to return. If
   *                 interpreted as index, this bound is exclusive.
   */
  public ArrayIterator(final Object[] elements, final int from, final int to) {
    mElements = elements;
    mIndex = from;
    mEndIndex = to - 1;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Iterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    return mIndex <= mEndIndex;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Iterator#next()
   */
  @SuppressWarnings("unchecked")
  @Override
  public E next() {
    if (mIndex > mEndIndex) {
      throw new NoSuchElementException();
    }
    // Optimized for performance reasons
    return (E) mElements[mIndex++];
  }

}
