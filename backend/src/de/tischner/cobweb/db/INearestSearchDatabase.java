package de.tischner.cobweb.db;

import java.util.Optional;

/**
 * Interface for databases that provide data relevant for nearest search.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface INearestSearchDatabase {
  /**
   * Attempts to get the unique OSM ID of a node by its internal ID.
   *
   * @param internalId The unique internal ID of the node
   * @return The unique OSM ID of the node or empty if no node with that
   *         internal ID could be found
   */
  Optional<Long> getOsmNodeByInternal(int internalId);
}
