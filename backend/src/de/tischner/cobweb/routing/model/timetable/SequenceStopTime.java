package de.tischner.cobweb.routing.model.timetable;

import org.onebusaway.gtfs.model.AgencyAndId;

/**
 * POJO representing a stop-time at a certain stop. That is, an arrival and
 * departure time at a stop representing a stay or transfer at the stop.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class SequenceStopTime {
  /**
   * The arrival time at the stop, in seconds since midnight.
   */
  private final int mArrTime;
  /**
   * The departure time at the stop, in seconds since midnight.
   */
  private final int mDepTime;
  /**
   * The external ID that represents the stop.
   */
  private final AgencyAndId mStopId;

  /**
   * Creates a new stop-time at the given stop.
   *
   * @param arrTime The arrival time at the stop, in seconds since midnight
   * @param depTime The departure time at the stop, in seconds since midnight
   * @param stopId  The external ID that represents the stop
   */
  public SequenceStopTime(final int arrTime, final int depTime, final AgencyAndId stopId) {
    mArrTime = arrTime;
    mDepTime = depTime;
    mStopId = stopId;
  }

  /**
   * Gets the arrival time at the stop, in seconds since midnight.
   *
   * @return The arrival time at the stop, in seconds since midnight
   */
  public int getArrTime() {
    return mArrTime;
  }

  /**
   * Gets the departure time at the stop, in seconds since midnight.
   *
   * @return The departure time at the stop, in seconds since midnight
   */
  public int getDepTime() {
    return mDepTime;
  }

  /**
   * Gets the external ID that represents the stop.
   *
   * @return The external ID that represents the stop
   */
  public AgencyAndId getStopId() {
    return mStopId;
  }
}
