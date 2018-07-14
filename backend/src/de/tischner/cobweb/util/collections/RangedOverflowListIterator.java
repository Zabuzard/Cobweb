package de.tischner.cobweb.util.collections;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/**
 * Iterator for a list with random access that iterates over a given range. The
 * range is allowed to overflow the bound.<br>
 * <br>
 * For example iterating over a list of size <tt>7</tt> from index <tt>5</tt> to
 * </tt>2</tt> (exclusive) which would return indices <tt>5, 6, 0, 1</tt>.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> The type of the elements
 */
public final class RangedOverflowListIterator<E> implements Iterator<E> {
  /**
   * The iterator to use.
   */
  private final Iterator<E> mIterator;

  /**
   * Creates an iterator that iterates all elements of the given list starting
   * from the given index (inclusive).<br>
   * <br>
   * For example using a list of size <tt>4</tt> with index <tt>2</tt> would
   * traverse indices <tt>2, 3, 0, 1</tt>.
   *
   * @param elements The elements to iterate
   * @param from     The index of the first element to iterate from (inclusive)
   * @throws IllegalArgumentException If the bound is below <tt>0</tt> or
   *                                  greater equals the size. Or the list does
   *                                  not implement {@link RandomAccess}.
   */
  public RangedOverflowListIterator(final List<E> elements, final int from) throws IllegalArgumentException {
    this(elements, from, from);
  }

  /**
   * Creates an iterator that iterates all elements of the given list starting
   * from the given index (inclusive) to the given index (exclusive). The right
   * bound is allowed to overflow the bounds.<br>
   * <br>
   * For example using a list of size <tt>5</tt> with a range of <tt>[3, 2)</tt>
   * would traverse indices <tt>3, 4, 0, 1</tt>.
   *
   * @param elements The elements to iterate
   * @param from     The index of the first element to iterate from, i.e. an
   *                 inclusive bound
   * @param to       The index one after the last element to iterate to, i.e. an
   *                 exclusive bound
   * @throws IllegalArgumentException If one of the bounds is below <tt>0</tt>
   *                                  or greater the size (greater equals for
   *                                  <tt>from</tt>). Or the list does not
   *                                  implement {@link RandomAccess}.
   */
  public RangedOverflowListIterator(final List<E> elements, final int from, final int to)
      throws IllegalArgumentException {
    if (from < 0 || to < 0 || from >= elements.size() || to > elements.size() || !(elements instanceof RandomAccess)) {
      throw new IllegalArgumentException();
    }

    if (from < to) {
      mIterator = new RangedListIterator<>(elements, from, to);
    } else if (to == 0) {
      mIterator = new RangedListIterator<>(elements, from, elements.size());
    } else {
      final Iterator<E> fromToEnd = new RangedListIterator<>(elements, from, elements.size());
      final Iterator<E> startToTo = new RangedListIterator<>(elements, 0, to);
      mIterator = new DoubletonIterator<>(fromToEnd, startToTo);
    }
  }

  @Override
  public boolean hasNext() {
    return mIterator.hasNext();
  }

  @Override
  public E next() {
    return mIterator.next();
  }

}
