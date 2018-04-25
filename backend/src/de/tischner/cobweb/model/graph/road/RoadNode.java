package de.tischner.cobweb.model.graph.road;

import de.tischner.cobweb.model.graph.INode;

public final class RoadNode implements INode, IHasId {

  private final long mId;

  public RoadNode(final long id) {
    mId = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tischner.cobweb.model.graph.road.IHasId#getId()
   */
  @Override
  public long getId() {
    return mId;
  }

}
