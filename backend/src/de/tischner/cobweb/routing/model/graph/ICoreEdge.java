package de.tischner.cobweb.routing.model.graph;

import java.io.Serializable;

/**
 * Interface for core edges. This are edges with additional common properties.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the nodes
 */
public interface ICoreEdge<N extends ICoreNode>
    extends IEdge<N>, IHasId, IHasTransportationMode, IReversedConsumer, Serializable {
  // Grouping interface, does not contain own methods at the moment
}
