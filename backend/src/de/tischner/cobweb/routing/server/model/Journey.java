package de.tischner.cobweb.routing.server.model;

import java.util.List;

/**
 * POJO that models a journey which consist of a departure and arrival time,
 * together with a route.<br>
 * <br>
 * Is used in a {@link RoutingResponse} and usually decoded into JSON.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class Journey {
  /**
   * The arrival time at the end of the journey, in milliseconds since epoch.
   */
  private long mArrTime;
  /**
   * The departure time at the start of the journey, in milliseconds since epoch.
   */
  private long mDepTime;
  /**
   * The route the journey represents. Consists of nodes and paths.
   */
  private List<RouteElement> mRoute;

  /**
   * Creates a new journey.
   *
   * @param depTime The departure time at the start of the journey, in
   *                milliseconds since epoch
   * @param arrTime The arrival time at the end of the journey, in milliseconds
   *                since epoch
   * @param route   The route the journey represents, consists of nodes and paths
   */
  public Journey(final long depTime, final long arrTime, final List<RouteElement> route) {
    mDepTime = depTime;
    mArrTime = arrTime;
    mRoute = route;
  }

  /**
   * Creates a new empty journey. Is used to construct the journey via reflection.
   */
  @SuppressWarnings("unused")
  private Journey() {
    // Empty constructor for construction through reflection
  }

  /**
   * Gets the arrival time at the end of the journey, in milliseconds since epoch.
   *
   * @return The arrival time in milliseconds since epoch
   */
  public long getArrTime() {
    return mArrTime;
  }

  /**
   * Gets the departure time at the start of the journey, in milliseconds since
   * epoch.
   *
   * @return The departure time in milliseconds since epoch
   */
  public long getDepTime() {
    return mDepTime;
  }

  /**
   * Gets the route the journey represents. Consists of nodes and paths.
   *
   * @return The route the journey represents
   */
  public List<RouteElement> getRoute() {
    return mRoute;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
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
