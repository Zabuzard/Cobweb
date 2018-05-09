package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;

/**
 * Tentative distance container for a given node. The container consists of a
 * node, the parent edge that lead to this node, a tentative distance and
 * optionally an estimate for the remaining distance.<br>
 * <br>
 * The parent edge can be used for backtracking, for example to construct a
 * shortest path. The tentative distance is the distance from a source to that
 * node, i.e. the sum of the edge costs when backtracking the parent edges to
 * the source. The estimated distance is from this node to the desired
 * destination, if present.<br>
 * <br>
 * The natural ordering of this container sums up the tentative distance and the
 * distance estimate and then compares ascending, i.e. smaller values first.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 * @param <N> Type of the node
 * @param <E> Type of the edge
 */
public final class TentativeDistance<N extends INode, E extends IEdge<N>>
    implements IHasPathCost, Comparable<TentativeDistance<N, E>> {
  /**
   * The estimated distance from this node to the desired destination or
   * <tt>0.0</tt> if not present.
   */
  private final double mEstimatedDistance;
  /**
   * The node this container wraps around.
   */
  private final N mNode;

  /**
   * The parent edge that lead to this node.
   */
  private final E mParentEdge;
  /**
   * The tentative distance from a source to this node.
   */
  private final double mTentativeDistance;

  /**
   * Creates a new tentative distance container for the given node. This
   * constructor sets the estimated distance to <tt>0.0</tt> which should be used
   * whenever there is no desired destination.
   *
   * @param node              The node to wrap around
   * @param parentEdge        The parent edge that lead to this node
   * @param tentativeDistance The tentative distance from a source to this node,
   *                          i.e. the sum of the edge costs when backtracking the
   *                          parent edges to the source
   */
  public TentativeDistance(final N node, final E parentEdge, final double tentativeDistance) {
    this(node, parentEdge, tentativeDistance, 0.0);
  }

  /**
   * Creates a new tentative distance container for the given node.
   *
   * @param node              The node to wrap around
   * @param parentEdge        The parent edge that lead to this node
   * @param tentativeDistance The tentative distance from a source to this node,
   *                          i.e. the sum of the edge costs when backtracking the
   *                          parent edges to the source
   * @param estimatedDistance An estimate about the distance from this node to the
   *                          desired destination, the guess must be
   *                          <i>monotone</i> and <i>admissible</i>
   */
  public TentativeDistance(final N node, final E parentEdge, final double tentativeDistance,
      final double estimatedDistance) {
    mNode = node;
    mParentEdge = parentEdge;
    mTentativeDistance = tentativeDistance;
    mEstimatedDistance = estimatedDistance;
  }

  /**
   * The natural ordering of this container sums up the tentative distance and the
   * distance estimate and then compares ascending, i.e. smaller values first.
   */
  @Override
  public int compareTo(final TentativeDistance<N, E> other) {
    return Double.compare(mTentativeDistance + mEstimatedDistance, other.mTentativeDistance + other.mEstimatedDistance);
  }

  /**
   * Gets an estimate about the distance from this node to the desired
   * destination, the guess is <i>monotone</i> and <i>admissible</i>.
   *
   * @return The estimated distance
   */
  public double getEstimatedDistance() {
    return mEstimatedDistance;
  }

  /**
   * Gets the node this container wraps around.
   *
   * @return The node to get
   */
  public N getNode() {
    return mNode;
  }

  /**
   * Gets the parent edge that lead to this node. Can be used for backtracking,
   * for example for shortest path construction.
   *
   * @return The parent edge that lead to this node
   */
  public E getParentEdge() {
    return mParentEdge;
  }

  /**
   * Gets the tentative distance from a source to this node, i.e. the sum of the
   * edge costs when backtracking the parent edges to the source.
   *
   * @return The tentative distance
   */
  @Override
  public double getPathCost() {
    return mTentativeDistance;
  }

  /**
   * Gets the tentative distance from a source to this node, i.e. the sum of the
   * edge costs when backtracking the parent edges to the source.
   *
   * @return The tentative distance
   */
  public double getTentativeDistance() {
    return mTentativeDistance;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("TentativeDistance [node=");
    builder.append(mNode);
    builder.append(", parentEdge=");
    builder.append(mParentEdge);
    builder.append(", tentativeDistance=");
    builder.append(mTentativeDistance);
    builder.append(", estimatedDistance=");
    builder.append(mEstimatedDistance);
    builder.append("]");
    return builder.toString();
  }

}
