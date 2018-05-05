package de.tischner.cobweb.routing.algorithms.metrics.landmark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.RandomAccess;

import de.tischner.cobweb.routing.algorithms.shortestpath.IShortestPathComputation;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.IHasPathCost;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;

/**
 * Implementation of a landmark provider that greedily selects landmarks that
 * are farthest away from each other.<br>
 * <br>
 * The resulting set of landmarks is thus distributed well along the graph.
 * Distances are computed by using a {@link IShortestPathComputation} on the
 * whole graph for every landmark. Thus, depending on the graph size and the
 * amount of landmarks, the landmark selection might take a while.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 * @param <N> Type of the nodes and landmarks
 * @param <E> Type of the edges
 * @param <G> Type of the graph
 */
public final class GreedyFarthestLandmarks<N extends INode, E extends IEdge<N>, G extends IGraph<N, E>>
    implements ILandmarkProvider<N> {
  /**
   * Algorithm to use for computing distances between nodes.
   */
  private final IShortestPathComputation<N, E> mComputation;
  /**
   * The graph to operate on.
   */
  private final G mGraph;
  /**
   * The random number generator to use for selection of the first landmark.
   */
  private final Random mRandom;

  /**
   * Creates a new landmark provider which generates landmarks on the given graph.
   *
   * @param graph The graph to select landmarks from
   */
  public GreedyFarthestLandmarks(final G graph) {
    mGraph = graph;
    mRandom = new Random();
    mComputation = new Dijkstra<>(graph);
  }

  /**
   * Greedily selects nodes from the graph as landmarks that are farthest away
   * from each other.<br>
   * <br>
   * The resulting collection of landmarks is thus distributed well along the
   * graph. Distances are computed by using a {@link IShortestPathComputation} on
   * the whole graph for every landmark. Thus, depending on the graph size and the
   * amount of landmarks, the landmark selection might take a while.
   */
  @Override
  public Collection<N> getLandmarks(final int amount) {
    int amountToUse = amount;
    if (amount > mGraph.size()) {
      amountToUse = mGraph.size();
    }

    final Collection<N> landmarks = new ArrayList<>(amountToUse);
    final Collection<N> nodes = mGraph.getNodes();

    // Choose the first landmark randomly
    final int index = mRandom.nextInt(nodes.size());
    // If the nodes support RandomAccess, fetch it directly
    if (nodes instanceof RandomAccess && nodes instanceof List) {
      landmarks.add(((List<N>) nodes).get(index));
    } else {
      // Iterate to the node and skip previous values
      final Iterator<N> nodeIter = nodes.iterator();
      for (int i = 0; i < index; i++) {
        nodeIter.next();
      }
      landmarks.add(nodeIter.next());
    }

    // Iteratively select the node which is farthest away from the current landmarks
    // Start by one since we already have the first landmark
    for (int i = 1; i < amountToUse; i++) {
      // Compute shortest path distances to all nodes
      final Map<N, ? extends IHasPathCost> nodeToDistance = mComputation.computeShortestPathCostsReachable(landmarks);

      // Search the node with highest distance
      double highestDistance = -1;
      N farthestNode = null;
      for (final Entry<N, ? extends IHasPathCost> entry : nodeToDistance.entrySet()) {
        final double distance = entry.getValue().getPathCost();
        if (distance > highestDistance) {
          // Node is farther, update
          highestDistance = distance;
          farthestNode = entry.getKey();
        }
      }
      // Add the farthest node
      landmarks.add(farthestNode);
    }

    return landmarks;
  }

}
