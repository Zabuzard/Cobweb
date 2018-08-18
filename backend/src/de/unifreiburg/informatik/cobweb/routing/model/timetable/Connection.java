package de.unifreiburg.informatik.cobweb.routing.model.timetable;

import java.io.Serializable;
import java.util.Comparator;

/**
 * POJO for a connection between two transit stops at given times. A connection
 * belongs to a trip which represents a sequence of connections.<br>
 * <br>
 * The natural order of a connection is ascending in departure time, then the
 * trip ID and the sequence index, after that all other fields.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class Connection implements Comparable<Connection>, Serializable {
  /**
   * Comparator that represents the natural order of connections. That is,
   * ascending in departure time, then the trip ID and sequence index. After
   * that all other fields.
   */
  private static final Comparator<Connection> DEP_TIME_ASCENDING =
      Comparator.comparing(Connection::getDepTime).thenComparing(Connection::getTripId)
          .thenComparing(Connection::getSequenceIndex).thenComparing(Connection::getArrTime)
          .thenComparing(Connection::getDepStopId).thenComparing(Connection::getArrStopId);
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The unique ID of the connections arrival stop.
   */
  private final int mArrStopId;
  /**
   * The time the connection arrives at the arrival stop, in seconds since
   * midnight.
   */
  private final int mArrTime;
  /**
   * The unique ID of the connections departure stop.
   */
  private final int mDepStopId;
  /**
   * The time the connection arrives at the departure stop, in seconds since
   * midnight.
   */
  private final int mDepTime;
  /**
   * The index of the connection in the connection sequence of the trip it
   * belongs to.
   */
  private final int mSequenceIndex;
  /**
   * The unique ID of the trip the connection belongs to.
   */
  private final int mTripId;

  /**
   * Creates a new connection.
   *
   * @param tripId        The unique ID of the trip the connection belongs to
   * @param sequenceIndex The index of the connection in the connection sequence
   *                      of the trip it belongs to
   * @param depStopId     The unique ID of the connections departure stop
   * @param arrStopId     The unique ID of the connections arrival stop
   * @param depTime       The time the connection arrives at the departure stop,
   *                      in seconds since midnight
   * @param arrTime       The time the connection arrives at the arrival stop,
   *                      in seconds since midnight
   */
  public Connection(final int tripId, final int sequenceIndex, final int depStopId, final int arrStopId,
      final int depTime, final int arrTime) {
    mTripId = tripId;
    mSequenceIndex = sequenceIndex;
    mDepStopId = depStopId;
    mArrStopId = arrStopId;
    mDepTime = depTime;
    mArrTime = arrTime;
  }

  @Override
  public int compareTo(final Connection other) {
    return DEP_TIME_ASCENDING.compare(this, other);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Connection)) {
      return false;
    }
    final Connection other = (Connection) obj;
    if (mArrStopId != other.mArrStopId) {
      return false;
    }
    if (mArrTime != other.mArrTime) {
      return false;
    }
    if (mDepStopId != other.mDepStopId) {
      return false;
    }
    if (mDepTime != other.mDepTime) {
      return false;
    }
    if (mTripId != other.mTripId) {
      return false;
    }
    if (mSequenceIndex != other.mSequenceIndex) {
      return false;
    }
    return true;
  }

  /**
   * Gets the unique ID of the connections arrival stop.
   *
   * @return The unique ID of the connections arrival stop
   */
  public int getArrStopId() {
    return mArrStopId;
  }

  /**
   * Gets the time the connection arrives at the arrival stop, in seconds since
   * midnight.
   *
   * @return The time the connection arrives at the arrival stop, in seconds
   *         since midnight
   */
  public int getArrTime() {
    return mArrTime;
  }

  /**
   * Gets the unique ID of the connections departure stop.
   *
   * @return The unique ID of the connections departure stop
   */
  public int getDepStopId() {
    return mDepStopId;
  }

  /**
   * Gets the time the connection arrives at the departure stop, in seconds
   * since midnight.
   *
   * @return The time the connection arrives at the departure stop, in seconds
   *         since midnight
   */
  public int getDepTime() {
    return mDepTime;
  }

  /**
   * Gets the index of the connection in the connection sequence of the trip it
   * belongs to.
   *
   * @return The index of the connection in the connection sequence of the trip
   *         it belongs to.
   */
  public int getSequenceIndex() {
    return mSequenceIndex;
  }

  /**
   * Gets the unique ID of the trip the connection belongs to.
   *
   * @return The unique ID of the trip the connection belongs to
   */
  public int getTripId() {
    return mTripId;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + mArrStopId;
    result = prime * result + mArrTime;
    result = prime * result + mDepStopId;
    result = prime * result + mDepTime;
    result = prime * result + mTripId;
    result = prime * result + mSequenceIndex;
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Connection [");
    builder.append(mDepStopId);
    builder.append("@");
    builder.append(mDepTime);
    builder.append(" -> ");
    builder.append(mArrStopId);
    builder.append("@");
    builder.append(mArrTime);
    builder.append(", trip=");
    builder.append(mTripId);
    builder.append(", sequenceIndex=");
    builder.append(mSequenceIndex);
    builder.append("]");
    return builder.toString();
  }
}
