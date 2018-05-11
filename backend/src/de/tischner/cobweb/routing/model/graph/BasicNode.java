package de.tischner.cobweb.routing.model.graph;

import java.io.Serializable;

import de.tischner.cobweb.routing.model.graph.road.IHasId;

/**
 * Basic implementation of a {@link INode} with an unique ID.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class BasicNode implements INode, IHasId, Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The unique ID of the node.
   */
  private final long mId;

  /**
   * Creates a new basic node with the given unique ID.
   *
   * @param id The unique ID of the node
   */
  public BasicNode(final long id) {
    mId = id;
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
    if (!(obj instanceof BasicNode)) {
      return false;
    }
    final BasicNode other = (BasicNode) obj;
    if (this.mId != other.mId) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.road.IHasId#getId()
   */
  @Override
  public long getId() {
    return mId;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (this.mId ^ (this.mId >>> 32));
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("BasicNode [id=");
    builder.append(mId);
    builder.append("]");
    return builder.toString();
  }

}
