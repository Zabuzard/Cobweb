package de.tischner.cobweb.routing.server.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * POJO that models a routing request.<br>
 * <br>
 * A request consists of departure time, source and destination nodes and
 * meta-data like desired transportation modes.<br>
 * <br>
 * It has the exact structure that is expected as request format for the REST
 * API. It is primarily used to be constructed from the clients JSON request.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RoutingRequest {
  /**
   * The departure time to start journeys with, in milliseconds since epoch.
   */
  private long mDepTime;
  /**
   * The unique ID of the node to start the journey from.
   */
  private long mFrom;
  /**
   * An array containing all allowed transportation modes. The values are to be
   * interpreted as values corresponding to the enum
   * {@link ETransportationMode}.
   */
  private int[] mModes;
  /**
   * The unique ID of the node to end the journey at.
   */
  private long mTo;

  /**
   * Creates a new routing request.
   *
   * @param from    The unique ID of the node to start the journey from
   * @param to      The unique ID of the node to end the journey at
   * @param depTime The departure time to start journeys with, in milliseconds
   *                since epoch
   * @param modes   A set containing all allowed transportation modes, must not
   *                be empty
   */
  public RoutingRequest(final long from, final long to, final long depTime, final Set<ETransportationMode> modes) {
    mFrom = from;
    mTo = to;
    mDepTime = depTime;
    setTransportationModes(modes);
  }

  /**
   * Creates a new empty routing request. Is used to construct the element via
   * reflection.
   */
  @SuppressWarnings("unused")
  private RoutingRequest() {
    // Empty constructor for construction through reflection
  }

  /**
   * Gets the departure time to start journeys with, in milliseconds since
   * epoch.
   *
   * @return The departure time in milliseconds since epoch
   */
  public long getDepTime() {
    return mDepTime;
  }

  /**
   * Gets the unique ID of the node to start the journey from.
   *
   * @return The ID of the source node
   */
  public long getFrom() {
    return mFrom;
  }

  /**
   * Gets a set containing all allowed transportation modes.
   *
   * @return A set containing all allowed transportation modes
   */
  public Set<ETransportationMode> getModes() {
    return Arrays.stream(mModes).mapToObj(ETransportationMode::fromValue).collect(Collectors.toSet());
  }

  /**
   * Gets the unique ID of the node to end the journey at.
   *
   * @return The ID of the destination node
   */
  public long getTo() {
    return mTo;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("RoutingRequest [from=");
    builder.append(mFrom);
    builder.append(", to=");
    builder.append(mTo);
    builder.append(", depTime=");
    builder.append(mDepTime);
    builder.append(", modes=");
    builder.append(Arrays.toString(mModes));
    builder.append("]");
    return builder.toString();
  }

  /**
   * Sets the allowed transportation modes.
   *
   * @param modes The transportation modes to set, must not be empty
   */
  private void setTransportationModes(final Set<ETransportationMode> modes) {
    mModes = new int[modes.size()];
    int i = 0;
    for (final ETransportationMode mode : modes) {
      mModes[i] = mode.getValue();
      i++;
    }
  }
}
