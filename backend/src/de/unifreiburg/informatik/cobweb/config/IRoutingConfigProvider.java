package de.unifreiburg.informatik.cobweb.config;

import java.nio.file.Path;

import de.unifreiburg.informatik.cobweb.routing.model.ERoutingModelMode;

/**
 * Interface for classes that provide routing related configuration settings.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IRoutingConfigProvider {
  /**
   * Gets the travel time in seconds after which to abort shortest path
   * computation.
   *
   * @return The travel time in seconds after which to abort shortest path
   *         computation
   */
  int getAbortTravelTime();

  /**
   * Gets the maximal allowed amount of access nodes to use when transferring
   * from a road node to a transit stop.
   *
   * @return The maximal allowed amount of access nodes to use when transferring
   *         from a road node to a transit stop
   */
  int getAccessNodesMaximum();

  /**
   * Gets the amount of landmarks to use for the landmark heuristic.
   *
   * @return The amount of landmarks to use for the landmark heuristic
   */
  int getAmountOfLandmarks();

  /**
   * Gets the range in meters stops should get connected by footpaths.
   *
   * @return The range in meters stops should get connected by footpaths
   */
  int getFootpathReachability();

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
   * The mode to use for the routing model.
   *
   * @return The mode to use for the routing model
   */
  ERoutingModelMode getRoutingModelMode();

  /**
   * Gets the port used by the routing server.
   *
   * @return The port used by the routing server
   */
  int getRoutingServerPort();

  /**
   * Gets the amount in seconds a transfer at the same stop takes.
   *
   * @return The amount in seconds a transfer at the same stop takes
   */
  int getTransferDelay();

  /**
   * Whether or not the graph cache should be used.
   *
   * @return <tt>True</tt> if the graph cache should be used, <tt>false</tt>
   *         otherwise
   */
  boolean useGraphCache();
}
