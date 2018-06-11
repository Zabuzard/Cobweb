package de.tischner.cobweb.routing.parsing.gtfs;

import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.transit.ITransitIdGenerator;
import de.tischner.cobweb.routing.model.graph.transit.TransitEdge;
import de.tischner.cobweb.routing.model.graph.transit.TransitNode;

/**
 * Implementation of an {@link IGtfsConnectionBuilder} which builds instances of
 * {@link TransitNode}s and {@link TransitEdge}s.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class GtfsConnectionBuilder implements IGtfsConnectionBuilder<ICoreNode, ICoreEdge<ICoreNode>> {
  /**
   * A generator which provides unique IDs for nodes and ways.
   */
  private final ITransitIdGenerator mIdGenerator;

  /**
   * Creates a new GTFS connection builder which operates on the given graph.
   *
   * @param idGenerator The generator to use for generating unique IDs for nodes
   *                    and edges
   */
  public GtfsConnectionBuilder(final ITransitIdGenerator idGenerator) {
    mIdGenerator = idGenerator;
  }

  @Override
  public ICoreEdge<ICoreNode> buildEdge(final ICoreNode source, final ICoreNode destination, final double cost) {
    return new TransitEdge<>(mIdGenerator.generateUniqueEdgeId(), source, destination, cost);
  }

  @Override
  public ICoreNode buildNode(final float latitude, final float longitude, final int time) {
    return new TransitNode(mIdGenerator.generateUniqueNodeId(), latitude, longitude, time);
  }

}
