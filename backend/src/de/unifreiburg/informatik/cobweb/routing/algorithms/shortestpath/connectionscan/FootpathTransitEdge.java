package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.connectionscan;

import java.util.EnumSet;
import java.util.Set;

import de.unifreiburg.informatik.cobweb.routing.model.graph.ETransportationMode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitEdge;

/**
 * Extension of a transit edge that is taken by foot instead of the tram.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the nodes
 */
public final class FootpathTransitEdge<N extends ICoreNode> extends TransitEdge<N> {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The transportation modes allowed by this edge.
   */
  private final Set<ETransportationMode> mModes;

  /**
   * Creates a new footpath transit edge with the given values.
   *
   * @param id          The ID of the edge
   * @param source      The source node of the edge
   * @param destination The destination node of the edge
   * @param cost        The cost of the edge
   */
  public FootpathTransitEdge(final int id, final N source, final N destination, final double cost) {
    super(id, source, destination, cost);
    mModes = EnumSet.of(ETransportationMode.CAR, ETransportationMode.BIKE, ETransportationMode.FOOT);
  }

  @Override
  public Set<ETransportationMode> getTransportationModes() {
    return mModes;
  }

  @Override
  public boolean hasTransportationMode(final ETransportationMode mode) {
    return mModes.contains(mode);
  }

}
