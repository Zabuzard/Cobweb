package de.tischner.cobweb.db;

/**
 * POJO for spatial node data. Stores information about a node like its way ID,
 * latitude and longitude.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class SpatialNodeData {
  /**
   * Unique OSM ID of the node.
   */
  private final long mId;
  /**
   * Latitude of the node.
   */
  private final double mLatitude;
  /**
   * Longitude of the node.
   */
  private final double mLongitude;

  /**
   * Creates a new spatial node data object with the given attributes.
   *
   * @param id        Unique OSM ID of the node
   * @param latitude  Latitude of the node
   * @param longitude Longitude of the node
   */
  public SpatialNodeData(final long id, final double latitude, final double longitude) {
    mId = id;
    mLatitude = latitude;
    mLongitude = longitude;
  }

  /**
   * Gets the unique OSM ID of the node.
   *
   * @return The node ID
   */
  public long getId() {
    return mId;
  }

  /**
   * Gets the latitude of the node.
   *
   * @return The latitude of the node
   */
  public double getLatitude() {
    return mLatitude;
  }

  /**
   * Gets the longitude of the node.
   *
   * @return The longitude of the node
   */
  public double getLongitude() {
    return mLongitude;
  }
}
