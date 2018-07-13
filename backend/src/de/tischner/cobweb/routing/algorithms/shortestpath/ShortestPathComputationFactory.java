package de.tischner.cobweb.routing.algorithms.shortestpath;

import java.util.Set;

import de.tischner.cobweb.routing.algorithms.metrics.IMetric;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.ILandmarkProvider;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.LandmarkMetric;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.RandomLandmarks;
import de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan.ConnectionScan;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.AStarModule;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.ModuleDijkstra;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.MultiModalModule;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.TransitModule;
import de.tischner.cobweb.routing.algorithms.shortestpath.hybridmodel.HybridRoadTimetable;
import de.tischner.cobweb.routing.algorithms.shortestpath.hybridmodel.ITranslationWithTime;
import de.tischner.cobweb.routing.model.ERoutingModelMode;
import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.timetable.Timetable;

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
  private final ERoutingModelMode mMode;
  private final Timetable mTable;
  private final ITranslationWithTime<ICoreNode, ICoreNode> mTranslation;

  /**
   * Creates a new shortest path computation factory which generates algorithms
   * for the given graph.<br>
   * <br>
   * Use {@link #initialize()} after creation.
   *
   * @param graph       The graph to route on
   * @param table       The timetable to route on, or <tt>null</tt> if not used
   * @param translation The translation to use, or <tt>null</tt> if not used.
   *                    The object is used to translate road nodes to nearest
   *                    transit nodes.
   * @param mode        The mode to use for the routing model
   */
  public ShortestPathComputationFactory(final IGraph<ICoreNode, ICoreEdge<ICoreNode>> graph, final Timetable table,
      final ITranslationWithTime<ICoreNode, ICoreNode> translation, final ERoutingModelMode mode) {
    mGraph = graph;
    mTable = table;
    mTranslation = translation;
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
      case CONNECTION_SCAN:
        return new HybridRoadTimetable(ModuleDijkstra.of(mGraph, AStarModule.of(mMetric), MultiModalModule.of(modes)),
            new ConnectionScan(mTable), mTranslation, modes, depTime);
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
