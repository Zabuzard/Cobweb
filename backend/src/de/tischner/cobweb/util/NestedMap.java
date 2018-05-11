package de.tischner.cobweb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Nested hash map which uses two keys in a nested structure for storing values.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <K1> Type of the first key
 * @param <K2> Type of the second key
 * @param <V> Type of the value
 */
public final class NestedMap<K1, K2, V> {

  /**
   * Internal map which stores maps of second keys and values for the first
   * keys.
   */
  private final Map<K1, Map<K2, V>> mK1ToK2ToV;

  /**
   * Creates a new empty nested map.
   */
  public NestedMap() {
    mK1ToK2ToV = new HashMap<>();
  }

  /**
   * Adds all entries from the given map to this map.
   *
   * @param nestedMap The map to add entries from
   */
  public void addAll(final NestedMap<K1, K2, V> nestedMap) {
    for (final Triple<K1, K2, V> triple : nestedMap.entrySet()) {
      put(triple.getFirst(), triple.getSecond(), triple.getThird());
    }
  }

  /**
   * Removes all of the mappings from this map. The map will be empty after this
   * call returns.
   */
  public void clear() {
    mK1ToK2ToV.clear();
  }

  /**
   * Returns an iterable object which contains all entries of this map. The
   * result will be constructed on call.
   *
   * @return An iterable object which contains all entries of this map
   */
  public Iterable<Triple<K1, K2, V>> entrySet() {
    final ArrayList<Triple<K1, K2, V>> result = new ArrayList<>();
    for (final Entry<K1, Map<K2, V>> entryOuter : mK1ToK2ToV.entrySet()) {
      for (final Entry<K2, V> entryInner : entryOuter.getValue().entrySet()) {
        result.add(new Triple<>(entryOuter.getKey(), entryInner.getKey(), entryInner.getValue()));
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
    if (!(obj instanceof NestedMap)) {
      return false;
    }
    final NestedMap<?, ?, ?> other = (NestedMap<?, ?, ?>) obj;
    if (this.mK1ToK2ToV == null) {
      if (other.mK1ToK2ToV != null) {
        return false;
      }
    } else if (!this.mK1ToK2ToV.equals(other.mK1ToK2ToV)) {
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
  public Map<K2, V> get(final K1 key1) {
    return mK1ToK2ToV.get(key1);
  }

  /**
   * Returns the value to which the specified keys are mapped, or <tt>null</tt>
   * if this map contains no mapping for the keys.
   *
   * @param key1 The first key
   * @param key2 The second key
   * @return The value to which the specified keys are mapped, or <tt>null</tt>
   *         if this map contains no mapping for the keys.
   */
  public V get(final K1 key1, final K2 key2) {
    final Map<K2, V> k2toV = mK1ToK2ToV.get(key1);
    if (k2toV == null) {
      return null;
    }
    return k2toV.get(key2);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.mK1ToK2ToV == null) ? 0 : this.mK1ToK2ToV.hashCode());
    return result;
  }

  /**
   * Returns a set view of the first keys contained in this map.
   *
   * @return A set view of the first keys contained in this map.
   */
  public Set<K1> keySet() {
    return mK1ToK2ToV.keySet();
  }

  /**
   * Associates the specified value with the two specified keys in this map.
   *
   * @param key1  First key
   * @param key2  Second key
   * @param value Value to associate
   * @return The previous value associated with the two keys, or <tt>null</tt>
   *         if there was no mapping for the keys.
   */
  public V put(final K1 key1, final K2 key2, final V value) {
    return mK1ToK2ToV.computeIfAbsent(key1, k -> new HashMap<>()).put(key2, value);
  }

  /**
   * Associates the specified value with the given map.
   *
   * @param key1        First key
   * @param key2ToValue Map to associate
   * @return The previous map associated with the given key, or <tt>null</tt> if
   *         there was no mapping for the key.
   */
  public Map<K2, V> put(final K1 key1, final Map<K2, V> key2ToValue) {
    return mK1ToK2ToV.put(key1, key2ToValue);
  }

  /**
   * Removes the mapping for the first key from this map if it is present.
   *
   * @param k1 The first key
   * @return The previous value associated with the first key, or <tt>null</tt>
   *         if there was no mapping for it.
   */
  public Map<K2, V> remove(final K1 k1) {
    return mK1ToK2ToV.remove(k1);
  }

  /**
   * Removes the mapping for the two keys from this map if it is present.
   *
   * @param k1 The first key
   * @param k2 The second key
   * @return The previous value associated with the two keys, or <tt>null</tt>
   *         if there was no mapping for it.
   */
  public V remove(final K1 k1, final K2 k2) {
    final Map<K2, V> k2ToV = mK1ToK2ToV.get(k1);
    if (k2ToV == null) {
      return null;
    }
    return k2ToV.remove(k2);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return mK1ToK2ToV.toString();
  }
}
