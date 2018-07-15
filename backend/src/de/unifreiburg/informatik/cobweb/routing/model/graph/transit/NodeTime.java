package de.unifreiburg.informatik.cobweb.routing.model.graph.transit;

import java.io.Serializable;

import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;

/**
 * POJO that groups a node with time information.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the node
 */
public final class NodeTime<N extends INode> implements Comparable<NodeTime<N>>, Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The node of the wrapper.
   */
  private final N mNode;
  /**
   * The time information, in seconds since midnight.
   */
  private int mTime;

  /**
   * Creates a new node time with the given node and time.
   *
   * @param node The node to add
   * @param time The time information to add, in seconds since midnight
   */
  public NodeTime(final N node, final int time) {
    mNode = node;
    mTime = time;
  }

  /**
   * Compares node times ascending in their time.
   */
  @Override
  public int compareTo(final NodeTime<N> other) {
    final int timeCompare = Integer.compare(mTime, other.mTime);
    if (timeCompare == 0) {
      if ((mNode != null && other.mNode != null) || (mNode == null && other.mNode == null)) {
        return 0;
      } else if (mNode == null && other.mNode != null) {
        return -1;
      } else {
        return 1;
      }
    }
    return timeCompare;
  }

  /**
   * Gets the node contained in this node time.
   *
   * @return The node to get
   */
  public N getNode() {
    return mNode;
  }

  /**
   * Gets the time of this node time, in seconds since midnight.
   *
   * @return The time to get, in seconds since midnight
   */
  public int getTime() {
    return mTime;
  }

  /**
   * Sets the time of this node time.
   *
   * @param time The time to set, in seconds since midnight
   */
  public void setTime(final int time) {
    mTime = time;
  }
}
