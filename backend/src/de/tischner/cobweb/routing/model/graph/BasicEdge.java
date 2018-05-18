package de.tischner.cobweb.routing.model.graph;

import java.io.Serializable;

import de.tischner.cobweb.routing.model.graph.road.IHasId;

/**
 * Basic implementation of an {@link IEdge} with an unique ID.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of node
 */
public final class BasicEdge<N extends INode> implements IEdge<N>, IHasId, Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The cost of the edge, i.e. its weight.
   */
  private final double mCost;

  /**
   * The destination node of the edge.
   */
  private final N mDestination;

  /**
   * The unique ID of the edge.
   */
  private final int mId;

  /**
   * The source node of the edge.
   */
  private final N mSource;

  /**
   * Creates a new basic edge.
   *
   * @param id          The unique ID of the edge
   * @param source      The source node of the edge
   * @param destination The destination node of the edge
   * @param cost        The cost of the edge, i.e. its weight
   */
  public BasicEdge(final int id, final N source, final N destination, final double cost) {
    mId = id;
    mSource = source;
    mDestination = destination;
    mCost = cost;
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
    if (!(obj instanceof BasicEdge)) {
      return false;
    }
    final BasicEdge<?> other = (BasicEdge<?>) obj;
    if (this.mId != other.mId) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.IEdge#getCost()
   */
  @Override
  public double getCost() {
    return mCost;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.IEdge#getDestination()
   */
  @Override
  public N getDestination() {
    return mDestination;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.road.IHasId#getId()
   */
  @Override
  public int getId() {
    return mId;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.IEdge#getSource()
   */
  @Override
  public N getSource() {
    return mSource;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.mId;
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("BasicEdge [id=");
    builder.append(mId);
    builder.append(", source=");
    builder.append(mSource);
    builder.append(", destination=");
    builder.append(mDestination);
    builder.append(", cost=");
    builder.append(mCost);
    builder.append("]");
    return builder.toString();
  }

}
