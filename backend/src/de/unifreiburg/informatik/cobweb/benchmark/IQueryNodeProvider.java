package de.unifreiburg.informatik.cobweb.benchmark;

import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;

/**
 * Interface for classes that provide nodes to query on.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
@FunctionalInterface
public interface IQueryNodeProvider {
  /**
   * Gets a random query node.
   *
   * @return A random query node
   */
  ICoreNode getQueryNode();
}
