package de.tischner.cobweb.routing.server.model;

import java.util.List;

public final class Journey {

  private long mArrTime;
  private long mDepTime;
  private List<RouteElement> mRoute;

  public Journey(final long depTime, final long arrTime, final List<RouteElement> route) {
    mDepTime = depTime;
    mArrTime = arrTime;
    mRoute = route;
  }

  private Journey() {
    // Empty constructor for construction through reflection
  }

  public long getArrTime() {
    return mArrTime;
  }

  public long getDepTime() {
    return mDepTime;
  }

  public List<RouteElement> getRoute() {
    return mRoute;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Journey [depTime=");
    builder.append(mDepTime);
    builder.append(", arrTime=");
    builder.append(mArrTime);
    builder.append(", route=");
    builder.append(mRoute);
    builder.append("]");
    return builder.toString();
  }

}
