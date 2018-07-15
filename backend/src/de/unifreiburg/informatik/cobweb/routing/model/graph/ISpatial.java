package de.unifreiburg.informatik.cobweb.routing.model.graph;

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
  float getLatitude();

  /**
   * Gets the longitude of the object in degrees.
   *
   * @return The longitude of the object
   */
  float getLongitude();

  /**
   * Sets the latitude of the object.
   *
   * @param latitude The latitude to set, in degrees
   */
  void setLatitude(float latitude);

  /**
   * Sets the longitude of the object.
   *
   * @param longitude The longitude to set, in degrees
   */
  void setLongitude(float longitude);
}
