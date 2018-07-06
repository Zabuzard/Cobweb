package de.tischner.cobweb.routing.model.timetable;

import org.onebusaway.gtfs.model.AgencyAndId;

public final class SequenceStopTime {
  private final int mArrTime;
  private final int mDepTime;
  private final AgencyAndId mStopId;

  public SequenceStopTime(final int arrTime, final int depTime, final AgencyAndId stopId) {
    mArrTime = arrTime;
    mDepTime = depTime;
    mStopId = stopId;
  }

  public int getArrTime() {
    return mArrTime;
  }

  public int getDepTime() {
    return mDepTime;
  }

  public AgencyAndId getStopId() {
    return mStopId;
  }
}
