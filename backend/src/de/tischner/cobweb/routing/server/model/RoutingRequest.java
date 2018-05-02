package de.tischner.cobweb.routing.server.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import de.tischner.cobweb.routing.server.ETransportationMode;

public final class RoutingRequest {
  private long mDepTime;
  private long mFrom;
  private int[] mModes;
  private long mTo;

  public RoutingRequest(final long from, final long to, final long depTime, final Set<ETransportationMode> modes) {
    mFrom = from;
    mTo = to;
    mDepTime = depTime;
    setTransportationModes(modes);
  }

  private RoutingRequest() {
    // Empty constructor for construction through reflection
  }

  public long getDepTime() {
    return mDepTime;
  }

  public long getFrom() {
    return mFrom;
  }

  public Set<ETransportationMode> getModes() {
    return Arrays.stream(mModes).mapToObj(ETransportationMode::fromValue).collect(Collectors.toSet());
  }

  public long getTo() {
    return mTo;
  }

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

  private void setTransportationModes(final Set<ETransportationMode> modes) {
    mModes = new int[modes.size()];
    int i = 0;
    for (final ETransportationMode mode : modes) {
      mModes[i] = mode.getValue();
      i++;
    }
  }
}
