package de.tischner.cobweb.db;

public final class SpatialNodeData {
  private final long mId;
  private final double mLatitude;
  private final double mLongitude;

  public SpatialNodeData(final long id, final double latitude, final double longitude) {
    mId = id;
    mLatitude = latitude;
    mLongitude = longitude;
  }

  public long getId() {
    return mId;
  }

  public double getLatitude() {
    return mLatitude;
  }

  public double getLongitude() {
    return mLongitude;
  }
}
