package de.unifreiburg.informatik.cobweb.searching.nearest.server.model;

/**
 * POJO that models a nearest search response.<br>
 * <br>
 * A response consists of the nearest OSM node, including its unique OSM ID and
 * its exact latitude and longitude coordinates. It also includes the time it
 * needed to answer the query in milliseconds.<br>
 * <br>
 * It has the exact structure that is expected as response format for the REST
 * API. It is primarily used to be constructed and then encoded to JSON to be
 * send to the client.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NearestSearchResponse {
  /**
   * The unique ID of the OSM node which is nearest to the requested location or
   * <code>-1</code> to indicate that there is no nearest node.
   */
  private long mId;
  /**
   * The latitude coordinate of the matched OSM node.
   */
  private float mLatitude;
  /**
   * The longitude coordinate of the matched OSM node.
   */
  private float mLongitude;
  /**
   * The duration answering the query took, in milliseconds.
   */
  private long mTime;

  /**
   * Creates a new nearest search response.
   *
   * @param time      The duration answering the query took, in milliseconds
   * @param id        The unique ID of the OSM node which is nearest to the
   *                  requested location or <code>-1</code> to indicate that there
   *                  is no nearest node
   * @param latitude  The latitude coordinate of the matched OSM node
   * @param longitude The longitude coordinate of the matched OSM node
   */
  public NearestSearchResponse(final long time, final long id, final float latitude, final float longitude) {
    mTime = time;
    mId = id;
    mLatitude = latitude;
    mLongitude = longitude;
  }

  /**
   * Creates a new empty nearest search response. Is used to construct the
   * element via reflection.
   */
  @SuppressWarnings("unused")
  private NearestSearchResponse() {
    // Empty constructor for construction through reflection
  }

  /**
   * Gets the unique ID of the OSM node which is nearest to the requested
   * location or <code>-1</code> to indicate that there is no nearest node.
   *
   * @return The unique ID of the OSM node or <code>-1</code> to indicate that there
   *         is no nearest node
   */
  public long getId() {
    return mId;
  }

  /**
   * Gets the latitude coordinate of the matched OSM node.
   *
   * @return The latitude coordinate of the matched OSM node
   */
  public float getLatitude() {
    return mLatitude;
  }

  /**
   * Gets the longitude coordinate of the matched OSM node.
   *
   * @return The longitude coordinate of the matched OSM node
   */
  public float getLongitude() {
    return mLongitude;
  }

  /**
   * Gets the duration answering the query took, in milliseconds.
   *
   * @return The duration to get
   */
  public long getTime() {
    return mTime;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("NearestSearchResponse [id=");
    builder.append(mId);
    builder.append(", latitude=");
    builder.append(mLatitude);
    builder.append(", longitude=");
    builder.append(mLongitude);
    builder.append("]");
    return builder.toString();
  }
}
