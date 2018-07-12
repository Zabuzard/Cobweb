package de.tischner.cobweb.routing.model.timetable;

public final class Footpath {
  private final int mArrId;
  private final int mDepId;
  private final int mDuration;

  public Footpath(final int depId, final int arrId, final int duration) {
    mDepId = depId;
    mArrId = arrId;
    mDuration = duration;
  }

  public int getArrStopId() {
    return mArrId;
  }

  public int getDepStopId() {
    return mDepId;
  }

  public int getDuration() {
    return mDuration;
  }
}
