package de.tischner.cobweb.routing.model.timetable;

/**
 * POJO representing a footpath. A footpath connects two transit stops with each
 * other and has a duration.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class Footpath {
  /**
   * The unique ID of the footpaths arrival stop.
   */
  private final int mArrStopId;
  /**
   * The unique ID of the footpaths departure stop.
   */
  private final int mDepStopId;
  /**
   * The duration it takes to walk the footpath, in seconds.
   */
  private final int mDuration;

  /**
   * Creates a new footpath.
   *
   * @param depStopId The unique ID of the footpaths departure stop
   * @param arrStopId The unique ID of the footpaths arrival stop
   * @param duration  The duration it takes to walk the footpath, in seconds
   */
  public Footpath(final int depStopId, final int arrStopId, final int duration) {
    mDepStopId = depStopId;
    mArrStopId = arrStopId;
    mDuration = duration;
  }

  /**
   * Gets the unique ID of the footpaths arrival stop.
   *
   * @return The unique ID of the footpaths arrival stop
   */
  public int getArrStopId() {
    return mArrStopId;
  }

  /**
   * Gets the unique ID of the footpaths departure stop.
   *
   * @return The unique ID of the footpaths departure stop
   */
  public int getDepStopId() {
    return mDepStopId;
  }

  /**
   * Gets the duration it takes to walk the footpath, in seconds.
   *
   * @return The duration it takes to walk the footpath, in seconds
   */
  public int getDuration() {
    return mDuration;
  }
}
