package de.tischner.cobweb.routing.model.graph.link;

import java.util.EnumSet;
import java.util.Set;

import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.IReversedProvider;

public final class LinkEdge<N extends ICoreNode> implements ICoreEdge<N> {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The destination of this edge.
   */
  private final N mDestination;
  private final Set<ETransportationMode> mModes;
  /**
   * An object that provides a reversed flag or <tt>null</tt> if not present.
   * Can be used to determine if the edge should be interpreted as reversed to
   * implement implicit edge reversal at constant time.
   */
  private IReversedProvider mReversedProvider;
  /**
   * The source of the edge.
   */
  private final N mSource;

  /**
   * Creates a new link edge which connects the given source and destination.
   *
   * @param source      The source node of the edge
   * @param destination The destination node of the edge
   */
  public LinkEdge(final N source, final N destination) {
    mSource = source;
    mDestination = destination;
    mModes = EnumSet.of(ETransportationMode.CAR, ETransportationMode.BIKE, ETransportationMode.FOOT);
  }

  @Override
  public double getCost() {
    return 0.0;
  }

  @Override
  public N getDestination() {
    if (mReversedProvider != null && mReversedProvider.isReversed()) {
      return mSource;
    }
    return mDestination;
  }

  @Override
  public int getId() {
    return -1;
  }

  @Override
  public N getSource() {
    if (mReversedProvider != null && mReversedProvider.isReversed()) {
      return mDestination;
    }
    return mSource;
  }

  @Override
  public Set<ETransportationMode> getTransportationModes() {
    return mModes;
  }

  @Override
  public boolean hasTransportationMode(final ETransportationMode mode) {
    return mModes.contains(mode);
  }

  @Override
  public void setReversedProvider(final IReversedProvider provider) {
    mReversedProvider = provider;
  }

}
