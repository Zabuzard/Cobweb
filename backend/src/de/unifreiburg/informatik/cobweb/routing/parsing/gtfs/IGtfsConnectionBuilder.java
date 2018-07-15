package de.unifreiburg.informatik.cobweb.routing.parsing.gtfs;

import de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IHasId;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ISpatial;

/**
 * Interface for objects that can construct connections out of GTFS data.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 */
public interface IGtfsConnectionBuilder<N extends INode & IHasId & ISpatial, E extends IEdge<N>> {
  /**
   * Builds an edge with the given source, destination and cost.
   *
   * @param source      The source of the edge
   * @param destination The destination of the edge
   * @param cost        The cost of the edge, in travel time as seconds
   * @return The constructed edge
   */
  E buildEdge(N source, N destination, double cost);

  /**
   * Builds a note with the given coordinates and time.
   *
   * @param latitude  The latitude of the node
   * @param longitude The longitude of the node
   * @param time      The time of the node in seconds since midnight
   * @return The constructed node
   */
  N buildNode(float latitude, float longitude, int time);
}
