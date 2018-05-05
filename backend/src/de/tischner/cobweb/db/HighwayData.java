package de.tischner.cobweb.db;

import de.tischner.cobweb.parsing.osm.EHighwayType;

/**
 * POJO for highway data. Stores information about a highway like its way ID,
 * highway type and maximal speed.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class HighwayData {
  /**
   * Maximal allowed speed in <tt>km/h</tt>.
   */
  private final int mMaxSpeed;
  /**
   * The type of the highway.
   */
  private final EHighwayType mType;
  /**
   * The ID of the OSM way this highway belongs to. It is an unique attribute.
   */
  private final long mWayId;

  /**
   * Creates a new highway data object with the given attributes.
   *
   * @param wayId    The unique ID of the OSM way this highway belongs to
   * @param type     The type of the highway. Do not use <tt>null</tt> if unknown,
   *                 instead use {@link EHighwayType#ROAD}.
   * @param maxSpeed The maximal allowed speed in <tt>km/h</tt>
   */
  public HighwayData(final long wayId, final EHighwayType type, final int maxSpeed) {
    mWayId = wayId;
    mType = type;
    mMaxSpeed = maxSpeed;
  }

  /**
   * Gets the maximal allowed speed in <tt>km/h</tt>.
   *
   * @return The maximal allowed speed in <tt>km/h</tt>
   */
  public int getMaxSpeed() {
    return mMaxSpeed;
  }

  /**
   * Gets the type of the highway.
   *
   * @return The type of the highway
   */
  public EHighwayType getType() {
    return mType;
  }

  /**
   * The unique ID of the OSM way this highway belongs to.
   *
   * @return The unique ID of the OSM way
   */
  public long getWayId() {
    return mWayId;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
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
