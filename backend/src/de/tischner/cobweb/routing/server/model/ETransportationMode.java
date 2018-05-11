package de.tischner.cobweb.routing.server.model;

/**
 * Types of transportation modes.<br>
 * <br>
 * A transportation mode has a corresponding value which is used when decoding
 * {@link RouteElement}s as JSON.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public enum ETransportationMode {
  /**
   * Transportation by bike.
   */
  BIKE(3),
  /**
   * Transportation by car.
   */
  CAR(0),
  /**
   * Transportation by foot.
   */
  FOOT(2),
  /**
   * Transportation mode is irrelevant.
   */
  IRRELEVANT(-1),
  /**
   * Transportation by tram.
   */
  TRAM(1);

  /**
   * Gets the transportation mode that corresponds to the given value.
   *
   * @param value The value to get the element from
   * @return The corresponding transportation mode or <tt>null</tt> if not
   *         present
   */
  public static ETransportationMode fromValue(final int value) {
    for (final ETransportationMode mode : ETransportationMode.values()) {
      if (mode.getValue() == value) {
        return mode;
      }
    }
    return null;
  }

  /**
   * The value that corresponds to the transportation mode.
   */
  private final int mValue;

  /**
   * Creates a new transportation mode with the given corresponding value.
   *
   * @param value The value that corresponds to this transportation mode
   */
  private ETransportationMode(final int value) {
    mValue = value;
  }

  /**
   * Gets the value this transportation mode corresponds to.<br>
   * <br>
   * It is used when decoding {@link RouteElement}s as JSON.
   *
   * @return The value this transportation mode corresponds to
   */
  public int getValue() {
    return mValue;
  }
}
