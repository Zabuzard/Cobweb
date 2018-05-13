package de.tischner.cobweb.util;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.routing.model.graph.road.ISpatial;

/**
 * Utility class which offers methods related to routing.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RoutingUtil {
  /**
   * The mean of the earth radius in metres.
   */
  private static final int EARTH_RADIUS_MEAN = 6_371_000;
  /**
   * The amount of degrees of a half circle.
   */
  private static final int HALF_CIRCLE_DEG = 180;
  /**
   * The maximal possible speed on a road in <tt>km/h</tt>
   */
  private static final double MAXIMAL_ROAD_SPEED = 200.0;
  /**
   * Factor to multiply with to convert milliseconds to nanoseconds.
   */
  private static final int MILLIS_TO_NANO = 1_000_000;
  /**
   * Factor to multiply with to convert <tt>m/s</tt> (metres per second) into
   * <tt>km/h</tt> (kilometres per hour).
   */
  private static final double MS_TO_KMH = 3.6;
  /**
   * Factor to multiply with to convert seconds to milliseconds.
   */
  private static final int SECONDS_TO_MILLIS = 1_000;

  /**
   * Converts the given value in <tt>degrees</tt> into <tt>radians</tt>.
   *
   * @param deg The value in <tt>degrees</tt> to convert
   * @return The corresponding value in <tt>radians</tt>
   */
  public static double degToRad(final double deg) {
    return deg * Math.PI / HALF_CIRCLE_DEG;
  }

  /**
   * Approximates the distance between the given objects by using a model which
   * represents the earth as equirectangular projection.
   *
   * @param first  The first object
   * @param second The second object
   * @return The distance between the given objects
   */
  public static double distanceEquiRect(final ISpatial first, final ISpatial second) {
    // Convert positions to radians
    final double firstLat = RoutingUtil.degToRad(first.getLatitude());
    final double firstLong = RoutingUtil.degToRad(first.getLongitude());
    final double secondLat = RoutingUtil.degToRad(second.getLatitude());
    final double secondLong = RoutingUtil.degToRad(second.getLongitude());

    final double x = (secondLong - firstLong) * Math.cos((firstLat + secondLat) / 2);
    final double y = secondLat - firstLat;
    return Math.sqrt(x * x + y * y) * EARTH_RADIUS_MEAN;
  }

  /**
   * Gets the speed used on the given highway.
   *
   * @param type     The type of the highway
   * @param maxSpeed The maximal allowed speed on the highway or <tt>-1</tt> if
   *                 not present
   * @return The speed used on the highway in <tt>km/h</tt>
   */
  public static double getSpeedOfHighway(final EHighwayType type, final int maxSpeed) {
    // Use the max speed property if present
    if (maxSpeed != -1) {
      return maxSpeed;
    }

    // Use the highway type if present
    if (type != null) {
      return type.getAverageSpeed();
    }

    // Use a default speed value
    return EHighwayType.RESIDENTIAL.getAverageSpeed();
  }

  /**
   * Converts the given value in <tt>km/h</tt> (kilometres per hour) into
   * <tt>m/s</tt> (metres per second).
   *
   * @param kmh The value in <tt>km/h</tt> to convert
   * @return The corresponding value in <tt>m/s</tt>
   */
  public static double kmhToMs(final double kmh) {
    return kmh / MS_TO_KMH;
  }

  /**
   * Gets the maximal possible speed on a road in <tt>km/h</tt>.
   *
   * @return The maximal possible speed in <tt>km/h</tt>
   */
  public static double maximalRoadSpeed() {
    // TODO Maybe this needs adjustment
    return MAXIMAL_ROAD_SPEED;
  }

  /**
   * Converts the given value in <tt>m/s</tt> (metres per second)<tt> into
   * km/h</tt> (kilometres per hour).
   *
   * @param ms The value in <tt>ms/s</tt> to convert
   * @return The corresponding value in <tt>km/h</tt>
   */
  public static double msToKmh(final double ms) {
    return ms * MS_TO_KMH;
  }

  /**
   * Converts the given value in <tt>nanoseconds</tt> to <tt>milliseconds</tt>.
   * The result is rounded down.
   *
   * @param nanos The value in <tt>nanoseconds</tt> to convert
   * @return The corresponding value in <tt>milliseconds</tt>, rounded down
   */
  public static long nanoToMilliseconds(final long nanos) {
    return nanos / MILLIS_TO_NANO;
  }

  /**
   * Converts the given value in <tt>radians</tt> into <tt>degrees</tt>.
   *
   * @param rad The value in <tt>radians</tt> to convert
   * @return The corresponding value in <tt>degrees</tt>
   */
  public static double radToDeg(final double rad) {
    return rad * HALF_CIRCLE_DEG / Math.PI;
  }

  /**
   * Converts the given value in <tt>seconds</tt> into <tt>milliseconds</tt>.
   *
   * @param seconds The value in <tt>seconds</tt> to convert
   * @return The corresponding value in <tt>milliseconds</tt>
   */
  public static double secondsToMilliseconds(final double seconds) {
    return seconds * SECONDS_TO_MILLIS;
  }

  /**
   * Gets the time need to travel the given distance with the given speed.
   *
   * @param distance The distance to travel in <tt>metres</tt>
   * @param speed    The speed to travel with in <tt>km/h</tt>
   * @return The time need to travel in <tt>seconds</tt>
   */
  public static double travelTime(final double distance, final double speed) {
    return distance / RoutingUtil.kmhToMs(speed);
  }

  /**
   * Utility class. No implementation.
   */
  private RoutingUtil() {

  }
}
