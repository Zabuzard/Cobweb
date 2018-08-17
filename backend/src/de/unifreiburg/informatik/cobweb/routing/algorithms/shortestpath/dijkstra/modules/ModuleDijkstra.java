package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import java.util.HashSet;
import java.util.OptionalDouble;
import java.util.Set;

import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IGraph;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;

/**
 * A Dijkstra algorithm for shortest path computation that can be modified by
 * using modules.<br>
 * <br>
 * Use {@link #addModule(IModule)} and {@link #removeModule(IModule)} to
 * register and unregister modules. Alternatively use the factory method
 * {@link #of(IGraph, IModule...)} for convenient instance creation.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the nodes
 * @param <E> Type of the edges
 */
public final class ModuleDijkstra<N extends INode, E extends IEdge<N>> extends Dijkstra<N, E> {
  /**
   * Creates a new module Dijkstra instance routing on the given graph and using
   * the given modules.
   *
   * @param         <N> Type of the nodes
   * @param         <E> Type of the edges
   * @param graph   The graph to route on
   * @param modules The modules to use
   * @return The created module Dijkstra instance
   */
  @SafeVarargs
  public static <N extends INode, E extends IEdge<N>> ModuleDijkstra<N, E> of(final IGraph<N, E> graph,
      final IModule<N, E>... modules) {
    final ModuleDijkstra<N, E> moduleDijkstra = new ModuleDijkstra<>(graph);
    if (modules != null) {
      for (final IModule<N, E> module : modules) {
        moduleDijkstra.addModule(module);
      }
    }
    return moduleDijkstra;
  }

  /**
   * The modules to use.
   */
  private final Set<IModule<N, E>> mModules;

  /**
   * Creates a new module Dijkstra instance routing on the given graph.
   *
   * @param graph The graph to route on
   */
  public ModuleDijkstra(final IGraph<N, E> graph) {
    super(graph);
    mModules = new HashSet<>();
  }

  /**
   * Adds the given module.
   *
   * @param module The module to add
   */
  public void addModule(final IModule<N, E> module) {
    mModules.add(module);
  }

  /**
   * Removes the given module.
   *
   * @param module The module to remove
   */
  public void removeModule(final IModule<N, E> module) {
    mModules.remove(module);
  }

  /**
   * Whether or not the given edge should be considered for relaxation. The
   * algorithm will ignore the edge and not follow it if this method returns
   * <tt>false</tt>.<br>
   * <br>
   * This will be the case if any modules
   * {@link IModule#considerEdgeForRelaxation(IEdge, INode)} method returns
   * <tt>false</tt>.
   */
  @Override
  protected boolean considerEdgeForRelaxation(final E edge, final N pathDestination) {
    // Ignore the base, it always considers all edges
    // Ask all modules and accumulate with logical and
    for (final IModule<N, E> module : mModules) {
      final boolean consider = module.considerEdgeForRelaxation(edge, pathDestination);
      if (!consider) {
        return false;
      }
    }

    return true;
  }

  /**
   * Gets an estimate about the shortest path distance from the given node to
   * the destination of the shortest path computation.<br>
   * <br>
   * Therefore, {@link IModule#getEstimatedDistance(INode, INode)} is called on
   * all modules and the greatest estimate is chosen. If there is no module
   * estimate the method falls back to the base implementation.
   */
  @Override
  protected double getEstimatedDistance(final N node, final N pathDestination) {
    // Choose greatest estimate
    final OptionalDouble maxEstimate =
        mModules.stream().map(module -> module.getEstimatedDistance(node, pathDestination))
            .filter(OptionalDouble::isPresent).mapToDouble(OptionalDouble::getAsDouble).max();
    if (maxEstimate.isPresent()) {
      return maxEstimate.getAsDouble();
    }

    // Fallback to base implementation
    return super.getEstimatedDistance(node, pathDestination);
  }

  /**
   * Provides the cost of a given edge.<br>
   * <br>
   * Therefore, {@link IModule#provideEdgeCost(IEdge, double)} is called on all
   * modules and the greatest cost is chosen. If no module provides a cost the
   * method falls back to the base implementation.
   */
  @Override
  protected double provideEdgeCost(final E edge, final double tentativeDistance) {
    // Choose greatest cost
    final OptionalDouble maxEdgeCost = mModules.stream().map(module -> module.provideEdgeCost(edge, tentativeDistance))
        .filter(OptionalDouble::isPresent).mapToDouble(OptionalDouble::getAsDouble).max();
    if (maxEdgeCost.isPresent()) {
      return maxEdgeCost.getAsDouble();
    }

    // Fallback to base implementation
    return super.provideEdgeCost(edge, tentativeDistance);
  }

  /**
   * Whether or not the algorithm should abort computation of the shortest path.
   * The method is called right after the given node has been settled.<br>
   * <br>
   * This will be the case if any modules
   * {@link IModule#shouldAbort(TentativeDistance)} method returns
   * <tt>true</tt>.
   *
   * @param tentativeDistance The tentative distance wrapper of the node that
   *                          was settled
   * @return <tt>True</tt> if the computation should be aborted, <tt>false</tt>
   *         if not
   */
  @Override
  protected boolean shouldAbort(final TentativeDistance<N, E> tentativeDistance) {
    // Ignore the base, it never aborts computation
    // Ask all modules and accumulate with logical or
    for (final IModule<N, E> module : mModules) {
      final boolean abort = module.shouldAbort(tentativeDistance);
      if (abort) {
        return true;
      }
    }

    return false;
  }

}
