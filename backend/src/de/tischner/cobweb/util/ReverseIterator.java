package de.tischner.cobweb.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class ReverseIterator<E> implements Iterator<E> {

  private final ListIterator<E> mListIterator;

  public ReverseIterator(final List<E> list) {
    mListIterator = list.listIterator(list.size());
  }

  @Override
  public boolean hasNext() {
    return mListIterator.hasPrevious();
  }

  @Override
  public E next() {
    return mListIterator.previous();
  }

}
