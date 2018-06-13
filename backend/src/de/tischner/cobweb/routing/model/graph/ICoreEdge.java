package de.tischner.cobweb.routing.model.graph;

import java.io.Serializable;

public interface ICoreEdge<N extends ICoreNode>
    extends IEdge<N>, IHasId, IHasTransportationMode, IReversedConsumer, Serializable {

}
