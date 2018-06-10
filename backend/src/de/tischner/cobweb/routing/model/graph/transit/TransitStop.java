package de.tischner.cobweb.routing.model.graph.transit;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.tischner.cobweb.routing.model.graph.INode;

public final class TransitStop<N extends INode> implements Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  private final List<NodeTime<N>> mArrivalNodes;
  private final float mLatitude;
  private final float mLongitude;
  private final NodeTime<N> mNodeTimeNeedle;

  public TransitStop(final List<NodeTime<N>> arrivalNodes, final float latitude, final float longitude) {
    // The list must be sorted and should implement RandomAccess
    mArrivalNodes = arrivalNodes;
    mNodeTimeNeedle = new NodeTime<>(null, 0);
    mLatitude = latitude;
    mLongitude = longitude;
  }

  public Collection<NodeTime<N>> getArrivalNodes() {
    return mArrivalNodes;
  }

  public float getLatitude() {
    return mLatitude;
  }

  public float getLongitude() {
    return mLongitude;
  }

  public NodeTime<N> getNextArrivalNode(final int time) {
    mNodeTimeNeedle.setTime(time);
    final int indexOfNext = -1 * Collections.binarySearch(mArrivalNodes, mNodeTimeNeedle) - 1;
    return mArrivalNodes.get(indexOfNext);
  }
}
