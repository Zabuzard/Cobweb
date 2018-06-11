package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.TentativeDistance;
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
    final boolean base = super.considerEdgeForRelaxation(edge, pathDestination);
    if (!base) {
      return false;
    }

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
    final double base = super.getEstimatedDistance(node, pathDestination);
    return DoubleStream
        .concat(DoubleStream.of(base),
            mModules.stream().mapToDouble(module -> module.getEstimatedDistance(node, pathDestination)))
        .max().getAsDouble();
  }

  @Override
  protected double provideEdgeCost(final E edge, final double tentativeDistance) {
    // Choose greatest cost
    final double base = super.provideEdgeCost(edge, tentativeDistance);
    return DoubleStream
        .concat(DoubleStream.of(base),
            mModules.stream().mapToDouble(module -> module.provideEdgeCost(edge, tentativeDistance)))
        .max().getAsDouble();
  }

  @Override
  protected Stream<E> provideEdgesToRelax(final TentativeDistance<N, E> tentativeDistance) {
    Stream<E> edges = super.provideEdgesToRelax(tentativeDistance);
    // Apply all modules
    for (final IModule<N, E> module : mModules) {
      edges = module.provideEdgesToRelax(edges, tentativeDistance);
    }
    return edges;
  }

}
