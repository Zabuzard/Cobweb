package de.unifreiburg.informatik.cobweb.routing.model.graph.transit;

import java.util.EnumSet;
import java.util.Set;

import de.unifreiburg.informatik.cobweb.routing.model.graph.ETransportationMode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IReversedConsumer;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IReversedProvider;

/**
 * Implementation of a {@link IEdge} which connects transit nodes.<br>
 * <br>
 * The class is fully serializable and can implicitly be reversed in constant
 * time by using a {@link IReversedConsumer}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the node which must have an ID and be spatial
 */
public class TransitEdge<N extends ICoreNode> implements ICoreEdge<N> {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The cost of this edge. Measured in seconds, interpreted as travel time.
   */
  private final double mCost;
  /**
   * The destination of this edge.
   */
  private final N mDestination;
  /**
   * The ID of this edge which is unique.
   */
  private final int mId;
  /**
   * The transportation modes allowed by this edge.
   */
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
   * Creates a new transit edge which connects the given source and destination.
   *
   * @param id          The ID of the edge which is unique
   * @param source      The source node of the edge
   * @param destination The destination node of the edge
   * @param cost        The cost of this edge, measured in seconds. Interpreted
   *                    as travel time.
   */
  public TransitEdge(final int id, final N source, final N destination, final double cost) {
    mId = id;
    mSource = source;
    mDestination = destination;
    mCost = cost;
    mModes = EnumSet.of(ETransportationMode.TRAM);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof TransitEdge)) {
      return false;
    }
    final TransitEdge<?> other = (TransitEdge<?>) obj;
    if (this.mId != other.mId) {
      return false;
    }
    return true;
  }

  /**
   * The cost of this edge. Measured in seconds, interpreted as travel time with
   * the maximal allowed or average speed for the given highway type.
   */
  @Override
  public double getCost() {
    return mCost;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge#getDestination(
   * )
   */
  @Override
  public N getDestination() {
    if (mReversedProvider != null && mReversedProvider.isReversed()) {
      return mSource;
    }
    return mDestination;
  }

  @Override
  public int getId() {
    return mId;
  }

  /*
   * (non-Javadoc)
   * @see de.unifreiburg.informatik.cobweb.model.graph.IEdge#getSource()
   */
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

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.mId;
    return result;
  }

  @Override
  public boolean hasTransportationMode(final ETransportationMode mode) {
    return mModes.contains(mode);
  }

  /*
   * (non-Javadoc)
   * @see de.unifreiburg.informatik.cobweb.routing.model.graph.road.
   * IReversedConsumer#
   * setReversedProvider(de.unifreiburg.informatik.cobweb.routing.model.graph.
   * road. IReversedProvider)
   */
  @Override
  public void setReversedProvider(final IReversedProvider provider) {
    mReversedProvider = provider;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("TransitEdge [");
    builder.append(getSource().getId());
    builder.append(" -(");
    builder.append(mCost);
    builder.append(")-> ");
    builder.append(getDestination().getId());
    builder.append("]");
    return builder.toString();
  }

}
