package de.tischner.cobweb.routing.algorithms.shortestpath;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.IHasPathCost;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.IPath;

public interface IShortestPathComputation<N extends INode, E extends IEdge<N>> {
  Collection<N> computeSearchSpace(Collection<N> sources, N destination);

  Collection<N> computeSearchSpace(N source, N destination);

  Optional<IPath<N, E>> computeShortestPath(Collection<N> sources, N destination);

  Optional<IPath<N, E>> computeShortestPath(N source, N destination);

  Optional<Double> computeShortestPathCost(Collection<N> sources, N destination);

  Optional<Double> computeShortestPathCost(N source, N destination);

  Map<N, ? extends IHasPathCost> computeShortestPathCostsReachable(Collection<N> sources);

  Map<N, ? extends IHasPathCost> computeShortestPathCostsReachable(N source);
}
