package de.tischner.cobweb.routing.model.graph.transit;

import java.io.Serializable;

import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.ISpatial;

/**
 * Implementation of an {@link INode} which represents a node on a transit
 * network. As such it has spatial data, a unique ID and belongs to a station.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TransitNode implements INode, IHasId, ISpatial, Serializable {
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
   * Creates a new transit node with the given ID and spatial data.
   *
   * @param id        The unique ID of this node
   * @param latitude  The latitude of this node, in degrees
   * @param longitude The longitude of this node, in degrees
   */
  public TransitNode(final int id, final float latitude, final float longitude) {
    mId = id;
    mLatitude = latitude;
    mLongitude = longitude;
    // TODO Add station logic
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
    builder.append("]");
    return builder.toString();
  }

}
