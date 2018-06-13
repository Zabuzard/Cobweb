package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import java.util.HashSet;
import java.util.OptionalDouble;
import java.util.Set;

import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;

public final class ModuleDijkstra<N extends INode, E extends IEdge<N>> extends Dijkstra<N, E> {
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

  private final Set<IModule<N, E>> mModules;

  public ModuleDijkstra(final IGraph<N, E> graph) {
    super(graph);
    mModules = new HashSet<>();
  }

  public void addModule(final IModule<N, E> module) {
    mModules.add(module);
  }

  public void removeModule(final IModule<N, E> module) {
    mModules.remove(module);
  }

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

}
