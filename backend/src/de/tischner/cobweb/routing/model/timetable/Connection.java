package de.tischner.cobweb.routing.model.timetable;

import java.io.Serializable;
import java.util.Comparator;

public final class Connection implements Comparable<Connection>, Serializable {
  private static final Comparator<Connection> DEP_TIME_ASCENDING = Comparator.comparing(Connection::getDepTime)
      .thenComparing(Connection::getTripId).thenComparing(Connection::getArrTime)
      .thenComparing(Connection::getDepStopId).thenComparing(Connection::getArrStopId);
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  private final int mArrStopId;
  private final int mArrTime;
  private final int mDepStopId;
  private final int mDepTime;
  private final int mTripId;

  public Connection(final int tripId, final int depStopId, final int arrStopId, final int depTime, final int arrTime) {
    mTripId = tripId;
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
    if (this.mArrStopId != other.mArrStopId) {
      return false;
    }
    if (this.mArrTime != other.mArrTime) {
      return false;
    }
    if (this.mDepStopId != other.mDepStopId) {
      return false;
    }
    if (this.mDepTime != other.mDepTime) {
      return false;
    }
    if (this.mTripId != other.mTripId) {
      return false;
    }
    return true;
  }

  public int getArrStopId() {
    return mArrStopId;
  }

  public int getArrTime() {
    return mArrTime;
  }

  public int getDepStopId() {
    return mDepStopId;
  }

  public int getDepTime() {
    return mDepTime;
  }

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
    result = prime * result + this.mArrStopId;
    result = prime * result + this.mArrTime;
    result = prime * result + this.mDepStopId;
    result = prime * result + this.mDepTime;
    result = prime * result + this.mTripId;
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Connection [");
    builder.append(this.mArrStopId);
    builder.append("@");
    builder.append(this.mArrTime);
    builder.append(" -> ");
    builder.append(this.mDepStopId);
    builder.append("@");
    builder.append(this.mDepTime);
    builder.append(", trip=");
    builder.append(this.mTripId);
    builder.append("]");
    return builder.toString();
  }
}
