package de.tischner.cobweb.routing.model.timetable;

import java.io.Serializable;

import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.ISpatial;

public final class Stop implements ISpatial, IHasId, Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;

  private final int mId;

  private float mLatitude;

  private float mLongitude;

  public Stop(final int id, final float latitude, final float longitude) {
    mId = id;
    mLatitude = latitude;
    mLongitude = longitude;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Stop)) {
      return false;
    }
    final Stop other = (Stop) obj;
    if (this.mId != other.mId) {
      return false;
    }
    return true;
  }

  @Override
  public int getId() {
    return mId;
  }

  @Override
  public float getLatitude() {
    return mLatitude;
  }

  @Override
  public float getLongitude() {
    return mLongitude;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.mId;
    return result;
  }

  @Override
  public void setLatitude(final float latitude) {
    mLatitude = latitude;
  }

  @Override
  public void setLongitude(final float longitude) {
    mLongitude = longitude;
  }
}
