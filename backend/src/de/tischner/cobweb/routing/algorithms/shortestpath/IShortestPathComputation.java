package de.tischner.cobweb.routing.algorithms.shortestpath;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.IHasPathCost;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.IPath;

/**
 * Interface for algorithms that are able to compute shortest paths from a
 * source to a destination.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of node
 * @param <E> Type of edge
 */
public interface IShortestPathComputation<N extends INode, E extends IEdge<N>> {
  /**
   * Computes and returns a collection of all nodes that were visited by the
   * algorithm while computing the shortest path from the given sources to the
   * given destination. This is known as <i>search space</i> and is primarily
   * used for debugging and benchmarking.<br>
   * <br>
   * The shortest path from multiple sources is the minimal shortest path for
   * all source nodes individually.
   *
   * @param sources     The sources to compute the shortest path from
   * @param destination The destination to compute the shortest path to
   * @return A collection of all visited nodes, the <i>search space</i>
   */
  Collection<N> computeSearchSpace(Collection<N> sources, N destination);

  /**
   * Computes and returns a collection of all nodes that were visited by the
   * algorithm while computing the shortest path from the given source to the
   * given destination. This is known as <i>search space</i> and is primarily
   * used for debugging and benchmarking.
   *
   * @param source      The source to compute the shortest path from
   * @param destination The destination to compute the shortest path to
   * @return A collection of all visited nodes, the <i>search space</i>
   */
  Collection<N> computeSearchSpace(N source, N destination);

  /**
   * Computes the shortest path from the given sources to the given
   * destination.<br>
   * <br>
   * The shortest path from multiple sources is the minimal shortest path for
   * all source nodes individually.
   *
   * @param sources     The sources to compute the shortest path from
   * @param destination The destination to compute the shortest path to
   * @return The shortest path if present, else empty
   */
  Optional<IPath<N, E>> computeShortestPath(Collection<N> sources, N destination);

  /**
   * Computes the shortest path from the given source to the given destination.
   *
   * @param source      The source to compute the shortest path from
   * @param destination The destination to compute the shortest path to
   * @return The shortest path if present, else empty
   */
  Optional<IPath<N, E>> computeShortestPath(N source, N destination);

  /**
   * Computes the cost of the shortest path from the given sources to the given
   * destination.<br>
   * <br>
   * The shortest path from multiple sources is the minimal shortest path for
   * all source nodes individually.
   *
   * @param sources     The sources to compute the shortest path from
   * @param destination The destination to compute the shortest path to
   * @return The cost of the shortest path if present, else empty
   */
  Optional<Double> computeShortestPathCost(Collection<N> sources, N destination);

  /**
   * Computes the cost of the shortest path from the given source to the given
   * destination.
   *
   * @param source      The source to compute the shortest path from
   * @param destination The destination to compute the shortest path to
   * @return The cost of the shortest path if present, else empty
   */
  Optional<Double> computeShortestPathCost(N source, N destination);

  /**
   * Computes the costs of all shortest paths from the given sources to all
   * other nodes.<br>
   * <br>
   * The shortest path from multiple sources is the minimal shortest path for
   * all source nodes individually.
   *
   * @param sources The sources to compute the shortest path from
   * @return A map which connects destination nodes to the costs of their
   *         shortest path
   */
  Map<N, ? extends IHasPathCost> computeShortestPathCostsReachable(Collection<N> sources);

  /**
   * Computes the costs of all shortest paths from the given source to all other
   * nodes.
   *
   * @param source The source to compute the shortest path from
   * @return A map which connects destination nodes to the costs of their
   *         shortest path
   */
  Map<N, ? extends IHasPathCost> computeShortestPathCostsReachable(N source);
}
