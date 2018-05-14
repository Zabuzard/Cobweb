package de.tischner.cobweb.routing.model.graph.road;

import java.io.Serializable;

import de.tischner.cobweb.routing.model.graph.INode;

/**
 * Implementation of an {@link INode} which represents a node on a road. As such
 * it has spatial data and a unique ID.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RoadNode implements INode, IHasId, ISpatial, Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The unique ID of this node.
   */
  private final long mId;
  /**
   * The latitude of this node, in degrees.
   */
  private float mLatitude;
  /**
   * The longitude of this node, in degrees.
   */
  private float mLongitude;

  /**
   * Creates a new road node with the given ID and spatial data.
   *
   * @param id        The unique ID of this node
   * @param latitude  The latitude of this node, in degrees
   * @param longitude The longitude of this node, in degrees
   */
  public RoadNode(final long id, final float latitude, final float longitude) {
    mId = id;
    mLatitude = latitude;
    mLongitude = longitude;
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
    if (!(obj instanceof RoadNode)) {
      return false;
    }
    final RoadNode other = (RoadNode) obj;
    if (mId != other.mId) {
      return false;
    }
    return true;
  }

  /**
   * Gets the unique ID of this node.
   */
  @Override
  public long getId() {
    return mId;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.road.ISpatial#getLatitude()
   */
  @Override
  public float getLatitude() {
    return mLatitude;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.road.ISpatial#getLongitude()
   */
  @Override
  public float getLongitude() {
    return mLongitude;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (mId ^ (mId >>> 32));
    return result;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.routing.model.graph.road.ISpatial#setLatitude(float)
   */
  @Override
  public void setLatitude(final float latitude) {
    mLatitude = latitude;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.routing.model.graph.road.ISpatial#setLongitude(float)
   */
  @Override
  public void setLongitude(final float longitude) {
    mLongitude = longitude;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("RoadNode [id=");
    builder.append(mId);
    builder.append(", latitude=");
    builder.append(mLatitude);
    builder.append(", longitude=");
    builder.append(mLongitude);
    builder.append("]");
    return builder.toString();
  }

}
