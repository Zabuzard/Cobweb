package de.tischner.cobweb.routing.model.graph.road;

import de.tischner.cobweb.routing.model.graph.INode;

public final class RoadNode implements INode, IHasId, ISpatial {

  private final long mId;
  private final double mLatitude;
  private final double mLongitude;

  public RoadNode(final long id, final double latitude, final double longitude) {
    mId = id;
    mLatitude = latitude;
    mLongitude = longitude;
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

}
