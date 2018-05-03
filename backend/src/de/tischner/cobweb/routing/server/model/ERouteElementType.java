package de.tischner.cobweb.routing.server.model;

public enum ERouteElementType {
  NODE(0), PATH(1);

  public static ERouteElementType fromValue(final int value) {
    for (final ERouteElementType type : ERouteElementType.values()) {
      if (type.getValue() == value) {
        return type;
      }
    }
    return null;
  }

  private final int mValue;

  private ERouteElementType(final int value) {
    mValue = value;
  }

  public int getValue() {
    return mValue;
  }
}