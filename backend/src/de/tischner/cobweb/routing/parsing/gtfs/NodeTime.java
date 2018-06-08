package de.tischner.cobweb.routing.parsing.gtfs;

import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.ISpatial;

public final class NodeTime<N extends INode & IHasId & ISpatial> implements Comparable<NodeTime<N>> {
  private final N mNode;
  private final int mTime;

  public NodeTime(final N node, final int time) {
    mNode = node;
    mTime = time;
  }

  @Override
  public int compareTo(final NodeTime<N> other) {
    return Integer.compare(mTime, other.mTime);
  }

  public N getNode() {
    return mNode;
  }

  public int getTime() {
    return mTime;
  }
}
