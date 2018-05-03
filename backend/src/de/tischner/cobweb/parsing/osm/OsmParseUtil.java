package de.tischner.cobweb.parsing.osm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OsmParseUtil {
  public static final String HIGHWAY_TAG = "highway";
  public static final String MAXSPEED_TAG = "maxspeed";
  public static final String NAME_TAG = "name";
  private final static Logger LOGGER = LoggerFactory.getLogger(OsmParseUtil.class);

  public static EHighwayType parseHighwayType(final String highwayText) {
    if (highwayText == null) {
      return null;
    }
    return EHighwayType.fromName(highwayText);
  }

  public static Integer parseMaxSpeed(final String maxSpeedText) {
    if (maxSpeedText == null) {
      return null;
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
