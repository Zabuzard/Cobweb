package de.tischner.cobweb.parsing.osm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OsmParseUtil {
  public static final String HIGHWAY_TAG = "highway";
  public static final String MAXSPEED_TAG = "maxspeed";
  public static final String NAME_TAG = "name";
  public static final String ONEWAY_TAG = "oneway";
  private final static Logger LOGGER = LoggerFactory.getLogger(OsmParseUtil.class);

  public static int getWayDirection(final Map<String, String> tagToValue) {
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

  public static EHighwayType parseHighwayType(final Map<String, String> tagToValue) {
    final String highwayText = tagToValue.get(HIGHWAY_TAG);
    if (highwayText == null) {
      return null;
    }
    return EHighwayType.fromName(highwayText);
  }

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
   * Utility class. No implementation.
   */
  private OsmParseUtil() {

  }
}
