package de.tischner.cobweb.routing.algorithms.shortestpath;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.IHasPathCost;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.IPath;

public abstract class AShortestPathComputation<N extends INode, E extends IEdge<N>>
    implements IShortestPathComputation<N, E> {

  @Override
  public Collection<N> computeSearchSpace(final N source, final N destination) {
    return computeSearchSpace(Collections.singletonList(source), destination);
  }

  @Override
  public Optional<IPath<N, E>> computeShortestPath(final N source, final N destination) {
    return computeShortestPath(Collections.singletonList(source), destination);
  }

  @Override
  public Optional<Double> computeShortestPathCost(final N source, final N destination) {
    return computeShortestPathCost(Collections.singletonList(source), destination);
  }

  @Override
  public Map<N, ? extends IHasPathCost> computeShortestPathCostsReachable(final N source) {
    return computeShortestPathCostsReachable(Collections.singletonList(source));
  }

}
