package de.tischner.cobweb.routing.algorithms.shortestpath.hybridmodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.tischner.cobweb.routing.algorithms.shortestpath.AShortestPathComputation;
import de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath;
import de.tischner.cobweb.routing.algorithms.shortestpath.IHasPathCost;
import de.tischner.cobweb.routing.algorithms.shortestpath.IShortestPathComputation;
import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.EdgeCost;
import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.IPath;
import de.tischner.cobweb.routing.model.graph.link.LinkEdge;

public final class HybridRoadTimetable extends AShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> {
  private final long mDepTime;
  private final IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> mRoadComputation;
  private final ITranslationWithTime<ICoreNode, ICoreNode> mRoadToTransitTranslation;
  private final IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> mTransitComputation;
  private final boolean mUseRoadOnly;

  public HybridRoadTimetable(final IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> roadComputation,
      final IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> transitComputation,
      final ITranslationWithTime<ICoreNode, ICoreNode> roadToTransitTranslation, final Set<ETransportationMode> modes,
      final long depTime) {
    mRoadComputation = roadComputation;
    mTransitComputation = transitComputation;
    mRoadToTransitTranslation = roadToTransitTranslation;
    mUseRoadOnly = !modes.contains(ETransportationMode.TRAM);
    mDepTime = depTime;
  }

  @Override
  public Collection<ICoreNode> computeSearchSpace(final Collection<ICoreNode> sources, final ICoreNode destination) {
    if (mUseRoadOnly) {
      return mRoadComputation.computeSearchSpace(sources, destination);
    }
    // TODO Don't ignore road nodes and road paths
    final Collection<ICoreNode> transitSources = sources.stream().map(this::translate).collect(Collectors.toList());
    final ICoreNode transitDestination = translate(destination);

    final Collection<ICoreNode> searchSpace =
        mTransitComputation.computeSearchSpace(transitSources, transitDestination);
    searchSpace.addAll(sources);
    searchSpace.add(destination);

    return searchSpace;
  }

  @Override
  public Optional<IPath<ICoreNode, ICoreEdge<ICoreNode>>> computeShortestPath(final Collection<ICoreNode> sources,
      final ICoreNode destination) {
    if (mUseRoadOnly) {
      return mRoadComputation.computeShortestPath(sources, destination);
    }
    // TODO Don't ignore road nodes and road paths
    final Collection<ICoreNode> transitSources = sources.stream().map(this::translate).collect(Collectors.toList());
    final ICoreNode transitDestination = translate(destination);

    final Optional<IPath<ICoreNode, ICoreEdge<ICoreNode>>> possiblePath =
        mTransitComputation.computeShortestPath(transitSources, transitDestination);
    if (!possiblePath.isPresent()) {
      return Optional.empty();
    }
    final IPath<ICoreNode, ICoreEdge<ICoreNode>> path = possiblePath.get();
    final EdgePath<ICoreNode, ICoreEdge<ICoreNode>> fullPath = new EdgePath<>();
    // TODO The source should be the chosen, not any. Unfortunately that
    // information is lost.
    fullPath.addEdge(new LinkEdge<>(sources.iterator().next(), path.getSource()), 0.0);
    for (final EdgeCost<ICoreNode, ICoreEdge<ICoreNode>> edgeCost : path) {
      fullPath.addEdge(edgeCost.getEdge(), edgeCost.getCost());
    }
    fullPath.addEdge(new LinkEdge<>(path.getDestination(), destination), 0.0);

    return Optional.of(fullPath);
  }

  @Override
  public Optional<Double> computeShortestPathCost(final Collection<ICoreNode> sources, final ICoreNode destination) {
    if (mUseRoadOnly) {
      return mRoadComputation.computeShortestPathCost(sources, destination);
    }
    // TODO Don't ignore road nodes and road paths
    final Collection<ICoreNode> transitSources = sources.stream().map(this::translate).collect(Collectors.toList());
    final ICoreNode transitDestination = translate(destination);

    return mTransitComputation.computeShortestPathCost(transitSources, transitDestination);
  }

  @Override
  public Map<ICoreNode, ? extends IHasPathCost> computeShortestPathCostsReachable(final Collection<ICoreNode> sources) {
    if (mUseRoadOnly) {
      return mRoadComputation.computeShortestPathCostsReachable(sources);
    }
    // TODO Don't ignore road nodes and road paths
    final Collection<ICoreNode> transitSources = sources.stream().map(this::translate).collect(Collectors.toList());

    final Map<ICoreNode, ? extends IHasPathCost> transitCostsReachable =
        mTransitComputation.computeShortestPathCostsReachable(transitSources);
    final Map<ICoreNode, IHasPathCost> costsReachable = new HashMap<>(transitCostsReachable);
    for (final ICoreNode roadSource : sources) {
      costsReachable.put(roadSource, () -> 0.0);
    }

    return costsReachable;
  }

  private ICoreNode translate(final ICoreNode node) {
    return mRoadToTransitTranslation.translate(node, mDepTime);
  }

}
