package de.tischner.cobweb.routing.parsing.gtfs;

import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.ISpatial;

public final class TripStopNodes<N extends INode & IHasId & ISpatial> {
  private final N mArrNode;
  private final int mArrTime;
  private final N mDepNode;
  private final int mDepTime;

  public TripStopNodes(final N arrNode, final N depNode, final int arrTime, final int depTime) {
    mArrNode = arrNode;
    mDepNode = depNode;
    mArrTime = arrTime;
    mDepTime = depTime;
  }

  public N getArrNode() {
    return mArrNode;
  }

  public int getArrTime() {
    return mArrTime;
  }

  public N getDepNode() {
    return mDepNode;
  }

  public int getDepTime() {
    return mDepTime;
  }
}
