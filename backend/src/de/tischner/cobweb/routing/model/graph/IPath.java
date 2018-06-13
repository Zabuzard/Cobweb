package de.tischner.cobweb.routing.model.graph;

/**
 * Interface for a path that consists of a source and destination node and edges
 * which lead to the destination.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 */
public interface IPath<N extends INode, E extends IEdge<N>> extends Iterable<EdgeCost<N, E>> {
  /**
   * Gets the destination node of the path.
   *
   * @return The destination node of the path
   */
  N getDestination();

  /**
   * Gets the source node of the path.
   *
   * @return The source node of the path
   */
  N getSource();

  /**
   * Gets the total cost of the path. That is the sum of all edge costs the path
   * consists of.
   *
   * @return The total cost of the path
   */
  double getTotalCost();

  /**
   * Gets the length of the path, i.e. the amount of edges it consists of.
   *
   * @return The length of the path
   */
  int length();
}
