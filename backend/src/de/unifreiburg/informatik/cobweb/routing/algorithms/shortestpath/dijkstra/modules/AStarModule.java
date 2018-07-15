package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import java.util.OptionalDouble;

import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.IMetric;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;

/**
 * Implementation of the A-Star algorithm as {@link IModule} for a
 * {@link ModuleDijkstra} that speedups shortest path communication on graphs by
 * estimating the distance between nodes using a heuristic metric.<br>
 * <br>
 * The heuristic metric must be <i>monotone</i> and <i>admissible</i>.<br>
 * <br>
 * The factory method {@link #of(IMetric)} can be used for convenient instance
 * creation.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of node
 * @param <E> Type of edge
 */
public final class AStarModule<N extends INode, E extends IEdge<N>> implements IModule<N, E> {
  /**
   * Creates an AStar algorithm instance using the given metric.
   *
   * @param        <N> Type of the node
   * @param        <E> Type of the edge
   * @param metric The metric to use
   * @return The created AStar algorithm instance
   */
  public static <N extends INode, E extends IEdge<N>> AStarModule<N, E> of(final IMetric<N> metric) {
    return new AStarModule<>(metric);
  }

  /**
   * The heuristic metric to use.
   */
  private final IMetric<N> mMetric;

  /**
   * Creates a new A-Star algorithm module which uses the given heuristic
   * metric.
   *
   * @param metric The heuristic metric which must be <i>monotone</i> and
   *               <i>admissible</i>
   */
  public AStarModule(final IMetric<N> metric) {
    mMetric = metric;
  }

  /**
   * Gets an estimate about the shortest path distance from the given node to
   * the destination of the shortest path computation.<br>
   * <br>
   * Therefore, it estimates the distance by using the given metric.
   */
  @Override
  public OptionalDouble getEstimatedDistance(final N node, final N pathDestination) {
    return OptionalDouble.of(mMetric.distance(node, pathDestination));
  }

}
