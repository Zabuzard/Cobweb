package de.tischner.cobweb.routing.server.model;

import java.util.List;

/**
 * POJO that models a routing response.<br>
 * <br>
 * A response consists of departure and arrival time, together with possible
 * routes.<br>
 * <br>
 * It has the exact structure that is expected as response format for the REST
 * API. It is primarily used to be constructed and then encoded to JSON to be
 * send to the client.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class RoutingResponse {
  /**
   * The unique ID of the node to start the journeys from.
   */
  private long mFrom;
  /**
   * A list of all computed appropriate journeys.
   */
  private List<Journey> mJourneys;
  /**
   * The unique ID of the node to end the journeys at.
   */
  private long mTo;

  /**
   * Creates a new routing response.
   *
   * @param from     The unique ID of the node to start the journeys from
   * @param to       The unique ID of the node to end the journeys at
   * @param journeys A list of all computed appropriate journeys
   */
  public RoutingResponse(final long from, final long to, final List<Journey> journeys) {
    mFrom = from;
    mTo = to;
    mJourneys = journeys;
  }

  /**
   * Creates a new empty routing request. Is used to construct the element via
   * reflection.
   */
  @SuppressWarnings("unused")
  private RoutingResponse() {
    // Empty constructor for construction through reflection
  }

  /**
   * Gets the unique ID of the node to start the journeys from.
   *
   * @return The ID of the source node
   */
  public long getFrom() {
    return mFrom;
  }

  /**
   * Gets a list of all computed appropriate journeys.
   *
   * @return A list of all journeys
   */
  public List<Journey> getJourneys() {
    return mJourneys;
  }

  /**
   * Gets the unique ID of the node to end the journeys at.
   *
   * @return The ID of the destination node
   */
  public long getTo() {
    return mTo;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("RoutingResponse [from=");
    builder.append(mFrom);
    builder.append(", to=");
    builder.append(mTo);
    builder.append(", journeys=");
    builder.append(mJourneys);
    builder.append("]");
    return builder.toString();
  }
}
