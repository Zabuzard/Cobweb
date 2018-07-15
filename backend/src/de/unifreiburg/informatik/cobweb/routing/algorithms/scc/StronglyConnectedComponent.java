package de.unifreiburg.informatik.cobweb.routing.algorithms.scc;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents a strongly connected component (SCC). That is a
 * collection of nodes where every node can reach all other nodes in the SCC.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 */
public final class StronglyConnectedComponent<N> {
  /**
   * The set of nodes contained in this SCC.
   */
  private final Set<N> mNodes;
  /**
   * The root node of this SCC.
   */
  private N mRootNode;

  /**
   * Creates a new initially empty SCC.
   */
  public StronglyConnectedComponent() {
    mNodes = new HashSet<>();
  }

  /**
   * Adds the given node to the SCC if not already contained.
   *
   * @param node The node to add
   * @return <tt>True</tt> if the node was not already contained and thus added,
   *         <tt>false</tt> otherwise
   */
  public boolean addNode(final N node) {
    return mNodes.add(node);
  }

  /**
   * Gets the nodes contained in this SCC.
   *
   * @return The nodes contained in this SCC
   */
  public Set<N> getNodes() {
    return mNodes;
  }

  /**
   * Gets the root node of this SCC.
   *
   * @return The root node or <tt>null</tt> if not set
   */
  public N getRootNode() {
    return mRootNode;
  }

  /**
   * Sets the root node of this SCC.
   *
   * @param rootNode The root node to set
   */
  public void setRootNode(final N rootNode) {
    mRootNode = rootNode;
  }

  /**
   * Gets the size of this SCC, i.e. the amount of contained nodes.
   *
   * @return The size of this SCC
   */
  public int size() {
    return mNodes.size();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
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
