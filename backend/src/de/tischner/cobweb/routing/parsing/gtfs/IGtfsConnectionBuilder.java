package de.tischner.cobweb.routing.parsing.gtfs;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.ISpatial;

/**
 * Interface for objects that can construct connections out of GTFS data.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 */
public interface IGtfsConnectionBuilder<N extends INode & IHasId & ISpatial, E extends IEdge<N>> {
  E buildEdge(N source, N destination, double cost);

  N buildNode(float latitude, float longitude);
}
