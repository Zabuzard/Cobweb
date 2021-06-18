package de.unifreiburg.informatik.cobweb.util.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.eclipse.collections.api.block.procedure.primitive.IntObjectProcedure;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.impl.list.mutable.FastList;

import de.unifreiburg.informatik.cobweb.routing.model.graph.IHasId;

/**
 * Implementation of a {@link Map} which uses arrays internally. Values are
 * implicitly referred to by the unique ID of their key as index. Negative IDs
 * and <code>null</code> keys, as well as values are not supported.<br>
 * <br>
 * This class is optimized for a use case where retrieval of the keys is not
 * needed and key IDs lie close to each other, preferable with no gaps between.
 * As such it provides a very fast access to values by the ID of their key. If
 * gaps between the IDs are minimized it has a very small memory footprint.<br>
 * <br>
 * The methods {@link #keySet()}, {@link #entrySet()}, {@link #values()} and
 * {@link #forEach(BiConsumer)} are not supported since keys are not saved in
 * the map. For iteration consider using internal iteration by
 * {@link #forEachIdValue(IntObjectProcedure)} or {@link #streamValues()}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <K> Type of the key, must provide a unique ID
 * @param <V> Type of the value
 */
public final class IdMap<K extends IHasId, V> implements Map<K, V>, Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The size of the map, i.e. the amount of entries.
   */
  private int mSize;
  /**
   * The internal list which associates IDs of keys as index to their
   * corresponding values. Can contain gaps which are indicated by <code>null</code>
   * values.
   */
  private final MutableList<V> mValues;

  /**
   * Creates a new initially empty array map.
   */
  public IdMap() {
    this(FastList.newList());
  }

  /**
   * Creates a new initially empty array map.
   *
   * @param initialCapacity The initial capacity of the map
   */
  public IdMap(final int initialCapacity) {
    this(FastList.newList(initialCapacity));
  }

  /**
   * Creates a new array map with the given values.
   *
   * @param values The values of the map
   */
  private IdMap(final MutableList<V> values) {
    mValues = values;
    mSize = mValues.size();
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#clear()
   */
  @Override
  public void clear() {
    mValues.clear();
    mSize = 0;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  @Override
  public boolean containsKey(final Object key) {
    if (!(key instanceof IHasId)) {
      return false;
    }
    final int index = ((IHasId) key).getId();
    if (index >= mValues.size() || index < 0) {
      return false;
    }
    return mValues.get(index) != null;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  @Override
  public boolean containsValue(final Object value) {
    return mValues.contains(value);
  }

  /**
   * This operation is not supported by the map.<br>
   * <br>
   * For iteration consider using internal iteration with
   * {@link #forEachIdValue(IntObjectProcedure)}.
   *
   * @throws UnsupportedOperationException The operation is not supported
   */
  @Override
  public Set<Entry<K, V>> entrySet() {
    throw new UnsupportedOperationException();
  }

  /**
   * This operation is not supported by the map.<br>
   * <br>
   * For iteration consider using internal iteration with
   * {@link #forEachIdValue(IntObjectProcedure)}.
   *
   * @throws UnsupportedOperationException The operation is not supported
   */
  @Override
  public void forEach(final BiConsumer<? super K, ? super V> action) {
    throw new UnsupportedOperationException();
  }

  /**
   * Executes the given procedure on all ID-value pairs in this map.
   *
   * @param action The action to perform on each ID-value pair
   */
  public void forEachIdValue(final IntObjectProcedure<? super V> action) {
    ((OrderedIterable<V>) mValues).forEachWithIndex((value, id) -> {
      if (value == null) {
        return;
      }
      action.value(id, value);
    });
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#get(java.lang.Object)
   */
  @Override
  public V get(final Object key) {
    if (!(key instanceof IHasId)) {
      return null;
    }
    final int index = ((IHasId) key).getId();
    if (index >= mValues.size() || index < 0) {
      return null;
    }
    return mValues.get(index);
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#isEmpty()
   */
  @Override
  public boolean isEmpty() {
    return mSize == 0;
  }

  /**
   * This operation is not supported by the map.<br>
   * <br>
   * For iteration consider using internal iteration with
   * {@link #forEachIdValue(IntObjectProcedure)}.
   *
   * @throws UnsupportedOperationException The operation is not supported
   */
  @Override
  public Set<K> keySet() {
    throw new UnsupportedOperationException();
  }

  /**
   * @throws IllegalArgumentException If the given key has a negative ID
   */
  @Override
  public V put(final K key, final V value) {
    final int index = key.getId();

    if (index < 0) {
      throw new IllegalArgumentException();
    }
    if (index >= mValues.size()) {
      CollectionUtil.increaseCapacity(mValues, index + 1);
    }
    final V previous = mValues.set(index, value);
    if (previous == null) {
      mSize++;
    }
    return previous;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#putAll(java.util.Map)
   */
  @Override
  public void putAll(final Map<? extends K, ? extends V> m) {
    m.forEach(this::put);
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#remove(java.lang.Object)
   */
  @Override
  public V remove(final Object key) {
    if (!(key instanceof IHasId)) {
      return null;
    }
    final int index = ((IHasId) key).getId();
    if (index >= mValues.size() || index < 0) {
      return null;
    }
    final V previous = mValues.set(index, null);
    if (previous != null) {
      mSize--;
    }
    return previous;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#size()
   */
  @Override
  public int size() {
    return mSize;
  }

  /**
   * Returns a stream over all values in this map. The stream is constructed in
   * <code>O(1)</code>.
   *
   * @return Stream over all values in this map
   */
  public Stream<V> streamValues() {
    return mValues.stream().filter(Objects::nonNull);
  }

  /**
   * This operation is not supported by the map.<br>
   * <br>
   * For iteration consider using internal iteration with
   * {@link #forEachIdValue(IntObjectProcedure)} or {@link #streamValues()}.
   *
   * @throws UnsupportedOperationException The operation is not supported
   */
  @Override
  public Collection<V> values() {
    throw new UnsupportedOperationException();
  }

}
