package de.unifreiburg.informatik.cobweb.routing.model.graph;

/**
 * POJO class that wraps an edge and cost. Can be used to group a different cost
 * than the default cost provided by the edge with the edge.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the node
 * @param <E> The type of the edge
 */
public final class EdgeCost<N extends INode, E extends IEdge<N>> {
  /**
   * The cost of this element.
   */
  private final double mCost;
  /**
   * The edge of this element.
   */
  private final E mEdge;

  /**
   * Creates a new edge cost element.
   *
   * @param edge The edge of this element
   * @param cost The cost of this element
   */
  public EdgeCost(final E edge, final double cost) {
    mEdge = edge;
    mCost = cost;
  }

  /**
   * Gets the cost.
   *
   * @return The cost to get
   */
  public double getCost() {
    return mCost;
  }

  /**
   * Gets the edge.
   *
   * @return The edge to get
   */
  public E getEdge() {
    return mEdge;
  }
}
