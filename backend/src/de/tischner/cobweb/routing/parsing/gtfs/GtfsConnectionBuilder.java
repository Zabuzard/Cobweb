package de.tischner.cobweb.routing.parsing.gtfs;

import de.tischner.cobweb.routing.model.graph.IGetNodeById;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.transit.ITransitIdGenerator;
import de.tischner.cobweb.routing.model.graph.transit.TransitEdge;
import de.tischner.cobweb.routing.model.graph.transit.TransitNode;

/**
 * Implementation of an {@link IGtfsConnectionBuilder} which builds instances of
 * {@link TransitNode}s and {@link TransitEdge}s.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <G> The type of the graph which must be able to get nodes by their IDs
 */
public final class GtfsConnectionBuilder<
    G extends IGraph<TransitNode, TransitEdge<TransitNode>> & IGetNodeById<TransitNode>>
    implements IGtfsConnectionBuilder<TransitNode, TransitEdge<TransitNode>> {
  /**
   * The graph to operate on.
   */
  private final G mGraph;
  /**
   * A generator which provides unique IDs for nodes and ways.
   */
  private final ITransitIdGenerator mIdGenerator;

  /**
   * Creates a new GTFS connection builder which operates on the given graph.
   *
   * @param graph       The graph to operate on
   * @param idGenerator The generator to use for generating unique IDs for nodes
   *                    and edges
   */
  public GtfsConnectionBuilder(final G graph, final ITransitIdGenerator idGenerator) {
    mGraph = graph;
    mIdGenerator = idGenerator;
  }

  @Override
  public TransitEdge<TransitNode> buildEdge(final TransitNode source, final TransitNode destination,
      final double cost) {
    return new TransitEdge<>(mIdGenerator.generateUniqueEdgeId(), source, destination, cost);
  }

  @Override
  public TransitNode buildNode(final float latitude, final float longitude) {
    return new TransitNode(mIdGenerator.generateUniqueNodeId(), latitude, longitude);
  }

}
