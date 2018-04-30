package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;

public final class TentativeDistance<N extends INode, E extends IEdge<N>>
    implements IHasPathCost, Comparable<TentativeDistance<N, E>> {

  private final double mEstimatedDistance;
  private final N mNode;
  private final E mParentEdge;
  private final double mTentativeDistance;

  public TentativeDistance(final N node, final E parentEdge, final double distance) {
    this(node, parentEdge, distance, 0.0);
  }

  public TentativeDistance(final N node, final E parentEdge, final double tentativeDistance,
      final double estimatedDistance) {
    mNode = node;
    mParentEdge = parentEdge;
    mTentativeDistance = tentativeDistance;
    mEstimatedDistance = estimatedDistance;
  }

  @Override
  public int compareTo(final TentativeDistance<N, E> other) {
    return Double.compare(mTentativeDistance + mEstimatedDistance, other.mTentativeDistance + other.mEstimatedDistance);
  }

  public double getEstimatedDistance() {
    return mEstimatedDistance;
  }

  public N getNode() {
    return mNode;
  }

  public E getParentEdge() {
    return mParentEdge;
  }

  @Override
  public double getPathCost() {
    return mTentativeDistance;
  }

  public double getTentativeDistance() {
    return mTentativeDistance;
  }

}
