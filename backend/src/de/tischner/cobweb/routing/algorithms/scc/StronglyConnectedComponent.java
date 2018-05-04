package de.tischner.cobweb.routing.algorithms.scc;

import java.util.HashSet;
import java.util.Set;

import de.tischner.cobweb.routing.model.graph.INode;

public final class StronglyConnectedComponent<N extends INode> {

  private final Set<N> mNodes;
  private N mRootNode;

  public StronglyConnectedComponent() {
    mNodes = new HashSet<>();
  }

  public boolean addNode(final N node) {
    return mNodes.add(node);
  }

  public Set<N> getNodes() {
    return mNodes;
  }

  public N getRootNode() {
    return mRootNode;
  }

  public int getSize() {
    return mNodes.size();
  }

  public void setRootNode(final N rootNode) {
    mRootNode = rootNode;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("StronglyConnectedComponent [rootNode=");
    builder.append(mRootNode);
    builder.append(", nodes=");
    builder.append(mNodes);
    builder.append("]");
    return builder.toString();
  }
}
