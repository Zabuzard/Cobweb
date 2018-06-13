package de.tischner.cobweb.routing.model.graph.road;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IReversedConsumer;
import de.tischner.cobweb.routing.model.graph.IReversedProvider;
import de.tischner.cobweb.routing.model.graph.SpeedTransportationModeComparator;
import de.tischner.cobweb.util.RoutingUtil;

/**
 * Implementation of a {@link IEdge} which represents a road.<br>
 * <br>
 * As such it has a {@link EHighwayType} and a maximal speed. It has an ID which
 * is unique to the way it belongs to. A way can consist of several edges. The
 * road edge connects nodes that have an ID and are spatial.<br>
 * <br>
 * The class is fully serializable and can implicitly be reversed in constant
 * time by using a {@link IReversedConsumer}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the node which must have an ID and be spatial
 */
public final class RoadEdge<N extends ICoreNode> implements ICoreEdge<N>, IRoadEdge {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The cost of this edge per transportation mode. Measured in seconds,
   * interpreted as travel time with the maximal allowed or average speed for
   * the given highway type.
   */
  private final Map<ETransportationMode, Double> mCost;
  /**
   * The cost of this edge if transportation mode does not matter. Measured in
   * seconds, interpreted as travel time with the maximal allowed or average
   * speed for the given highway type.
   */
  private double mDefaultCost;
  /**
   * The destination of this edge.
   */
  private final N mDestination;
  /**
   * The ID of this edge which is unique to the way it belongs to. A way can
   * consist of several edges.
   */
  private final int mId;
  /**
   * The maximal speed allowed on this edge, in <tt>km/h</tt>.
   */
  private final int mMaxSpeed;
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
   * The highway type of the edge.
   */
  private final EHighwayType mType;

  /**
   * Creates a new road edge which connects the given source and destination.
   *
   * @param id          The ID of the road which is unique to the way it belongs
   *                    to. A way can consist of several edges
   * @param source      The source node of the edge
   * @param destination The destination node of the edge
   * @param type        The highway type of the edge, use
   *                    {@link EHighwayType#ROAD} if unknown
   * @param maxSpeed    The maximal speed allowed on this edge, in <tt>km/h</tt>
   * @param modes       The transportation modes allowed for this edge
   */
  public RoadEdge(final int id, final N source, final N destination, final EHighwayType type, final int maxSpeed,
      final Set<ETransportationMode> modes) {
    mId = id;
    mSource = source;
    mDestination = destination;
    mType = type;
    mMaxSpeed = maxSpeed;

    mCost = new EnumMap<>(ETransportationMode.class);
    for (final ETransportationMode mode : modes) {
      mCost.put(mode, -1.0);
    }

    updateCost();
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
    if (!(obj instanceof RoadEdge)) {
      return false;
    }
    final RoadEdge<?> other = (RoadEdge<?>) obj;
    if (this.mDestination == null) {
      if (other.mDestination != null) {
        return false;
      }
    } else if (!this.mDestination.equals(other.mDestination)) {
      return false;
    }
    if (this.mId != other.mId) {
      return false;
    }
    if (this.mSource == null) {
      if (other.mSource != null) {
        return false;
      }
    } else if (!this.mSource.equals(other.mSource)) {
      return false;
    }
    if (this.mType != other.mType) {
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
    return mDefaultCost;
  }

  @Override
  public double getCost(final ETransportationMode mode) {
    return mCost.get(mode);
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

  /**
   * Gets the ID of this edge which is unique to the way it belongs to. A way
   * can consist of several edges.
   */
  @Override
  public int getId() {
    return mId;
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

  @Override
  public Set<ETransportationMode> getTransportationModes() {
    return mCost.keySet();
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
    result = prime * result + this.mId;
    result = prime * result + ((this.mSource == null) ? 0 : this.mSource.hashCode());
    result = prime * result + ((this.mType == null) ? 0 : this.mType.hashCode());
    return result;
  }

  @Override
  public boolean hasTransportationMode(final ETransportationMode mode) {
    return mCost.containsKey(mode);
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
    builder.append("RoadEdge [id=");
    builder.append(mId);
    builder.append(", ");
    builder.append(getSource().getId());
    builder.append(" -(");
    builder.append(mCost);
    builder.append(")-> ");
    builder.append(getDestination().getId());
    builder.append("]");
    return builder.toString();
  }

  /**
   * Recomputes and updates the cost of this edge. Therefore, distances between
   * the given source and destination are computed. Based on that the travel
   * time is computed.<br>
   * <br>
   * The method should be used if the spatial data of the source or destination
   * node changed, as the cost is not updated without calling this method.
   */
  public void updateCost() {
    final double distance = RoutingUtil.distanceEquiRect(mSource, mDestination);
    for (final ETransportationMode mode : mCost.keySet()) {
      mCost.put(mode, computeCost(distance, mode));
    }

    final ETransportationMode fastestMode = Collections.max(mCost.keySet(), new SpeedTransportationModeComparator());
    mDefaultCost = mCost.get(fastestMode);
  }

  /**
   * Computes the cost for this edge. Therefore, the travel time is computed
   * based on the given distance and mode.
   *
   * @param distance The distance to travel in meters
   * @param mode     The transportation mode to use for traveling
   * @return The computed cost for this edge
   */
  private double computeCost(final double distance, final ETransportationMode mode) {
    final double speed = RoutingUtil.getSpeedOfHighway(mType, mMaxSpeed, mode);
    return RoutingUtil.travelTime(distance, speed);
  }

}
