package de.tischner.cobweb.util;

import java.util.EnumSet;
import java.util.Set;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.ISpatial;

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
   * Maximal speed of a bike in <tt>km/h</tt>.
   */
  private static final int MAX_BIKE_SPEED = 14;
  /**
   * Maximal walking speed in <tt>km/h</tt>.
   */
  private static final int MAX_FOOT_SPEED = 5;
  /**
   * The maximal possible speed on a road in <tt>km/h</tt>
   */
  private static final double MAXIMAL_ROAD_SPEED = 200.0;
  /**
   * Factor to multiply with to convert milliseconds to nanoseconds.
   */
  private static final int MILLIS_TO_NANO = 1_000_000;
  /**
   * Factor to multiply with to convert <tt>mph</tt> (miles per hour) into
   * <tt>km/h</tt> (kilometres per hour).
   */
  private static final double MPH_TO_KMH = 1.60934;

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
    if (first == second) {
      return 0.0;
    }

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
   * @param mode     The transportation mode to use for traveling
   * @return The speed used on the highway in <tt>km/h</tt>
   */
  public static double getSpeedOfHighway(final EHighwayType type, final int maxSpeed, final ETransportationMode mode) {
    final int speedOnRoad;
    if (maxSpeed != -1) {
      // Use the max speed property if present
      speedOnRoad = maxSpeed;
    } else if (type != null) {
      // Use the highway type if present
      speedOnRoad = type.getAverageSpeed();
    } else {
      // Use a default speed value
      speedOnRoad = EHighwayType.RESIDENTIAL.getAverageSpeed();
    }

    // Limit the speed by the transportation modes maximal speed
    if (mode == ETransportationMode.CAR) {
      // Car is not limited, use the given road speed
      return speedOnRoad;
    } else if (mode == ETransportationMode.BIKE) {
      return Math.min(speedOnRoad, MAX_BIKE_SPEED);
    } else if (mode == ETransportationMode.FOOT) {
      return Math.min(speedOnRoad, MAX_FOOT_SPEED);
    } else {
      // Assume no limit on the transportation mode
      return speedOnRoad;
    }
  }

  /**
   * Gets a set of allowed transportation modes for the given highway type.
   *
   * @param type The type of the highway
   * @return A set of allowed transportation modes
   */
  public static Set<ETransportationMode> getTransportationModesOfHighway(final EHighwayType type) {
    if (type == EHighwayType.MOTORWAY || type == EHighwayType.MOTORWAY_LINK) {
      return EnumSet.of(ETransportationMode.CAR);
    } else if (type == EHighwayType.CYCLEWAY) {
      return EnumSet.of(ETransportationMode.BIKE);
    }
    return EnumSet.of(ETransportationMode.CAR, ETransportationMode.BIKE, ETransportationMode.FOOT);
  }

  /**
   * Gets the maximal walking speed in <tt>km/h</tt>.
   *
   * @return The maximal walking speed in <tt>km/h</tt>
   */
  public static double getWalkingSpeed() {
    return MAX_FOOT_SPEED;
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
    return MAXIMAL_ROAD_SPEED;
  }

  /**
   * Converts the given value in <tt>milliseconds</tt> into <tt>seconds</tt>.
   *
   * @param millis The value in <tt>milliseconds</tt> to convert
   * @return The corresponding value in <tt>seconds</tt>
   */
  public static double millisToSeconds(final long millis) {
    return ((double) millis) / SECONDS_TO_MILLIS;
  }

  /**
   * Converts the given value in <tt>mph</tt> (miles per hour)<tt> into
   * km/h</tt> (kilometres per hour).
   *
   * @param mph The value in <tt>mph</tt> to convert
   * @return The corresponding value in <tt>km/h</tt>
   */
  public static double mphToKmh(final double mph) {
    return mph * MPH_TO_KMH;
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
  public static long nanosToMillis(final long nanos) {
    return nanos / MILLIS_TO_NANO;
  }

  /**
   * Converts the given value in <tt>nanoseconds</tt> to <tt>seconds</tt>.
   *
   * @param nanos The value in <tt>nanoseconds</tt> to convert
   * @return The corresponding value in <tt>seconds</tt>
   */
  public static double nanosToSeconds(final long nanos) {
    return ((double) nanos) / MILLIS_TO_NANO / SECONDS_TO_MILLIS;
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
  public static double secondsToMillis(final double seconds) {
    return seconds * SECONDS_TO_MILLIS;
  }

  /**
   * Converts the given value in <tt>seconds</tt> to <tt>nanoseconds</tt>.
   *
   * @param seconds The value in <tt>seconds</tt> to convert
   * @return The corresponding value in <tt>nanoseconds</tt>
   */
  public static long secondsToNanos(final double seconds) {
    return (long) (RoutingUtil.secondsToMillis(seconds) * MILLIS_TO_NANO);
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
