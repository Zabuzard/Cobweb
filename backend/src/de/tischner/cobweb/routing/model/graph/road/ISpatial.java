package de.tischner.cobweb.routing.model.graph.road;

/**
 * Interface for spatial objects.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface ISpatial {
  /**
   * Gets the latitude of the object in degrees.
   *
   * @return The latitude of the object
   */
  double getLatitude();

  /**
   * Gets the longitude of the object in degrees.
   *
   * @return The longitude of the object
   */
  double getLongitude();

  /**
   * Sets the latitude of the object.
   *
   * @param latitude The latitude to set, in degrees
   */
  void setLatitude(double latitude);

  /**
   * Sets the longitude of the object.
   *
   * @param longitude The longitude to set, in degrees
   */
  void setLongitude(double longitude);
}
