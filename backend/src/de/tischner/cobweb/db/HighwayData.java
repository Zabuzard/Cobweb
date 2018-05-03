package de.tischner.cobweb.db;

import de.tischner.cobweb.parsing.osm.EHighwayType;

public final class HighwayData {

  private final int mMaxSpeed;
  private final EHighwayType mType;
  private final long mWayId;

  public HighwayData(final long wayId, final EHighwayType type, final int maxSpeed) {
    mWayId = wayId;
    mType = type;
    mMaxSpeed = maxSpeed;
  }

  public int getMaxSpeed() {
    return mMaxSpeed;
  }

  public EHighwayType getType() {
    return mType;
  }

  public long getWayId() {
    return mWayId;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("HighwayData [wayId=");
    builder.append(mWayId);
    builder.append(", type=");
    builder.append(mType);
    builder.append(", maxSpeed=");
    builder.append(mMaxSpeed);
    builder.append("]");
    return builder.toString();
  }

}
