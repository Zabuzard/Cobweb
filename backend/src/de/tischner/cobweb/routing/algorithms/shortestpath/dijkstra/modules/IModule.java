package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import java.util.OptionalDouble;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;

/**
 * Interface for Dijkstra modules used by {@link ModuleDijkstra}. Defines
 * various methods that allow to manipulate how the base Dijkstra works, like
 * providing edge costs different to {@link IEdge#getCost()}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the nodes
 * @param <E> Type of the edges
 */
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
   * @return An estimate about the shortest path distance or empty if the module
   *         has no estimate
   */
  default OptionalDouble getEstimatedDistance(@SuppressWarnings("unused") final N node,
      @SuppressWarnings("unused") final N pathDestination) {
    return OptionalDouble.empty();
  }

  /**
   * Provides the cost of a given edge.<br>
   * <br>
   * The base is the result of {@link IEdge#getCost()}. Implementations are
   * allowed to override this method in order to modify the cost.
   *
   * @param edge              The edge whose cost to provide
   * @param tentativeDistance The current tentative distance when relaxing this
   *                          edge
   * @return The cost of the given edge or empty if the module does not provide
   *         a cost
   */
  default OptionalDouble provideEdgeCost(@SuppressWarnings("unused") final E edge,
      @SuppressWarnings("unused") final double tentativeDistance) {
    return OptionalDouble.empty();
  }
}
