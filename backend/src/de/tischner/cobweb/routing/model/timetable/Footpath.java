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

  public int getArrId() {
    return mArrId;
  }

  public int getDepId() {
    return mDepId;
  }

  public int getDuration() {
    return mDuration;
  }
}
