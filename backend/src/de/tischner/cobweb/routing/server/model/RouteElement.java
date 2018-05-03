package de.tischner.cobweb.routing.server.model;

import java.util.List;

public final class RouteElement {

  private List<double[]> mGeom;
  private int mMode;
  private String mName;
  private int mType;

  public RouteElement(final ERouteElementType type, final ETransportationMode mode, final String name,
      final List<double[]> geom) {
    mName = name;
    mGeom = geom;

    setType(type);
    setMode(mode);
  }

  public RouteElement(final ERouteElementType type, final String name, final List<double[]> geom) {
    mName = name;
    mGeom = geom;

    setType(type);
    setMode(ETransportationMode.IRRELEVANT);
  }

  private RouteElement() {
    // Empty constructor for construction through reflection
  }

  public List<double[]> getGeom() {
    return mGeom;
  }

  public ETransportationMode getMode() {
    return ETransportationMode.fromValue(mMode);
  }

  public String getName() {
    return mName;
  }

  public ERouteElementType getType() {
    return ERouteElementType.fromValue(mType);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("RouteElement [type=");
    builder.append(mType);
    builder.append(", mode=");
    builder.append(mMode);
    builder.append(", name=");
    builder.append(mName);
    builder.append(", geom=");
    builder.append(mGeom);
    builder.append("]");
    return builder.toString();
  }

  private void setMode(final ETransportationMode mode) {
    mMode = mode.getValue();
  }

  private void setType(final ERouteElementType type) {
    mType = type.getValue();
  }
}
