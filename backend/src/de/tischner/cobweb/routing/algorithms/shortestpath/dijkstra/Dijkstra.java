package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

import de.tischner.cobweb.routing.algorithms.shortestpath.AShortestPathComputation;
import de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath;
import de.tischner.cobweb.routing.algorithms.shortestpath.EmptyPath;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.IPath;

public class Dijkstra<N extends INode, E extends IEdge<N>, G extends IGraph<N, E>>
    extends AShortestPathComputation<N, E> {

  private final G mGraph;

  public Dijkstra(final G graph) {
    mGraph = graph;
  }

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

  private TentativeDistance<N, E> createDistance(final N node, final E parentEdge, final double tentativeDistance,
      final N pathDestination) {
    if (pathDestination == null) {
      return new TentativeDistance<>(node, parentEdge, tentativeDistance);
    }

    final double estimatedDistance = getEstimatedDistance(node, pathDestination);
    return new TentativeDistance<>(node, parentEdge, tentativeDistance, estimatedDistance);
  }

  protected Map<N, TentativeDistance<N, E>> computeShortestPathCostHelper(final Collection<N> sources,
      final N pathDestination) {
    // NOTE If RAM allows, the maps could be exchanged by arrays after mapping the
    // real node IDs to dummy IDs without gaps.
    final Map<N, TentativeDistance<N, E>> nodeToDistance = new HashMap<>(sources.size());
    final Map<N, TentativeDistance<N, E>> nodeToSettledDistance = new HashMap<>(sources.size());
    final PriorityQueue<TentativeDistance<N, E>> activeNodes = new PriorityQueue<>(sources.size());

    // Sources are initial active nodes
    for (final N source : sources) {
      // Create a container for the node
      final TentativeDistance<N, E> distance = createDistance(source, null, 0.0, pathDestination);
      // Put the distance as active node
      nodeToDistance.put(source, distance);
      activeNodes.add(distance);
    }

    // Poll and settle all active nodes
    while (!activeNodes.isEmpty()) {
      final TentativeDistance<N, E> distance = activeNodes.poll();
      final N node = distance.getNode();
      final double tentativeDistance = distance.getTentativeDistance();

      // Skip the element if the node was already settled before. In that case there
      // was a better path to this node around and this path was abandoned.
      if (nodeToSettledDistance.containsKey(node)) {
        continue;
      }

      // Settle the current node
      nodeToSettledDistance.put(node, distance);

      // End the algorithm if destination was settled
      if (pathDestination != null && node.equals(pathDestination)) {
        break;
      }

      // Relax all outgoing edges
      final Set<E> edges = mGraph.getOutgoingEdges(node);
      for (final E edge : edges) {
        // Skip the edge if it should not be considered
        if (!considerEdgeForRelaxation(edge, pathDestination)) {
          continue;
        }

        final N destination = edge.getDesintation();
        final double tentativeEdgeDistance = tentativeDistance + edge.getCost();

        // Check if the destination is visited for the first time
        TentativeDistance<N, E> destinationDistance = nodeToDistance.get(destination);
        if (destinationDistance == null) {
          // Create a container for the destination
          destinationDistance = createDistance(destination, edge, tentativeEdgeDistance, pathDestination);

          // Put the distance as active node
          nodeToDistance.put(destination, destinationDistance);
          activeNodes.add(destinationDistance);

          // Relaxation has finished
          continue;
        }

        // Destination is not visited for the first time
        // Don't relax if the node was already settled
        if (nodeToSettledDistance.containsKey(destination)) {
          continue;
        }

        // Don't relax if the edge does not improve the distance to this destination
        if (tentativeEdgeDistance >= destinationDistance.getTentativeDistance()) {
          continue;
        }

        // Improve the distance by replacing the old container with a new one
        // representing the path taken by this edge
        destinationDistance = createDistance(destination, edge, tentativeEdgeDistance, pathDestination);
        // Replace the old distance by the new one and set as active
        nodeToDistance.put(destination, destinationDistance);
        activeNodes.add(destinationDistance);
      }
    }

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
