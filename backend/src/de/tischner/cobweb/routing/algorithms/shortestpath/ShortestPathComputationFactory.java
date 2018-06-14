package de.tischner.cobweb.routing.algorithms.shortestpath;

import java.util.Set;

import de.tischner.cobweb.routing.algorithms.metrics.IMetric;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.ILandmarkProvider;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.LandmarkMetric;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.RandomLandmarks;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.AStarModule;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.ModuleDijkstra;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.MultiModalModule;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.TransitModule;
import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;

/**
 * Factory that generates algorithms for shortest path computation.<br>
 * <br>
 * Call {@link #initialize()} after creation. Then use
 * {@link #createAlgorithm()} and similar methods to create algorithms.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the nodes
 * @param <E> The type of the edges
 */
public final class ShortestPathComputationFactory<N extends INode, E extends IEdge<N>> {
  /**
   * The amount of landmarks to use for the landmark heuristic.
   */
  private static final int AMOUNT_OF_LANDMARKS = 20;
  /**
   * The base algorithm to use for {@link #createAlgorithm()}.
   */
  private IShortestPathComputation<N, E> mBaseComputation;
  /**
   * The graph to route on.
   */
  private final IGraph<N, E> mGraph;
  /**
   * The metric to use for the {@link AStarModule} module.
   */
  private IMetric<N> mMetric;

  /**
   * Creates a new shortest path computation factory which generates algorithms
   * for the given graph.<br>
   * <br>
   * Use {@link #initialize()} after creation.
   *
   * @param graph The graph to route on
   */
  public ShortestPathComputationFactory(final IGraph<N, E> graph) {
    mGraph = graph;
  }

  /**
   * Creates a basic shortest path algorithm.<br>
   * <br>
   * Note that the resulting algorithm is not necessarily a new instance, the
   * factory is allowed to returned cached instances.
   *
   * @return A basic shortest path algorithm
   */
  public IShortestPathComputation<N, E> createAlgorithm() {
    return mBaseComputation;
  }

  /**
   * Creates a shortest path algorithm which respects the given departure time
   * and transportation mode restrictions.<br>
   * <br>
   * Note that the resulting algorithm is not necessarily a new instance, the
   * factory is allowed to returned cached instances.
   *
   * @param depTime The departure time in milliseconds since epoch
   * @param modes   The transportation mode restrictions
   * @return A shortest path algorithm with the given constraints
   */
  public IShortestPathComputation<N, E> createAlgorithm(final long depTime, final Set<ETransportationMode> modes) {
    return ModuleDijkstra.of(mGraph, AStarModule.of(mMetric), TransitModule.of(depTime), MultiModalModule.of(modes));
  }

  /**
   * Initializes the factory. Must be used prior to usage.
   */
  public void initialize() {
    final ILandmarkProvider<N> landmarkProvider = new RandomLandmarks<>(mGraph);
    mMetric = new LandmarkMetric<>(AMOUNT_OF_LANDMARKS, mGraph, landmarkProvider);
    mBaseComputation = ModuleDijkstra.of(mGraph, AStarModule.of(mMetric));
  }
}
