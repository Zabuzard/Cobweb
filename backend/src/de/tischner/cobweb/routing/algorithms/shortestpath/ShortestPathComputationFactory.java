package de.tischner.cobweb.routing.algorithms.shortestpath;

import java.util.Set;

import de.tischner.cobweb.routing.algorithms.metrics.IMetric;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.ILandmarkProvider;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.LandmarkMetric;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.RandomLandmarks;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.AStar;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.ModuleDijkstra;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.MultiModalModule;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.TransitModule;
import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;

public final class ShortestPathComputationFactory<N extends INode, E extends IEdge<N>> {
  /**
   * The amount of landmarks to use for the landmark heuristic used by the
   * routing algorithm.
   */
  private static final int AMOUNT_OF_LANDMARKS = 20;
  private IShortestPathComputation<N, E> mBaseComputation;
  private final IGraph<N, E> mGraph;
  private IMetric<N> mMetric;

  public ShortestPathComputationFactory(final IGraph<N, E> graph) {
    mGraph = graph;
  }

  public IShortestPathComputation<N, E> getAlgorithm() {
    return mBaseComputation;
  }

  public IShortestPathComputation<N, E> getAlgorithm(final long depTime, final Set<ETransportationMode> modes) {
    return ModuleDijkstra.of(mGraph, AStar.of(mMetric), TransitModule.of(depTime), MultiModalModule.of(modes));
  }

  public void initialize() {
    final ILandmarkProvider<N> landmarkProvider = new RandomLandmarks<>(mGraph);
    mMetric = new LandmarkMetric<>(AMOUNT_OF_LANDMARKS, mGraph, landmarkProvider);
    mBaseComputation = ModuleDijkstra.of(mGraph, AStar.of(mMetric));
  }
}
