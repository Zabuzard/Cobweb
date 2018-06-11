package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import java.util.stream.Stream;

import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;

public interface IModule<N extends INode, E extends IEdge<N>> {
  /**
   * Whether or not the given edge should be considered for relaxation. The
   * algorithm will ignore the edge and not follow it if this method returns
   * <tt>false</tt>.
   *
   * @param edge            The edge in question
   * @param pathDestination The destination of the shortest path computation or
   *                        <tt>null</tt> if not present
   * @return <tt>True</tt> if the edge should be considered, <tt>false</tt>
   *         otherwise
   */
  default boolean considerEdgeForRelaxation(@SuppressWarnings("unused") final E edge,
      @SuppressWarnings("unused") final N pathDestination) {
    return true;
  }

  /**
   * Gets an estimate about the shortest path distance from the given node to
   * the destination of the shortest path computation.<br>
   * <br>
   * The estimate must be <i>monotone</i> and <i>admissible</i>.
   *
   * @param node            The node to estimate the distance from
   * @param pathDestination The destination to estimate the distance to
   * @return An estimate about the shortest path distance
   */
  default double getEstimatedDistance(@SuppressWarnings("unused") final N node,
      @SuppressWarnings("unused") final N pathDestination) {
    return 0.0;
  }

  /**
   * Provides the cost of an given edge.<br>
   * <br>
   * The base is the result of {@link E#getCost()}. Implementations are allowed
   * to override this method in order to modify the cost.
   *
   * @param edge              The edge whose cost to provide
   * @param tentativeDistance The current tentative distance when relaxing this
   *                          edge
   * @return Stream of edges to process for relaxation
   */
  default double provideEdgeCost(final E edge, @SuppressWarnings("unused") final double tentativeDistance) {
    return edge.getCost();
  }

  /**
   * Generates a stream of edges to process for relaxation.<br>
   * <br>
   * The base usually are all outgoing edges of the given node. The method is
   * used in order to further filter the stream. Additionally, the method
   * {@link #considerEdgeForRelaxation(IEdge, INode)} will be called on each
   * element of this stream.
   *
   * @param base              The base stream of edges
   * @param tentativeDistance The tentative distance wrapper of the node to
   *                          relax edges of
   * @return Stream of edges to process for relaxation
   */
  default Stream<E> provideEdgesToRelax(final Stream<E> base,
      @SuppressWarnings("unused") final TentativeDistance<N, E> tentativeDistance) {
    return base;
  }
}
