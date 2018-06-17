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

/**
 * Module for a {@link ModuleDijkstra} that dynamically provides the correct
 * edge costs and for {@link IHasTransportationMode} edges and decides if they
 * should be considered, based on given transportation mode restrictions.<br>
 * <br>
 * The factory method {@link #of(Set)} can be used for convenient instance
 * creation.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the nodes
 * @param <E> Type of the edges
 */
public final class MultiModalModule<N extends INode, E extends IEdge<N>> implements IModule<N, E> {

  /**
   * Creates a multi-modal module instance with the given transportation mode
   * restrictions.
   *
   * @param       <N> Type of the nodes
   * @param       <E> Type of the edges
   * @param modes The transportation mode restrictions. Only modes listed in the
   *              set are allowed to be taken by the routing. If possible
   *              provide an {@link EnumSet} or similar optimized sets for
   *              efficiency.
   * @return The created multi-modal module instance
   */
  public static <N extends INode, E extends IEdge<N>> MultiModalModule<N, E> of(final Set<ETransportationMode> modes) {
    return new MultiModalModule<>(modes);
  }

  /**
   * The transportation mode restrictions. Only modes listed in the set are
   * allowed to be taken by the routing.
   */
  private final Set<ETransportationMode> mModes;
  /**
   * Comparator to be used to determine the fastest available transportation
   * mode.
   */
  private final SpeedTransportationModeComparator mSpeedComparator;

  /**
   * Creates a multi-modal module instance with the given transportation mode
   * restrictions.
   *
   * @param modes The transportation mode restrictions. Only modes listed in the
   *              set are allowed to be taken by the routing. If possible
   *              provide an {@link EnumSet} or similar optimized sets for
   *              efficiency.
   */
  public MultiModalModule(final Set<ETransportationMode> modes) {
    mModes = modes;
    mSpeedComparator = new SpeedTransportationModeComparator();
  }

  /**
   * Whether or not the given edge should be considered for relaxation.<br>
   * <br>
   * The method will ignore edges that are not of type
   * {@link IHasTransportationMode}, i.e. return <tt>true</tt>. Else it checks
   * if the edge can be taken with any of the restricted transportation modes.
   */
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

  /**
   * Provides the cost of a given edge.<br>
   * <br>
   * The method will ignore edges that are not of type
   * {@link IHasTransportationMode}. Else the cost corresponding to the fastest
   * available transportation mode is chosen.
   */
  @Override
  public OptionalDouble provideEdgeCost(final E edge, final double tentativeDistance) {
    // Only interested in edges that have transportation modes
    if (!(edge instanceof IHasTransportationMode)) {
      return OptionalDouble.empty();
    }

    final Set<ETransportationMode> edgeModes = ((IHasTransportationMode) edge).getTransportationModes();
    return computeEdgeCost(edge, edgeModes);
  }

  /**
   * Computes the cost of the given edge when taken with the fastest
   * transportation mode available after applying the transportation mode
   * restrictions.
   *
   * @param edge      The edge in question
   * @param edgeModes The transportation modes with which the edge can be taken.
   *                  If possible pass an {@link EnumSet} of a similar efficient
   *                  set for efficiency.
   * @return The cost of the given edge when taken with the fastest available
   *         mode, in seconds interpreted as travel time.
   */
  private OptionalDouble computeEdgeCost(final E edge, final Set<ETransportationMode> edgeModes) {
    // No adjustment needed if edge only supports one mode, the cost is then
    // correct already
    if (edgeModes.size() == 1) {
      return OptionalDouble.empty();
    }

    // Pick the fastest mode that is available after applying the restrictions
    final Set<ETransportationMode> availableModes = EnumSet.copyOf(edgeModes);
    availableModes.retainAll(mModes);
    if (availableModes.isEmpty()) {
      return OptionalDouble.empty();
    }
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
