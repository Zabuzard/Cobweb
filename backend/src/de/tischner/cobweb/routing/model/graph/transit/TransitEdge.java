package de.tischner.cobweb.routing.model.graph.transit;

import java.io.Serializable;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.IReversedConsumer;
import de.tischner.cobweb.routing.model.graph.IReversedProvider;
import de.tischner.cobweb.routing.model.graph.ISpatial;

/**
 * Implementation of a {@link IEdge} which connects transit nodes.<br>
 * <br>
 * The class is fully serializable and can implicitly be reversed in constant
 * time by using a {@link IReversedConsumer}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the node which must have an ID and be spatial
 */
public final class TransitEdge<N extends INode & IHasId & ISpatial & Serializable>
    implements IEdge<N>, IReversedConsumer, Serializable {
  /**
   * THe serial version UID.
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
   * @param source      The source node of the edge
   * @param destination The destination node of the edge
   * @param cost        The cost of this edge, measured in seconds. Interpreted
   *                    as travel time.
   */
  public TransitEdge(final N source, final N destination, final double cost) {
    mSource = source;
    mDestination = destination;
    mCost = cost;
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
    if (this.mDestination == null) {
      if (other.mDestination != null) {
        return false;
      }
    } else if (!this.mDestination.equals(other.mDestination)) {
      return false;
    }
    if (this.mSource == null) {
      if (other.mSource != null) {
        return false;
      }
    } else if (!this.mSource.equals(other.mSource)) {
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
   * @see de.tischner.cobweb.routing.model.graph.IEdge#getDestination()
   */
  @Override
  public N getDestination() {
    if (mReversedProvider != null && mReversedProvider.isReversed()) {
      return mSource;
    }
    return mDestination;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.model.graph.IEdge#getSource()
   */
  @Override
  public N getSource() {
    if (mReversedProvider != null && mReversedProvider.isReversed()) {
      return mDestination;
    }
    return mSource;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.mDestination == null) ? 0 : this.mDestination.hashCode());
    result = prime * result + ((this.mSource == null) ? 0 : this.mSource.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.road.IReversedConsumer#
   * setReversedProvider(de.tischner.cobweb.routing.model.graph.road.
   * IReversedProvider)
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
    builder.append("RoadEdge [");
    builder.append(getSource().getId());
    builder.append(" -(");
    builder.append(mCost);
    builder.append(")-> ");
    builder.append(getDestination().getId());
    builder.append("]");
    return builder.toString();
  }

}
