package de.tischner.cobweb.routing.model.graph.transit;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.tischner.cobweb.routing.model.graph.INode;

public final class TransitStop<N extends INode> {
  private final List<NodeTime<N>> mArrivalNodes;
  private final NodeTime<N> mNodeTimeNeedle;

  public TransitStop(final List<NodeTime<N>> arrivalNodes) {
    // The list must be sorted and should implement RandomAccess
    mArrivalNodes = arrivalNodes;
    mNodeTimeNeedle = new NodeTime<>(null, 0);
  }

  public Collection<NodeTime<N>> getArrivalNodes() {
    return mArrivalNodes;
  }

  public NodeTime<N> getNextArrivalNode(final int time) {
    mNodeTimeNeedle.setTime(time);
    final int indexOfNext = -1 * Collections.binarySearch(mArrivalNodes, mNodeTimeNeedle) - 1;
    return mArrivalNodes.get(indexOfNext);
  }
}
