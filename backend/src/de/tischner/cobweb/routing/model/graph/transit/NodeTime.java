package de.tischner.cobweb.routing.model.graph.transit;

import de.tischner.cobweb.routing.model.graph.INode;

public final class NodeTime<N extends INode> implements Comparable<NodeTime<N>> {
  private final N mNode;
  private int mTime;

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

  public void setTime(final int time) {
    mTime = time;
  }
}
