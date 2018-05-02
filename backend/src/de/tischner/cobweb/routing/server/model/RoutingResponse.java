package de.tischner.cobweb.routing.server.model;

import java.util.List;

public final class RoutingResponse {
  private long mFrom;
  private List<Journey> mJournes;
  private long mTo;

  public RoutingResponse(final long from, final long to, final List<Journey> journeys) {
    mFrom = from;
    mTo = to;
    mJournes = journeys;
  }

  private RoutingResponse() {
    // Empty constructor for construction through reflection
  }

  public long getFrom() {
    return mFrom;
  }

  public List<Journey> getJourneys() {
    return mJournes;
  }

  public long getTo() {
    return mTo;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("RoutingResponse [from=");
    builder.append(mFrom);
    builder.append(", to=");
    builder.append(mTo);
    builder.append(", journes=");
    builder.append(mJournes);
    builder.append("]");
    return builder.toString();
  }
}
