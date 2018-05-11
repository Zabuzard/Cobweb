package de.tischner.cobweb.routing.model.graph;

/**
 * Interface for a weighted directed edge that connects two nodes.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 */
public interface IEdge<N extends INode> {
  /**
   * The cost of the edge, i.e. its weight.
   *
   * @return The cost of the edge
   */
  double getCost();

  /**
   * The destination node of the edge.
   *
   * @return The destination node of the edge
   */
  N getDestination();

  /**
   * The source node of the edge.
   *
   * @return The source node of the edge
   */
  N getSource();
}
