package de.unifreiburg.informatik.cobweb.db;

/**
 * POJO for spatial node data. Stores information about a node like its ID,
 * latitude and longitude.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class SpatialNodeData {
  /**
   * Unique ID of the node used by the graph.
   */
  private final int mId;
  /**
   * Unique OSM ID of the node.
   */
  private final long mIdOsm;
  /**
   * Latitude of the node.
   */
  private final float mLatitude;
  /**
   * Longitude of the node.
   */
  private final float mLongitude;

  /**
   * Creates a new spatial node data object with the given attributes.
   *
   * @param id        Unique ID of the node used by the graph
   * @param idOsm     Unique OSM ID of the node
   * @param latitude  Latitude of the node
   * @param longitude Longitude of the node
   */
  public SpatialNodeData(final int id, final long idOsm, final float latitude, final float longitude) {
    mId = id;
    mIdOsm = idOsm;
    mLatitude = latitude;
    mLongitude = longitude;
  }

  /**
   * Gets the unique ID of the node used by the graph.
   *
   * @return The node ID
   */
  public int getId() {
    return mId;
  }

  /**
   * Gets the latitude of the node.
   *
   * @return The latitude of the node
   */
  public float getLatitude() {
    return mLatitude;
  }

  /**
   * Gets the longitude of the node.
   *
   * @return The longitude of the node
   */
  public float getLongitude() {
    return mLongitude;
  }

  /**
   * Gets the unique OSM ID of the node.
   *
   * @return The node ID
   */
  public long getOsmId() {
    return mIdOsm;
  }
}
