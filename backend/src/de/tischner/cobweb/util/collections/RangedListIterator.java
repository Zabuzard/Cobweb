package de.tischner.cobweb.util.collections;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public final class RangedListIterator<E> implements Iterator<E> {

  private final List<E> mElements;
  private int mNextIndex;
  private final int mTo;

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
