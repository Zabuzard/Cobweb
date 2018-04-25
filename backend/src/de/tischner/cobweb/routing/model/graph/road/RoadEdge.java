package de.tischner.cobweb.routing.model.graph.road;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;

public final class RoadEdge<N extends INode & IHasId> implements IEdge<N>, IHasId {

  private final double mCost;
  private final N mDestination;
  private final long mId;
  private final N mSource;

  public RoadEdge(final long id, final N source, final N destination, final double cost) {
    mId = id;
    mSource = source;
    mDestination = destination;
    mCost = cost;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tischner.cobweb.model.graph.IEdge#getCost()
   */
  @Override
  public double getCost() {
    return mCost;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.tischner.cobweb.model.graph.IEdge#getDesintation()
   */
  @Override
  public N getDesintation() {
    return mDestination;
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

  /*
   * (non-Javadoc)
   * 
   * @see de.tischner.cobweb.model.graph.IEdge#getSource()
   */
  @Override
  public N getSource() {
    return mSource;
  }

}
