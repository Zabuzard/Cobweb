package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra;

import de.tischner.cobweb.routing.algorithms.metrics.IMetric;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;

/**
 * Implementation of the A-Star algorithm that speedups shortest path
 * communication on graphs by estimating the distance between nodes using a
 * heuristic metric.<br>
 * <br>
 * The heuristic metric must be <i>monotone</i> and <i>admissible</i>.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of node
 * @param <E> Type of edge
 * @param <G> Type of graph
 */
public final class AStar<N extends INode, E extends IEdge<N>, G extends IGraph<N, E>> extends Dijkstra<N, E, G> {
  /**
   * The heuristic metric to use.
   */
  private final IMetric<N> mMetric;

  /**
   * Creates a new A-Star algorithm which operates on the given graph and uses
   * the given heuristic metric.
   *
   * @param graph  The graph to operate on
   * @param metric The heuristic metric which must be <i>monotone</i> and
   *               <i>admissible</i>
   */
  public AStar(final G graph, final IMetric<N> metric) {
    super(graph);
    mMetric = metric;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra#
   * getEstimatedDistance(de.tischner.cobweb.routing.model.graph.INode,
   * de.tischner.cobweb.routing.model.graph.INode)
   */
  @Override
  protected double getEstimatedDistance(final N node, final N pathDestination) {
    return mMetric.distance(node, pathDestination);
  }

}
