package de.tischner.cobweb.util.collections;

import java.util.Iterator;

public final class DoubletonIterator<E> implements Iterator<E> {

  private final Iterator<E> mFirst;
  private final Iterator<E> mSecond;

  public DoubletonIterator(final Iterator<E> first, final Iterator<E> second) {
    mFirst = first;
    mSecond = second;
  }

  @Override
  public boolean hasNext() {
    return mFirst.hasNext() && mSecond.hasNext();
  }

  @Override
  public E next() {
    if (mFirst.hasNext()) {
      return mFirst.next();
    }
    return mSecond.next();
  }

}
