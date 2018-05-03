package de.tischner.cobweb.util;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.routing.model.graph.road.ISpatial;

public final class RoutingUtil {

  private static final int EARTH_RADIUS_MEAN = 6_371_000;
  private static final int HALF_CIRCLE_DEG = 180;
  private static final double MAXIMAL_ROAD_SPEED = 200.0;
  private static final double MS_TO_KMH = 3.6;
  private static final int SECONDS_TO_MILLIS = 1_000;

  public static double degToRad(final double deg) {
    return deg * Math.PI / HALF_CIRCLE_DEG;
  }

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

  public static double kmhToMs(final double kmh) {
    return kmh / MS_TO_KMH;
  }

  public static double maximalRoadSpeed() {
    // TODO Maybe this needs adjustment
    return MAXIMAL_ROAD_SPEED;
  }

  public static double msToKmh(final double ms) {
    return ms * MS_TO_KMH;
  }

  public static double radToDeg(final double rad) {
    return rad * HALF_CIRCLE_DEG / Math.PI;
  }

  public static double secondsToMilliseconds(final double seconds) {
    return seconds * SECONDS_TO_MILLIS;
  }

  public static double travelTime(final double distance, final double speed) {
    return distance / RoutingUtil.kmhToMs(speed);
  }

  /**
   * Utility class. No implementation.
   */
  private RoutingUtil() {

  }
}
