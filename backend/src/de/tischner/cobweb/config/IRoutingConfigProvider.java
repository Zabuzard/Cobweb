package de.tischner.cobweb.config;

import java.nio.file.Path;

/**
 * Interface for classes that provide routing related configuration settings.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IRoutingConfigProvider {
  /**
   * Gets the path to the graph cache. Is used to serialize and deserialize a
   * graph.
   *
   * @return The path to the graph cache
   */
  Path getGraphCache();

  /**
   * Gets the path to the graph cache info object that stores information about
   * the content of the graph cache. Can be used to determine which data files
   * need to be considered when parsing in order to avoid pushing the same data
   * again.
   *
   * @return The path to the graph cache info object
   */
  Path getGraphCacheInfo();

  /**
   * Gets the path to the filter used to filter OSM roads.
   *
   * @return The path to the filter
   */
  Path getOsmRoadFilter();

  /**
   * Gets the port used by the routing server.
   *
   * @return The port used by the routing server
   */
  int getRoutingServerPort();

  /**
   * Whether or not the graph cache should be used.
   *
   * @return <tt>True</tt> if the graph cache should be used, <tt>false</tt>
   *         otherwise
   */
  boolean useGraphCache();
}
