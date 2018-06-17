package de.tischner.cobweb.util.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Implementation of a collection that wraps two given collections.<br>
 * <br>
 * Method calls are forwarded to the given collections and then results are
 * combined. The method {@link #containsAll(Collection)} is not supported.
 * Methods {@link #add(Object)} and {@link #addAll(Collection)} are executed on
 * the first collection only.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> The type of the elements contained in the collection
 */
public final class DoubletonCollection<E> implements Collection<E> {
  /**
   * The first collection.
   */
  private final Collection<E> mFirst;
  /**
   * The second collection.
   */
  private final Collection<E> mSecond;

  /**
   * Creates a new doubleton collection wrapping the two given collections.
   *
   * @param first  The first collection
   * @param second The second collection
   */
  public DoubletonCollection(final Collection<E> first, final Collection<E> second) {
    mFirst = first;
    mSecond = second;
  }

  /**
   * Adds the given element to the second collection.
   */
  @Override
  public boolean add(final E e) {
    return mSecond.add(e);
  }

  /**
   * Adds the given elements to the second collection.
   */
  @Override
  public boolean addAll(final Collection<? extends E> c) {
    return mSecond.addAll(c);
  }

  @Override
  public void clear() {
    mFirst.clear();
    mSecond.clear();
  }

  @Override
  public boolean contains(final Object o) {
    return mFirst.contains(o) || mSecond.contains(o);
  }

  /**
   * Not implemented.
   *
   * @throws UnsupportedOperationException The method is not implemented
   */
  @Override
  public boolean containsAll(final Collection<?> c) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty() {
    return mFirst.isEmpty() && mSecond.isEmpty();
  }

  @Override
  public Iterator<E> iterator() {
    return new DoubletonIterator<>(mFirst.iterator(), mSecond.iterator());
  }

  @Override
  public boolean remove(final Object o) {
    boolean wasModified = mFirst.remove(o);
    if (!wasModified) {
      wasModified |= mSecond.remove(o);
    }
    return wasModified;
  }

  @Override
  public boolean removeAll(final Collection<?> c) {
    boolean wasModified = mFirst.removeAll(c);
    wasModified |= mSecond.removeAll(c);
    return wasModified;
  }

  @Override
  public boolean retainAll(final Collection<?> c) {
    boolean wasModified = mFirst.retainAll(c);
    wasModified |= mSecond.retainAll(c);
    return wasModified;
  }

  @Override
  public int size() {
    return mFirst.size() + mSecond.size();
  }

  @Override
  public Object[] toArray() {
    return Stream.concat(mFirst.stream(), mSecond.stream()).toArray();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(final T[] a) {
    if (a == null) {
      return null;
    }
    if (a.length >= size()) {
      // Use the given array
      final Iterator<E> firstIter = mFirst.iterator();
      final int firstSize = mFirst.size();
      for (int i = 0; i < firstSize; i++) {
        a[i] = (T) firstIter.next();
      }

      final Iterator<E> secondIter = mSecond.iterator();
      final int secondSize = mSecond.size();
      for (int i = 0; i < secondSize; i++) {
        a[firstSize + i] = (T) secondIter.next();
      }

      // Required per method documentation
      if (a.length != 0) {
        a[firstSize + secondSize] = null;
      }
      return a;
    }

    // Create a new array
    return Stream.concat(mFirst.stream(), mSecond.stream())
        .toArray(length -> (T[]) Array.newInstance(a.getClass().getComponentType(), length));
  }
}
