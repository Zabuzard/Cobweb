package de.tischner.cobweb.routing.model.graph.road;

import de.tischner.cobweb.routing.model.graph.INode;

public final class RoadNode implements INode, IHasId, ISpatial {

  private final long mId;
  private double mLatitude;
  private double mLongitude;

  public RoadNode(final long id, final double latitude, final double longitude) {
    mId = id;
    mLatitude = latitude;
    mLongitude = longitude;
  }

  /*
   * (non-Javadoc)
   *
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

  /*
   * (non-Javadoc)
   *
   * @see de.tischner.cobweb.model.graph.road.IHasId#getId()
   */
  @Override
  public long getId() {
    return mId;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.tischner.cobweb.routing.model.graph.road.ISpatial#getLatitude()
   */
  @Override
  public double getLatitude() {
    return mLatitude;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.tischner.cobweb.routing.model.graph.road.ISpatial#getLongitude()
   */
  @Override
  public double getLongitude() {
    return mLongitude;
  }

  /*
   * (non-Javadoc)
   *
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
   *
   * @see de.tischner.cobweb.routing.model.graph.road.ISpatial#setLatitude(double)
   */
  @Override
  public void setLatitude(final double latitude) {
    mLatitude = latitude;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.routing.model.graph.road.ISpatial#setLongitude(double)
   */
  @Override
  public void setLongitude(final double longitude) {
    mLongitude = longitude;
  }

  /*
   * (non-Javadoc)
   *
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
