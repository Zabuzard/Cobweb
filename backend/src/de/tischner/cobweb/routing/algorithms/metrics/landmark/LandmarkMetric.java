package de.tischner.cobweb.routing.algorithms.metrics.landmark;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.routing.algorithms.metrics.IMetric;
import de.tischner.cobweb.routing.algorithms.shortestpath.IShortestPathComputation;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.IHasPathCost;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.util.collections.NestedDoubleMap;

/**
 * Implements the a metric for nodes by using landmarks.<br>
 * <br>
 * Given two objects it approximates the distance by comparing shortest paths
 * from the objects to the landmarks. The distance depends on the underlying
 * distance model of the graph, i.e. the format used by the edge cost.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the nodes and landmarks
 * @param <E> The type of the edges
 * @param <G> The type of the graph
 */
public final class LandmarkMetric<N extends INode, E extends IEdge<N>, G extends IGraph<N, E>> implements IMetric<N> {
  /**
   * Logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(LandmarkMetric.class);
  /**
   * Landmarks to use for computing the metric.
   */
  private Collection<N> mLandmarks;
  /**
   * Nested map that connects landmarks to all other nodes and the corresponding
   * shortest path distance.
   */
  private final NestedDoubleMap<N, N> mLandmarkToNodeDistance;
  /**
   * Nested map that connects all nodes to the landmarks and the corresponding
   * shortest path distance.
   */
  private final NestedDoubleMap<N, N> mNodeToLandmarkDistance;

  /**
   * Creates a new landmark metric that uses the given amount of landmarks
   * produced by the given provider.<br>
   * <br>
   * Given two objects it approximates the distance by comparing shortest paths
   * from the objects to the landmarks. The distance depends on the underlying
   * distance model of the graph, i.e. the format used by the edge cost.<br>
   * <br>
   * Due to the computation of landmarks and shortest paths, the creation of
   * this metric might take a while.
   *
   * @param amount           The amount of landmarks to use
   * @param graph            The graph to define the metric on
   * @param landmarkProvider The provider to use for generation of the landmarks
   */
  public LandmarkMetric(final int amount, final G graph, final ILandmarkProvider<N> landmarkProvider) {
    // TODO Evaluate whether double can be exchanged by float for distances to
    // optimize space
    mLandmarkToNodeDistance = new NestedDoubleMap<>(amount);
    mNodeToLandmarkDistance = new NestedDoubleMap<>(graph.size());
    mNodeToLandmarkDistance.setNestedInitialCapacity(amount);

    initialize(amount, graph, landmarkProvider, new Dijkstra<>(graph));
  }

  /**
   * Approximates the distance between the given two nodes by comparing shortest
   * paths from the nodes to the landmarks. The distance depends on the
   * underlying distance model of the graph, i.e. the format used by the edge
   * cost.
   */
  @Override
  public double distance(final N first, final N second) {
    double greatestDistance = 0.0;
    for (final N landmark : mLandmarks) {
      // Ignore the landmark if anyone can not reach it
      if (!mNodeToLandmarkDistance.contains(first, landmark) || !mNodeToLandmarkDistance.contains(second, landmark)
          || !mLandmarkToNodeDistance.contains(landmark, second)
          || !mLandmarkToNodeDistance.contains(landmark, first)) {
        continue;
      }

      final double firstToLandmark = mNodeToLandmarkDistance.get(first, landmark);
      final double secondToLandmark = mNodeToLandmarkDistance.get(second, landmark);
      final double landmarkToSecond = mLandmarkToNodeDistance.get(landmark, second);
      final double landmarkToFirst = mLandmarkToNodeDistance.get(landmark, first);

      final double landmarkBehindDestination = firstToLandmark - secondToLandmark;
      final double landmarkBeforeSource = landmarkToSecond - landmarkToFirst;
      final double distance = Math.max(landmarkBehindDestination, landmarkBeforeSource);
      if (distance > greatestDistance) {
        greatestDistance = distance;
      }
    }

    return greatestDistance;
  }

  /**
   * Initializes this metric. It generates landmarks using the given provider
   * and computes shortest path distances from the landmarks to all nodes and
   * vice versa.<br>
   * <br>
   * Depending on the size of the graph and the amount of landmarks this method
   * may take a while.
   *
   * @param amount           The amount of landmarks to generate
   * @param graph            The graph to operate on
   * @param landmarkProvider The provider to use to generate landmarks
   * @param computation      The algorithm to use for computing shortest paths
   */
  private void initialize(final int amount, final G graph, final ILandmarkProvider<N> landmarkProvider,
      final IShortestPathComputation<N, E> computation) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Computing landmarks");
    }
    mLandmarks = landmarkProvider.getLandmarks(amount);

    // Compute distances from landmarks to all other nodes
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Computing distances from {} landmarks to {} nodes", mLandmarks.size(), graph.size());
    }
    for (final N landmark : mLandmarks) {
      final Map<N, ? extends IHasPathCost> nodeToDistance = computation.computeShortestPathCostsReachable(landmark);
      mLandmarkToNodeDistance.setNestedInitialCapacity(nodeToDistance.size());
      for (final Entry<N, ? extends IHasPathCost> entry : nodeToDistance.entrySet()) {
        mLandmarkToNodeDistance.put(landmark, entry.getKey(), entry.getValue().getPathCost());
      }
    }

    // Compute distances from all nodes to landmarks
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Computing distances from {} nodes to {} landmarks", graph.size(), mLandmarks.size());
    }
    graph.reverse();
    for (final N landmark : mLandmarks) {
      final Map<N, ? extends IHasPathCost> nodeToDistance = computation.computeShortestPathCostsReachable(landmark);
      for (final Entry<N, ? extends IHasPathCost> entry : nodeToDistance.entrySet()) {
        mNodeToLandmarkDistance.put(entry.getKey(), landmark, entry.getValue().getPathCost());
      }
    }
    graph.reverse();
  }
}
