package de.tischner.cobweb.parsing.osm;

import java.util.HashMap;
import java.util.Map;

/**
 * Types of highways. Use {@link #ROAD} for an unknown highway type.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public enum EHighwayType {
  /**
   * A living street.
   */
  LIVING_STREET("living_street", 7),
  /**
   * A fast motorway.
   */
  MOTORWAY("motorway", 120),
  /**
   * The link to a motorway.
   */
  MOTORWAY_LINK("motorway_link", 50),
  /**
   * A primary road.
   */
  PRIMARY("primary", 100),
  /**
   * The link to a primary road.
   */
  PRIMARY_LINK("primary_link", 50),
  /**
   * A residential road.
   */
  RESIDENTIAL("residential", 50),
  /**
   * A road of unknown type.
   */
  ROAD("road", 20),
  /**
   * A secondary road.
   */
  SECONDARY("secondary", 80),
  /**
   * The link to a secondary road.
   */
  SECONDARY_LINK("secondary_link", 50),
  /**
   * A service road.
   */
  SERVICE("service", 7),
  /**
   * A tertiary road.
   */
  TERTIARY("tertiary", 70),
  /**
   * A fast trunk.
   */
  TRUNK("trunk", 110),
  /**
   * The link to a trunk.
   */
  TRUNK_LINK("trunk_link", 50),
  /**
   * An unclassified road.
   */
  UNCLASSIFIED("unclassified", 40),
  /**
   * An unsurfaced road.
   */
  UNSURFACED("unsurfaced", 30);

  /**
   * Map that connects highway tag names to their type.
   */
  private static final Map<String, EHighwayType> NAME_TO_TYPE = EHighwayType.constructLookupTable();

  /**
   * Gets the highway type corresponding to the given tag name or <tt>null</tt> if
   * there is no. The method runs in <tt>O(1)</tt>.
   *
   * @param name The tag name of the highway type to get
   * @return The corresponding highway type or <tt>null</tt>
   */
  public static EHighwayType fromName(final String name) {
    return NAME_TO_TYPE.get(name);
  }

  /**
   * Constructs a map that connects highway tag names to their type for a fast
   * lookup.
   *
   * @return A map that connects highway tag names to their types
   */
  private static Map<String, EHighwayType> constructLookupTable() {
    final HashMap<String, EHighwayType> nameToType = new HashMap<>();

    for (final EHighwayType type : EHighwayType.values()) {
      nameToType.put(type.getName(), type);
    }

    return nameToType;
  }

  /**
   * The average speed for this highway type in <tt>km/h</tt>.
   */
  private final int mAverageSpeed;
  /**
   * The tag name of the highway type.
   */
  private final String mName;

  /**
   * Creates a new highway type with the given tag name and average speed.
   *
   * @param textValue    The tag name
   * @param averageSpeed The average speed in <tt>km/h</tt>
   */
  private EHighwayType(final String textValue, final int averageSpeed) {
    mName = textValue;
    mAverageSpeed = averageSpeed;
  }

  /**
   * Gets the average speed of this highway type, in <tt>km/h</tt>.
   *
   * @return The average speed of this highway type, in <tt>km/h</tt>
   */
  public int getAverageSpeed() {
    return mAverageSpeed;
  }

  /**
   * Gets the tag name of this highway type.
   *
   * @return The tag name to get
   */
  public String getName() {
    return mName;
  }
}
