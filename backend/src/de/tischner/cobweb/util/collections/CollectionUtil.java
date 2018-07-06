package de.tischner.cobweb.util.collections;

import java.util.Collection;

/**
 * Class providing utility methods for collections.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class CollectionUtil {
  /**
   * Increases the internal capacity of the given collection by appending
   * <tt>null</tt> values until the collection has the desired capacity.
   *
   * @param            <E> The type of the elements contained in the collection
   * @param collection The collection to increase
   * @param capacity   The desired capacity
   */
  public static <E> void increaseCapacity(final Collection<E> collection, final int capacity) {
    for (int i = collection.size(); i < capacity; i++) {
      collection.add(null);
    }
  }

  /**
   * Utility class. No implementation.
   */
  private CollectionUtil() {

  }
}
