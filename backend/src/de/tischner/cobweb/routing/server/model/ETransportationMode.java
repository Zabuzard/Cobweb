package de.tischner.cobweb.routing.server.model;

public enum ETransportationMode {
  BIKE(3), CAR(0), FOOT(2), IRRELEVANT(-1), TRAM(1);

  public static ETransportationMode fromValue(final int value) {
    for (final ETransportationMode mode : ETransportationMode.values()) {
      if (mode.getValue() == value) {
        return mode;
      }
    }
    return null;
  }

  private final int mValue;

  private ETransportationMode(final int value) {
    mValue = value;
  }

  public int getValue() {
    return mValue;
  }
}
