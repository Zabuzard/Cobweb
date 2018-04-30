package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra;

import de.tischner.cobweb.routing.algorithms.metrics.IMetric;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;

public final class AStar<N extends INode, E extends IEdge<N>, G extends IGraph<N, E>> extends Dijkstra<N, E, G> {

  private final IMetric<N> mMetric;

  public AStar(final G graph, final IMetric<N> metric) {
    super(graph);
    mMetric = metric;
  }

  @Override
  protected double getEstimatedDistance(final N node, final N pathDestination) {
    return mMetric.distance(node, pathDestination);
  }

}
