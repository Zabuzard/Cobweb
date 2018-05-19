package de.tischner.cobweb.util.collections;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.tuple.primitive.ObjectDoublePair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectDoubleHashMap;

/**
 * Primitive nested hash map which uses two keys in a nested structure for
 * storing double values.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <K1> Type of the first key
 * @param <K2> Type of the second key
 */
public final class NestedDoubleMap<K1, K2> {

  /**
   * Internal map which stores maps of second keys and double values for the
   * first keys.
   */
  private final Map<K1, MutableObjectDoubleMap<K2>> mK1ToK2ToDouble;
  /**
   * The initial capacity to use when creating maps which connect the second key
   * to the double value or <tt>-1</tt> if a default value should be used.
   */
  private int mNestedInitialCapacity;

  /**
   * Creates a new empty nested double map.
   */
  public NestedDoubleMap() {
    this(Maps.mutable.withInitialCapacity(50));
  }

  /**
   * Creates a new empty nested map with an initial capacity.
   *
   * @param initialCapacity The initial capacity of the map
   */
  public NestedDoubleMap(final int initialCapacity) {
    this(Maps.mutable.withInitialCapacity(initialCapacity));
  }

  /**
   * Creates a new nested double map which uses the given map.
   *
   * @param nestedMap The nested double map to use
   */
  private NestedDoubleMap(final Map<K1, MutableObjectDoubleMap<K2>> nestedMap) {
    mK1ToK2ToDouble = nestedMap;
    mNestedInitialCapacity = -1;
  }

  /**
   * Adds all entries from the given map to this map.
   *
   * @param nestedMap The map to add entries from
   */
  public void addAll(final NestedDoubleMap<K1, K2> nestedMap) {
    for (final PairAndDouble<K1, K2> entry : nestedMap.entrySet()) {
      put(entry.getFirst(), entry.getSecond(), entry.getValue());
    }
  }

  /**
   * Removes all of the mappings from this map. The map will be empty after this
   * call returns.
   */
  public void clear() {
    mK1ToK2ToDouble.clear();
  }

  /**
   * Whether the map contains an entry for the given keys.
   *
   * @param key1 The first key
   * @param key2 The second key
   * @return <tt>True</tt> if the map contains an entry for the keys,
   *         <tt>false</tt> otherwise
   */
  public boolean contains(final K1 key1, final K2 key2) {
    final ObjectDoubleMap<K2> k2ToDouble = mK1ToK2ToDouble.get(key1);
    if (k2ToDouble == null) {
      return false;
    }
    return k2ToDouble.containsKey(key2);
  }

  /**
   * Returns an iterable object which contains all entries of this map. The
   * result will be constructed on call.
   *
   * @return An iterable object which contains all entries of this map
   */
  public Iterable<PairAndDouble<K1, K2>> entrySet() {
    final List<PairAndDouble<K1, K2>> result = Lists.mutable.empty();
    for (final Entry<K1, MutableObjectDoubleMap<K2>> entryOuter : mK1ToK2ToDouble.entrySet()) {
      for (final ObjectDoublePair<K2> entryInner : entryOuter.getValue().keyValuesView()) {
        result.add(new PairAndDouble<>(entryOuter.getKey(), entryInner.getOne(), entryInner.getTwo()));
      }
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof NestedDoubleMap)) {
      return false;
    }
    final NestedDoubleMap<?, ?> other = (NestedDoubleMap<?, ?>) obj;
    if (this.mK1ToK2ToDouble == null) {
      if (other.mK1ToK2ToDouble != null) {
        return false;
      }
    } else if (!this.mK1ToK2ToDouble.equals(other.mK1ToK2ToDouble)) {
      return false;
    }
    return true;
  }

  /**
   * Returns the map to which the specified first key is mapped, or
   * <tt>null</tt> if this map contains no mapping for the first key.
   *
   * @param key1 The first key
   * @return The map to which the specified first key is mapped, or
   *         <tt>null</tt> if this map contains no mapping for the first key.
   */
  public ObjectDoubleMap<K2> get(final K1 key1) {
    return mK1ToK2ToDouble.get(key1);
  }

  /**
   * Returns the value to which the specified keys are mapped, or <tt>0.0</tt>
   * if this map contains no mapping for the keys.
   *
   * @param key1 The first key
   * @param key2 The second key
   * @return The value to which the specified keys are mapped, or <tt>0.0</tt>
   *         if this map contains no mapping for the keys.
   */
  public double get(final K1 key1, final K2 key2) {
    final ObjectDoubleMap<K2> k2toDouble = mK1ToK2ToDouble.get(key1);
    if (k2toDouble == null) {
      return 0.0;
    }
    return k2toDouble.get(key2);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.mK1ToK2ToDouble == null) ? 0 : this.mK1ToK2ToDouble.hashCode());
    return result;
  }

  /**
   * Returns a set view of the first keys contained in this map.
   *
   * @return A set view of the first keys contained in this map.
   */
  public Set<K1> keySet() {
    return mK1ToK2ToDouble.keySet();
  }

  /**
   * Associates the specified double value with the two specified keys in this
   * map.
   *
   * @param key1  First key
   * @param key2  Second key
   * @param value Value to associate
   */
  public void put(final K1 key1, final K2 key2, final double value) {
    mK1ToK2ToDouble.computeIfAbsent(key1, k -> buildMap()).put(key2, value);
  }

  /**
   * Associates the specified value with the given map.
   *
   * @param key1        First key
   * @param key2ToValue Map to associate
   * @return The previous map associated with the given key, or <tt>null</tt> if
   *         there was no mapping for the key.
   */
  public MutableObjectDoubleMap<K2> put(final K1 key1, final MutableObjectDoubleMap<K2> key2ToValue) {
    return mK1ToK2ToDouble.put(key1, key2ToValue);
  }

  /**
   * Removes the mapping for the first key from this map if it is present.
   *
   * @param k1 The first key
   * @return The previous value associated with the first key, or <tt>null</tt>
   *         if there was no mapping for it.
   */
  public MutableObjectDoubleMap<K2> remove(final K1 k1) {
    return mK1ToK2ToDouble.remove(k1);
  }

  /**
   * Removes the mapping for the two keys from this map if it is present.
   *
   * @param k1 The first key
   * @param k2 The second key
   */
  public void remove(final K1 k1, final K2 k2) {
    final MutableObjectDoubleMap<K2> k2ToV = mK1ToK2ToDouble.get(k1);
    if (k2ToV == null) {
      return;
    }

    k2ToV.remove(k2);
    if (k2ToV.isEmpty()) {
      mK1ToK2ToDouble.remove(k1);
    }
  }

  /**
   * The initial capacity to use when creating maps which connect the second key
   * to the double value or <tt>-1</tt> if a default value should be used.
   *
   * @param initialCapacity The initial capacity to use or <tt>-1</tt> for a
   *                        default value
   */
  public void setNestedInitialCapacity(final int initialCapacity) {
    mNestedInitialCapacity = initialCapacity;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return mK1ToK2ToDouble.toString();
  }

  /**
   * Builds a new map which connects the second key to double values.
   *
   * @return The constructed map
   */
  private MutableObjectDoubleMap<K2> buildMap() {
    if (mNestedInitialCapacity == -1) {
      return ObjectDoubleMaps.mutable.empty();
    }
    return new ObjectDoubleHashMap<>(mNestedInitialCapacity);
  }
}
