package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import java.util.Collections;
import java.util.EnumSet;
import java.util.OptionalDouble;
import java.util.Set;

import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IHasTransportationMode;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.SpeedTransportationModeComparator;
import de.tischner.cobweb.routing.model.graph.road.IRoadEdge;

public final class MultiModalModule<N extends INode, E extends IEdge<N>> implements IModule<N, E> {

  public static <N extends INode, E extends IEdge<N>> MultiModalModule<N, E> of(final Set<ETransportationMode> modes) {
    return new MultiModalModule<>(modes);
  }

  private final Set<ETransportationMode> mModes;

  private final SpeedTransportationModeComparator mSpeedComparator;

  public MultiModalModule(final Set<ETransportationMode> modes) {
    mModes = modes;
    mSpeedComparator = new SpeedTransportationModeComparator();
  }

  @Override
  public boolean considerEdgeForRelaxation(final E edge, final N pathDestination) {
    // Only interested in edges that have transportation modes
    if (!(edge instanceof IHasTransportationMode)) {
      return true;
    }
    final Set<ETransportationMode> edgeModes = ((IHasTransportationMode) edge).getTransportationModes();
    // Consider edge if it has any mode in common with the mode restrictions
    return mModes.stream().anyMatch(edgeModes::contains);
  }

  @Override
  public OptionalDouble provideEdgeCost(final E edge, final double tentativeDistance) {
    // Only interested in edges that have transportation modes
    if (!(edge instanceof IHasTransportationMode)) {
      return OptionalDouble.empty();
    }

    final Set<ETransportationMode> edgeModes = ((IHasTransportationMode) edge).getTransportationModes();
    return computeEdgeCost(edge.getCost(), edgeModes, edge);
  }

  private OptionalDouble computeEdgeCost(final double edgeCost, final Set<ETransportationMode> edgeModes,
      final E edge) {
    // No adjustment needed if edge only supports one mode, the cost is then
    // correct already
    if (edgeModes.size() == 1) {
      return OptionalDouble.empty();
    }

    // Pick the fastest mode that is available after applying the restrictions
    final Set<ETransportationMode> availableModes = EnumSet.copyOf(edgeModes);
    availableModes.retainAll(mModes);
    final ETransportationMode fastestMode = Collections.max(availableModes, mSpeedComparator);

    // Edge cost is already laid out for car or tram (depending on road or
    // transit edge)
    if (fastestMode == ETransportationMode.CAR || fastestMode == ETransportationMode.TRAM) {
      return OptionalDouble.empty();
    }
    if (edge instanceof IRoadEdge) {
      final IRoadEdge asRoadEdge = (IRoadEdge) edge;
      // Recompute using the given mode
      return OptionalDouble.of(asRoadEdge.getCost(fastestMode));
    }

    return OptionalDouble.empty();
  }

}
