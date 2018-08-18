package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath;

import java.util.Set;

import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric;
import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.IMetric;
import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.landmark.ILandmarkProvider;
import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.landmark.LandmarkMetric;
import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.landmark.RandomLandmarks;
import de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.INearestNeighborComputation;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.connectionscan.ConnectionScan;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules.AStarModule;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules.AbortAfterModule;
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
   * The travel time in seconds after which to abort shortest path computation
   * to access nodes.
   */
  private final int mAbortTravelTimeToAccessNodes;
  /**
   * Object to use for computing access nodes. Or <tt>null</tt> if not used.
   */
  private final IAccessNodeComputation<ICoreNode, ICoreNode> mAccessNodeComputation;
  /**
   * The amount of landmarks to use for the landmark heuristic.
   */
  private final int mAmountOfLandmarks;
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
   * @param graph                        The graph to route on
   * @param table                        The timetable to route on, or
   *                                     <tt>null</tt> if not used
   * @param accessNodeComputation        The access node computation to use, or
   *                                     <tt>null</tt> if not used.
   * @param stopToNearestRoadNode        Object to use for retrieving the
   *                                     nearest road node to a given stop, or
   *                                     <tt>null</tt> if not used.
   * @param mode                         The mode to use for the routing model
   * @param abortTravelTimeToAccessNodes The travel time in seconds after which
   *                                     to abort shortest path computation to
   *                                     access nodes
   * @param amountOfLandmarks            The amount of landmarks to use for the
   *                                     landmark heuristic
   */
  public ShortestPathComputationFactory(final IGraph<ICoreNode, ICoreEdge<ICoreNode>> graph, final Timetable table,
      final IAccessNodeComputation<ICoreNode, ICoreNode> accessNodeComputation,
      final INearestNeighborComputation<ICoreNode> stopToNearestRoadNode, final ERoutingModelMode mode,
      final int abortTravelTimeToAccessNodes, final int amountOfLandmarks) {
    mGraph = graph;
    mTable = table;
    mAccessNodeComputation = accessNodeComputation;
    mStopToNearestRoadNode = stopToNearestRoadNode;
    mMode = mode;
    mAbortTravelTimeToAccessNodes = abortTravelTimeToAccessNodes;
    mAmountOfLandmarks = amountOfLandmarks;
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
        return createAlgorithmHybridRoadTimetable(depTime, modes);
      case LINK_GRAPH:
        return createAlgorithmLinkGraph(depTime, modes);
      default:
        throw new AssertionError();
    }
  }

  /**
   * Creates an instance of the ALT algorithm, which is A-star using the
   * landmarks heuristic.
   *
   * @return The created algorithm
   */
  public IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> createAlgorithmAlt() {
    return mBaseComputation;
  }

  /**
   * Creates an instance of the A-star algorithm using the as-the-crow-flies
   * metric.
   *
   * @return The created algorithm
   */
  public IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> createAlgorithmAStarAsTheCrowFlies() {
    final IMetric<ICoreNode> metric = new AsTheCrowFliesMetric<>();
    return ModuleDijkstra.of(mGraph, AStarModule.of(metric));
  }

  /**
   * Creates an instance of Connection Scan algorithm.
   *
   * @return The created algorithm
   */
  public IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> createAlgorithmCsa() {
    return new ConnectionScan(mTable);
  }

  /**
   * Creates an instance of the ordinary Dijkstra algorithm.
   *
   * @return The created algorithm
   */
  public IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> createAlgorithmDijkstra() {
    return new Dijkstra<>(mGraph);
  }

  /**
   * Creates an instance of an algorithm for a hybrid approach connecting road
   * and timetable models.
   *
   * @param depTime The departure time in milliseconds since epoch
   * @param modes   The transportation mode restrictions
   * @return The created algorithm
   */
  public IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>>
      createAlgorithmHybridRoadTimetable(final long depTime, final Set<ETransportationMode> modes) {
    return new HybridRoadTimetable(ModuleDijkstra.of(mGraph, AStarModule.of(mMetric), MultiModalModule.of(modes)),
        ModuleDijkstra.of(mGraph, AStarModule.of(mMetric), AbortAfterModule.of(mAbortTravelTimeToAccessNodes),
            MultiModalModule.of(modes)),
        new ConnectionScan(mTable), mAccessNodeComputation, mStopToNearestRoadNode, modes, depTime);
  }

  /**
   * Creates an instance of an algorithm for a link graph.
   *
   * @param depTime The departure time in milliseconds since epoch
   * @param modes   The transportation mode restrictions
   * @return The created algorithm
   */
  public IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> createAlgorithmLinkGraph(final long depTime,
      final Set<ETransportationMode> modes) {
    return ModuleDijkstra.of(mGraph, AStarModule.of(mMetric), TransitModule.of(depTime), MultiModalModule.of(modes));
  }

  /**
   * Creates an instance of a time-dependent ALT algorithm.
   *
   * @param depTime The departure time in milliseconds since epoch
   * @return The created algorithm
   */
  public IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> createAlgorithmTimeDependentAlt(final long depTime) {
    return ModuleDijkstra.of(mGraph, AStarModule.of(mMetric), TransitModule.of(depTime));
  }

  /**
   * Gets the access node computation used by this factory.
   *
   * @return The access node computation used by this factory, or <tt>null</tt>
   *         if not used
   */
  public IAccessNodeComputation<ICoreNode, ICoreNode> getAccessNodeComputation() {
    return mAccessNodeComputation;
  }

  /**
   * Gets the object used by this factory for retrieving the nearest road node
   * to a given stop.
   *
   * @return The object used by this factory for retrieving the nearest road
   *         node to a given stop, or <tt>null</tt> if not used
   */
  public INearestNeighborComputation<ICoreNode> getStopToNearestRoadNode() {
    return mStopToNearestRoadNode;
  }

  /**
   * Initializes the factory. Must be used prior to usage.
   */
  public void initialize() {
    final ILandmarkProvider<ICoreNode> landmarkProvider = new RandomLandmarks<>(mGraph);
    mMetric = new LandmarkMetric<>(mAmountOfLandmarks, mGraph, landmarkProvider);
    mBaseComputation = ModuleDijkstra.of(mGraph, AStarModule.of(mMetric));
  }
}
