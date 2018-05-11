package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra;

/**
 * Interface for classes that provide path costs.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
@FunctionalInterface
public interface IHasPathCost {
  /**
   * Gets the path cost.
   *
   * @return The cost of the path
   */
  double getPathCost();
}
