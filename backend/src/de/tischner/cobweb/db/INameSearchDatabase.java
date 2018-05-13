package de.tischner.cobweb.db;

import java.util.Collection;

/**
 * Interface for databases that provide data relevant for name search.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface INameSearchDatabase {
  /**
   * Gets node name data for all nodes in the database that have a name.
   *
   * @return Node name data for all nodes that have a name.
   */
  Collection<NodeNameData> getAllNodeNameData();
}
