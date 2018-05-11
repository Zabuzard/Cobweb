package de.tischner.cobweb.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Iterator which iterates a given list reversely.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> Type of the element
 */
public final class ReverseIterator<E> implements Iterator<E> {
  /**
   * The list iterator of the list to iterate over.
   */
  private final ListIterator<E> mListIterator;

  /**
   * Creates a new reverse iterator which is able to reversely iterate the given
   * list.
   *
   * @param list The list to iterate over
   */
  public ReverseIterator(final List<E> list) {
    mListIterator = list.listIterator(list.size());
  }

  /*
   * (non-Javadoc)
   * @see java.util.Iterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    return mListIterator.hasPrevious();
  }

  /*
   * (non-Javadoc)
   * @see java.util.Iterator#next()
   */
  @Override
  public E next() {
    return mListIterator.previous();
  }

}
