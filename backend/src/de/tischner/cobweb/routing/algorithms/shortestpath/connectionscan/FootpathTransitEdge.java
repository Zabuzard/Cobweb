package de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan;

import java.util.EnumSet;
import java.util.Set;

import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.transit.TransitEdge;

public final class FootpathTransitEdge<N extends ICoreNode> extends TransitEdge<N> {
  /**
   * The transportation modes allowed by this edge.
   */
  private final Set<ETransportationMode> mModes;

  public FootpathTransitEdge(final int id, final N source, final N destination, final double cost) {
    super(id, source, destination, cost);
    mModes = EnumSet.of(ETransportationMode.FOOT);
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
