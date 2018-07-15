package de.unifreiburg.informatik.cobweb.util.collections;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Implementation of a {@link Set} which uses arrays as long as the size is
 * below a certain threshold. Switches over to a regular {@link Set} if the
 * threshold is exceeded. The set is not synchronized and does not guarantee any
 * order when iterating.<br>
 * <br>
 * This set is <b>extremely optimized</b> for a use case of where sets with a
 * <b>very low size</b> is needed (below <tt>7</tt>) which are rarely modified.
 * The {@link #contains(Object)} method and iteration operates faster on arrays
 * for this small sizes. In reverse, any modifications like {@link #add(Object)}
 * and {@link #remove(Object)} operate slower.<br>
 * <br>
 * When creating instances prefer to <b>not</b> repeatedly call
 * {@link #add(Object)}. Instead, consider using
 * {@link #HybridArrayHashSet(Collection)} or {@link #addAll(Collection)}. <b>Do
 * not use</b> this set if it is known that the resulting size exceeds the
 * threshold. Or, alternatively use {@link #HybridArrayHashSet(Collection)} and
 * {@link #addAll(Collection)} to force the switch-over to the regular
 * {@link Set} as soon as possible.<br>
 * <br>
 * The internal array does not use any buffer or capacity for future
 * modifications. Every modification, when in <i>array-mode</i>, will create a
 * new array with different size.<br>
 * <br>
 * <b>Never</b> alternately use {@link #add(Object)} and {@link #remove(Object)}
 * when the size is around the threshold where the class switches from
 * <i>array-</i> to <i>set-mode</i>. The class does not offer any protection for
 * this scenario, it will copy over all elements for each such call.<br>
 * <br>
 * For iteration prefer using internal iteration using
 * {@link #forEach(java.util.function.Consumer)} instead of external iteration
 * by {@link #iterator()}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> Type of the elements contained in this set
 */
public final class HybridArrayHashSet<E> implements Set<E>, Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The threshold of when to switch modes. If the size is greater than this,
   * the set will use a regular set instead.
   */
  private static final int THRESHOLD = 6;
  /**
   * If in array-mode this contains the elements of the set, else <tt>null</tt>.
   */
  private Object[] mArray;
  /**
   * If in set-mode this contains the elements of the set, else <tt>null</tt>.
   */
  private Set<E> mSet;

  /**
   * Creates a new empty set. If possible, consider using
   * {@link #HybridArrayHashSet(Collection)} or at least
   * {@link #HybridArrayHashSet(Object)}.
   */
  public HybridArrayHashSet() {
    mArray = new Object[0];
  }

  /**
   * Creates a new set with the given elements.
   *
   * @param c The elements the set should contain
   */
  public HybridArrayHashSet(final Collection<E> c) {
    if (c.size() > THRESHOLD) {
      mSet = new HashSet<>(c);
    } else {
      mArray = c.toArray();
    }
  }

  /**
   * Creates a new set with the given element. If possible, consider using
   * {@link #HybridArrayHashSet(Collection)}.
   *
   * @param e The element the set should contain
   */
  public HybridArrayHashSet(final E e) {
    mArray = new Object[] { e };
  }

  /**
   * Creates a new set with the given elements. The set will be empty if
   * <tt>null</tt> or an empty array is passed.
   *
   * @param elements The elements the set should contain
   */
  @SafeVarargs
  public HybridArrayHashSet(final E... elements) {
    if (elements == null || elements.length == 0) {
      mArray = new Object[0];
      return;
    }

    if (elements.length > THRESHOLD) {
      mSet = new HashSet<>(Arrays.asList(elements));
    } else {
      mArray = elements.clone();
    }
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#add(java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean add(final E e) {
    if (useSet()) {
      return mSet.add(e);
    }
    // Check if the element is contained already
    boolean isContained = false;
    for (int i = 0; i < mArray.length; i++) {
      final Object element = mArray[i];
      if (e == null ? element == null : e.equals(element)) {
        isContained = true;
        break;
      }
    }
    if (isContained) {
      return false;
    }

    // Switch over to set
    if (mArray.length == THRESHOLD) {
      mSet = new HashSet<>();
      for (int i = 0; i < mArray.length; i++) {
        mSet.add((E) mArray[i]);
      }
      mArray = null;
      return mSet.add(e);
    }

    // Create a new array with place for one more object
    final int indexForElement = mArray.length;
    mArray = Arrays.copyOf(mArray, mArray.length + 1);
    mArray[indexForElement] = e;
    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#addAll(java.util.Collection)
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean addAll(final Collection<? extends E> c) {
    if (useSet()) {
      return mSet.addAll(c);
    }

    // Eliminate duplicates
    final HashSet<E> elementsToAdd = new HashSet<>(c);
    // Remove everything which is already contained
    for (int i = 0; i < mArray.length; i++) {
      elementsToAdd.remove(mArray[i]);
    }

    if (elementsToAdd.isEmpty()) {
      return false;
    }

    final int resultingSize = mArray.length + elementsToAdd.size();
    if (resultingSize > THRESHOLD) {
      // Switch over to set
      mSet = elementsToAdd;
      for (int i = 0; i < mArray.length; i++) {
        mSet.add((E) mArray[i]);
      }
      mArray = null;
      return true;
    }

    // Append the elements to the array
    final Iterator<E> elementToAddIter = elementsToAdd.iterator();
    final int indexForFirstElement = mArray.length;
    mArray = Arrays.copyOf(mArray, resultingSize);
    for (int i = indexForFirstElement; i < resultingSize; i++) {
      mArray[i] = elementToAddIter.next();
    }

    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#clear()
   */
  @Override
  public void clear() {
    mSet = null;
    mArray = new Object[0];
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#contains(java.lang.Object)
   */
  @Override
  public boolean contains(final Object o) {
    if (useSet()) {
      return mSet.contains(o);
    }

    for (int i = 0; i < mArray.length; i++) {
      final Object element = mArray[i];
      if (o == null ? element == null : o.equals(element)) {
        return true;
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#containsAll(java.util.Collection)
   */
  @Override
  public boolean containsAll(final Collection<?> c) {
    if (useSet()) {
      return mSet.containsAll(c);
    }

    outer: for (final Object o : c) {
      for (int i = 0; i < mArray.length; i++) {
        final Object element = mArray[i];
        if (o == null ? element == null : o.equals(element)) {
          // Was found, continue with the next element
          continue outer;
        }
      }
      // Was not found
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Iterable#forEach(java.util.function.Consumer)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void forEach(final Consumer<? super E> action) {
    if (useSet()) {
      mSet.forEach(action);
      return;
    }
    for (int i = 0; i < mArray.length; i++) {
      action.accept((E) mArray[i]);
    }
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#isEmpty()
   */
  @Override
  public boolean isEmpty() {
    if (useSet()) {
      return false;
    }
    return mArray.length == 0;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#iterator()
   */
  @Override
  public Iterator<E> iterator() {
    if (useSet()) {
      return mSet.iterator();
    }
    return new ArrayIterator<>(mArray);
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#remove(java.lang.Object)
   */
  @Override
  public boolean remove(final Object o) {
    if (useSet()) {
      final boolean wasRemoved = mSet.remove(o);
      // Switch over to array
      if (wasRemoved && mSet.size() == THRESHOLD) {
        mArray = mSet.toArray();
        mSet = null;
      }
      return wasRemoved;
    }

    if (mArray.length == 0) {
      return false;
    }
    // Search the index of the element if contained
    int indexOfElement = -1;
    for (int i = 0; i < mArray.length; i++) {
      final Object element = mArray[i];
      if (o == null ? element == null : o.equals(element)) {
        indexOfElement = i;
        break;
      }
    }
    if (indexOfElement == -1) {
      return false;
    }
    // Create a new array without the given element
    final Object[] elements = new Object[mArray.length - 1];
    for (int i = 0; i < indexOfElement; i++) {
      elements[i] = mArray[i];
    }
    for (int i = indexOfElement + 1; i < mArray.length; i++) {
      elements[i - 1] = mArray[i];
    }
    mArray = elements;
    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#removeAll(java.util.Collection)
   */
  @Override
  public boolean removeAll(final Collection<?> c) {
    if (useSet()) {
      final boolean wasModified = mSet.removeAll(c);

      // Switch over to array
      if (mSet.size() <= THRESHOLD) {
        mArray = mSet.toArray();
        mSet = null;
      }
      return wasModified;
    }

    // Determine the resulting size
    int resultingSize = mArray.length;
    for (int i = 0; i < mArray.length; i++) {
      if (c.contains(mArray[i])) {
        resultingSize--;
      }
    }
    if (resultingSize == mArray.length) {
      return false;
    }
    // Create the new array
    final Object[] elements = new Object[resultingSize];
    int indexToPush = 0;
    for (int i = 0; i < mArray.length; i++) {
      final Object element = mArray[i];
      if (!c.contains(element)) {
        elements[indexToPush++] = element;
      }
    }
    mArray = elements;
    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#retainAll(java.util.Collection)
   */
  @Override
  public boolean retainAll(final Collection<?> c) {
    if (useSet()) {
      final boolean wasModified = mSet.retainAll(c);

      // Switch over to array
      if (mSet.size() <= THRESHOLD) {
        mArray = mSet.toArray();
        mSet = null;
      }
      return wasModified;
    }

    // Use a set as utility
    final Set<Object> asSet = new HashSet<>(Arrays.asList(mArray));
    final boolean wasModified = asSet.retainAll(c);
    mArray = asSet.toArray();
    return wasModified;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#size()
   */
  @Override
  public int size() {
    if (useSet()) {
      return mSet.size();
    }
    return mArray.length;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#toArray()
   */
  @Override
  public Object[] toArray() {
    if (useSet()) {
      return mSet.toArray();
    }
    return mArray;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#toArray(java.lang.Object[])
   */
  @Override
  public <T> T[] toArray(final T[] a) {
    if (useSet()) {
      return mSet.toArray(a);
    }
    return Arrays.asList(mArray).toArray(a);
  }

  /**
   * Whether or not the set is currently in <i>set-mode</i>. This is determined
   * by {@link #mArray} being <tt>null</tt> only.
   *
   * @return <tt>True</tt> if the set is in <i>set-mode</i>, <tt>false</tt> if
   *         in <i>array-mode</i>
   */
  private boolean useSet() {
    return mArray == null;
  }
}
