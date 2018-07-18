package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath;

import java.util.Set;

import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.IMetric;
import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.landmark.ILandmarkProvider;
import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.landmark.LandmarkMetric;
import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.landmark.RandomLandmarks;
import de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.INearestNeighborComputation;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.connectionscan.ConnectionScan;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules.AStarModule;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules.ModuleDijkstra;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules.MultiModalModule;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules.TransitModule;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.hybridmodel.HybridRoadTimetable;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.hybridmodel.IAccessNodeComputation;
import de.unifreiburg.informatik.cobweb.routing.model.ERoutingModelMode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ETransportationMode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IGraph;
import de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable;

/**
 * Factory that generates algorithms for shortest path computation.<br>
 * <br>
 * Call {@link #initialize()} after creation. Then use
 * {@link #createAlgorithm()} and similar methods to create algorithms.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ShortestPathComputationFactory {
  /**
   * The amount of landmarks to use for the landmark heuristic.
   */
  private static final int AMOUNT_OF_LANDMARKS = 20;
  /**
   * Object to use for computing access nodes. Or <tt>null</tt> if not used.
   */
  private final IAccessNodeComputation<ICoreNode, ICoreNode> mAccessNodeComputation;
  /**
   * The base algorithm to use for {@link #createAlgorithm()}.
   */
  private IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> mBaseComputation;
  /**
   * The graph to route on.
   */
  private final IGraph<ICoreNode, ICoreEdge<ICoreNode>> mGraph;
  /**
   * The metric to use for the {@link AStarModule} module.
   */
  private IMetric<ICoreNode> mMetric;
  /**
   * The mode to use for the routing model. Determines which algorithms to
   * choose.
   */
  private final ERoutingModelMode mMode;
  /**
   * Object to use for retrieving the nearest road node to a given stop, or
   * <tt>null</tt> if not used.
   */
  private final INearestNeighborComputation<ICoreNode> mStopToNearestRoadNode;
  /**
   * The timetable to use for transit data, or <tt>null</tt> if not used.
   */
  private final Timetable mTable;

  /**
   * Creates a new shortest path computation factory which generates algorithms
   * for the given graph.<br>
   * <br>
   * Use {@link #initialize()} after creation.
   *
   * @param graph                 The graph to route on
   * @param table                 The timetable to route on, or <tt>null</tt> if
   *                              not used
   * @param accessNodeComputation The access node computation to use, or
   *                              <tt>null</tt> if not used.
   * @param stopToNearestRoadNode Object to use for retrieving the nearest road
   *                              node to a given stop, or <tt>null</tt> if not
   *                              used.
   * @param mode                  The mode to use for the routing model
   */
  public ShortestPathComputationFactory(final IGraph<ICoreNode, ICoreEdge<ICoreNode>> graph, final Timetable table,
      final IAccessNodeComputation<ICoreNode, ICoreNode> accessNodeComputation,
      final INearestNeighborComputation<ICoreNode> stopToNearestRoadNode, final ERoutingModelMode mode) {
    mGraph = graph;
    mTable = table;
    mAccessNodeComputation = accessNodeComputation;
    mStopToNearestRoadNode = stopToNearestRoadNode;
    mMode = mode;
  }

  /**
   * Creates a basic shortest path algorithm.<br>
   * <br>
   * Note that the resulting algorithm is not necessarily a new instance, the
   * factory is allowed to returned cached instances.
   *
   * @return A basic shortest path algorithm
   */
  public IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> createAlgorithm() {
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
  public IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> createAlgorithm(final long depTime,
      final Set<ETransportationMode> modes) {
    switch (mMode) {
      case GRAPH_WITH_TIMETABLE:
        return new HybridRoadTimetable(ModuleDijkstra.of(mGraph, AStarModule.of(mMetric), MultiModalModule.of(modes)),
            new ConnectionScan(mTable), mAccessNodeComputation, mStopToNearestRoadNode, modes, depTime);
      case LINK_GRAPH:
        return ModuleDijkstra.of(mGraph, AStarModule.of(mMetric), TransitModule.of(depTime),
            MultiModalModule.of(modes));
      default:
        throw new AssertionError();
    }
  }

  /**
   * Initializes the factory. Must be used prior to usage.
   */
  public void initialize() {
    final ILandmarkProvider<ICoreNode> landmarkProvider = new RandomLandmarks<>(mGraph);
    mMetric = new LandmarkMetric<>(AMOUNT_OF_LANDMARKS, mGraph, landmarkProvider);
    mBaseComputation = ModuleDijkstra.of(mGraph, AStarModule.of(mMetric));
  }
}
