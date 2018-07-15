package de.unifreiburg.informatik.cobweb.routing.model.graph.transit;

import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;

/**
 * Implementation of an {@link INode} which represents a node on a transit
 * network. As such it has spatial data, a unique ID and belongs to a station.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TransitNode implements ICoreNode, ITransitNode {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The unique ID of this node.
   */
  private final int mId;
  /**
   * The latitude of this node, in degrees.
   */
  private float mLatitude;
  /**
   * The longitude of this node, in degrees.
   */
  private float mLongitude;
  /**
   * The timestamp of this node, in seconds since midnight.
   */
  private final int mTime;

  /**
   * Creates a new transit node with the given ID, spatial data and a timestamp.
   *
   * @param id        The unique ID of this node
   * @param latitude  The latitude of this node, in degrees
   * @param longitude The longitude of this node, in degrees
   * @param time      The timestamp of this node, in seconds since midnight
   */
  public TransitNode(final int id, final float latitude, final float longitude, final int time) {
    mId = id;
    mLatitude = latitude;
    mLongitude = longitude;
    mTime = time;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof TransitNode)) {
      return false;
    }
    final TransitNode other = (TransitNode) obj;
    if (this.mId != other.mId) {
      return false;
    }
    return true;
  }

  /**
   * Gets the unique ID of this node.
   */
  @Override
  public int getId() {
    return mId;
  }

  @Override
  public float getLatitude() {
    return mLatitude;
  }

  @Override
  public float getLongitude() {
    return mLongitude;
  }

  @Override
  public int getTime() {
    return mTime;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.mId;
    return result;
  }

  @Override
  public void setLatitude(final float latitude) {
    mLatitude = latitude;
  }

  @Override
  public void setLongitude(final float longitude) {
    mLongitude = longitude;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("TransitNode [id=");
    builder.append(mId);
    builder.append(", latitude=");
    builder.append(mLatitude);
    builder.append(", longitude=");
    builder.append(mLongitude);
    builder.append(", time=");
    builder.append(mTime);
    builder.append("]");
    return builder.toString();
  }

}
