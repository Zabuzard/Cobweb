package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

import de.tischner.cobweb.routing.algorithms.shortestpath.AShortestPathComputation;
import de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath;
import de.tischner.cobweb.routing.algorithms.shortestpath.EmptyPath;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.IPath;

public class Dijkstra<N extends INode, E extends IEdge<N>, G extends IGraph<N, E>>
    extends AShortestPathComputation<N, E> {

  @Override
  public Collection<N> computeSearchSpace(final Collection<N> sources, final N destination) {
    return computeShortestPathCostHelper(sources, null).keySet();
  }

  @Override
  public Optional<IPath<N, E>> computeShortestPath(final Collection<N> sources, final N destination) {
    final Map<N, TentativeDistance<N, E>> nodeToDistance = computeShortestPathCostHelper(sources, destination);
    final TentativeDistance<N, E> destinationDistance = nodeToDistance.get(destination);

    // Destination is not reachable from the given sources
    if (destinationDistance == null) {
      return Optional.empty();
    }

    E parentEdge = destinationDistance.getParentEdge();
    // Destination is already a source node
    if (parentEdge == null) {
      return Optional.of(new EmptyPath<>(destination));
    }

    // Build the path reversely by following the pointers from the destination to
    // one of the sources
    final EdgePath<N, E> path = new EdgePath<>(true);
    while (parentEdge != null) {
      // Add the edge and prepare next round
      path.addEdge(parentEdge);

      final N parent = parentEdge.getSource();
      parentEdge = nodeToDistance.get(parent).getParentEdge();
    }
    return Optional.of(path);
  }

  @Override
  public Optional<Double> computeShortestPathCost(final Collection<N> sources, final N destination) {
    final Map<N, TentativeDistance<N, E>> nodeToDistance = computeShortestPathCostHelper(sources, destination);
    return Optional.ofNullable(nodeToDistance.get(destination)).map(TentativeDistance::getEstimatedDistance);
  }

  @Override
  public Map<N, ? extends IHasPathCost> computeShortestPathCostsReachable(final Collection<N> sources) {
    return computeShortestPathCostHelper(sources, null);
  }

  protected Map<N, TentativeDistance<N, E>> computeShortestPathCostHelper(final Collection<N> sources,
      final N destination) {
    final boolean isDestinationPresent = destination == null;
    final Map<N, TentativeDistance<N, E>> nodeToDistance = new HashMap<>(sources.size());
    final Map<N, TentativeDistance<N, E>> nodeToSettledDistance = new HashMap<>(sources.size());
    final PriorityQueue<TentativeDistance<N, E>> activeNodes = new PriorityQueue<>(sources.size());

    // TODO Implement

    return nodeToSettledDistance;
  }

  protected boolean considerEdgeForRelaxation(final E edge, final N pathDestination) {
    // Dijkstras algorithm considers every outgoing edge.
    // This method may be used by extending classes to improve performance.
    return true;
  }

  protected double getEstimatedDistance(final N node, final N pathDestination) {
    // Dijkstras algorithm does not use estimations. It makes the worst possible
    // guess of 0 for every node.
    // This method may be used by extending classes to improve performance.
    return 0.0;
  }

}
