package de.tischner.cobweb.routing.server.model;

/**
 * Types of route elements.<br>
 * <br>
 * A route element has a corresponding value which is used when decoding
 * {@link RouteElement}s as JSON.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public enum ERouteElementType {
  /**
   * A node which represents a position on a road.
   */
  NODE(0),
  /**
   * A path which represents a several edges connecting nodes.
   */
  PATH(1);

  /**
   * Gets the route element type that corresponds to the given value.
   *
   * @param value The value to get the element from
   * @return The corresponding route element type or <tt>null</tt> if not
   *         present
   */
  public static ERouteElementType fromValue(final int value) {
    for (final ERouteElementType type : ERouteElementType.values()) {
      if (type.getValue() == value) {
        return type;
      }
    }
    return null;
  }

  /**
   * The value that corresponds to the route element type.
   */
  private final int mValue;

  /**
   * Creates a new route element type with the given corresponding value.
   *
   * @param value The value that corresponds to this route element type
   */
  private ERouteElementType(final int value) {
    mValue = value;
  }

  /**
   * Gets the value this route element type corresponds to.<br>
   * <br>
   * It is used when decoding {@link RouteElement}s as JSON.
   *
   * @return The value this route element type corresponds to
   */
  public int getValue() {
    return mValue;
  }
}