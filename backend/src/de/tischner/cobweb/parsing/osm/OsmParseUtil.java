package de.tischner.cobweb.parsing.osm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that offers constants and methods related to parsing OSM
 * entities.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class OsmParseUtil {
  /**
   * Tag name for OSM ways that contains the highway property.
   */
  public static final String HIGHWAY_TAG = "highway";
  /**
   * Tag name for OSM ways that contains the maxspeed property.
   */
  public static final String MAXSPEED_TAG = "maxspeed";
  /**
   * Tag name for OSM entities that contains the name property.
   */
  public static final String NAME_TAG = "name";
  /**
   * Tag name for OSM ways that contains the oneway property.
   */
  public static final String ONEWAY_TAG = "oneway";
  /**
   * Logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(OsmParseUtil.class);

  /**
   * Gets the highway type of the given OSM way.
   *
   * @param tagToValue Map connecting the tags of the OSM way to their values
   * @return The highway type or <tt>null</tt> if not present
   */
  public static EHighwayType parseHighwayType(final Map<String, String> tagToValue) {
    final String highwayText = tagToValue.get(HIGHWAY_TAG);
    if (highwayText == null) {
      return null;
    }
    return EHighwayType.fromName(highwayText);
  }

  /**
   * Gets the maximal allowed speed for the given OSM way in <tt>km/h</tt>.<br>
   * <br>
   * If the value is in a wrong format an average value will be returned. In
   * particular, the method will not throw any exception. But the error will be
   * logged.
   *
   * @param tagToValue Map connecting the tags of the OSM way to their values
   * @return The maximal allowed speed, an average value or <tt>-1</tt> if not
   *         present. The speed is in <tt>km/h</tt>.
   */
  public static int parseMaxSpeed(final Map<String, String> tagToValue) {
    final String maxSpeedText = tagToValue.get(MAXSPEED_TAG);
    if (maxSpeedText == null) {
      return -1;
    }
    // "none" means there is no speed limit, use a default value
    if (maxSpeedText.equals("none")) {
      return EHighwayType.MOTORWAY.getAverageSpeed();
    }
    // "walk" means the speed limit is at which humans tend to walk
    if (maxSpeedText.equals("walk")) {
      return EHighwayType.LIVING_STREET.getAverageSpeed();
    }
    // "signals" means the speed limit changes according to signal signs, use a
    // default value
    if (maxSpeedText.equals("signals")) {
      return EHighwayType.MOTORWAY.getAverageSpeed();
    }
    // TODO Format allows '60' (then its kmh) but also '60 mph' (then we need to
    // convert)
    // TODO Current errors: '5 mph', '50;100' and '100, 70'
    try {
      return Integer.parseInt(maxSpeedText);
    } catch (final NumberFormatException e) {
      // Use a default value instead
      LOGGER.error("Can not parse maxspeed value: {}", maxSpeedText);
      return EHighwayType.RESIDENTIAL.getAverageSpeed();
    }
  }

  /**
   * Gets the direction of the given OSM way.
   *
   * @param tagToValue Map connecting the tags of the OSM way to their values
   * @return A value greater than <tt>0</tt> if the way goes only into the
   *         direction it was declared in the OSM file. A value less than
   *         <tt>0</tt> if it goes into the opposite direction than declared in
   *         the OSM file. Or <tt>0</tt> if the way goes into both directions or
   *         the value was not present or could not be parsed.
   */
  public static int parseWayDirection(final Map<String, String> tagToValue) {
    final String onewayText = tagToValue.get(ONEWAY_TAG);
    if (onewayText == null || onewayText.isEmpty() || onewayText.equals("no") || onewayText.equals("false")
        || onewayText.equals("0")) {
      return 0;
    }
    if (onewayText.equals("yes") || onewayText.equals("true") || onewayText.equals("1")) {
      return 1;
    }
    if (onewayText.equals("-1") || onewayText.equals("reverse")) {
      return -1;
    }

    return 0;
  }

  /**
   * Utility class. No implementation.
   */
  private OsmParseUtil() {

  }
}
