package de.tischner.cobweb.searching.nearest.server.model;

/**
 * POJO that models a nearest search request.<br>
 * <br>
 * A request consists of a latitude and longitude.<br>
 * <br>
 * It has the exact structure that is expected as request format for the REST
 * API. It is primarily used to be constructed from the clients JSON request.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NearestSearchRequest {
  /**
   * The latitude to search the nearest node for.
   */
  private float mLatitude;
  /**
   * The longitude to search the nearest node for.
   */
  private float mLongitude;

  /**
   * Creates a new nearest search request.
   *
   * @param latitude  The latitude to search the nearest node for
   * @param longitude The longitude to search the nearest node for
   */
  public NearestSearchRequest(final float latitude, final float longitude) {
    mLatitude = latitude;
    mLongitude = longitude;
  }

  /**
   * Creates a new empty nearest search request. Is used to construct the
   * element via reflection.
   */
  @SuppressWarnings("unused")
  private NearestSearchRequest() {
    // Empty constructor for construction through reflection
  }

  /**
   * Gets the latitude to search the nearest node for.
   *
   * @return The latitude to search the nearest node for
   */
  public float getLatitude() {
    return mLatitude;
  }

  /**
   * Gets the longitude to search the nearest node for.
   *
   * @return The longitude to search the nearest node for
   */
  public float getLongitude() {
    return mLongitude;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("NearestSearchRequest [latitude=");
    builder.append(mLatitude);
    builder.append(", longitude=");
    builder.append(mLongitude);
    builder.append("]");
    return builder.toString();
  }
}
