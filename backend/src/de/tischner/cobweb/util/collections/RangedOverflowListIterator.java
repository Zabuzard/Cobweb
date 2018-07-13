package de.tischner.cobweb.util.collections;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

public final class RangedOverflowListIterator<E> implements Iterator<E> {

  private final Iterator<E> mIterator;

  public RangedOverflowListIterator(final List<E> elements, final int from) throws IllegalArgumentException {
    this(elements, from, from);
  }

  public RangedOverflowListIterator(final List<E> elements, final int from, final int to)
      throws IllegalArgumentException {
    if (from < 0 || to < 0 || from >= elements.size() || to > elements.size() || !(elements instanceof RandomAccess)) {
      throw new IllegalArgumentException("Size: " + elements.size() + ", from: " + from + ", to: " + to
          + ", is RandomAccess: " + (elements instanceof RandomAccess));
    }

    if (from < to) {
      mIterator = new RangedListIterator<>(elements, from, to);
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
