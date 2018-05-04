package de.tischner.cobweb.parsing.osm;

import java.util.HashMap;
import java.util.Map;

/**
 * Types of highways.
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

  private static final Map<String, EHighwayType> NAME_TO_TYPE = EHighwayType.constructLookupTable();

  public static EHighwayType fromName(final String name) {
    return NAME_TO_TYPE.get(name);
  }

  private static Map<String, EHighwayType> constructLookupTable() {
    final HashMap<String, EHighwayType> nameToType = new HashMap<>();

    for (final EHighwayType type : EHighwayType.values()) {
      nameToType.put(type.getName(), type);
    }

    return nameToType;
  }

  private final int mAverageSpeed;

  private final String mName;

  private EHighwayType(final String textValue, final int averageSpeed) {
    mName = textValue;
    mAverageSpeed = averageSpeed;
  }

  public int getAverageSpeed() {
    return mAverageSpeed;
  }

  public String getName() {
    return mName;
  }
}
