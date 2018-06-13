package de.tischner.cobweb.routing.model.graph.road;

import de.tischner.cobweb.routing.model.graph.ETransportationMode;

/**
 * Indicator interface for road edges.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IRoadEdge {
  /**
   * The cost of this edge for using the given transportation mode. Measured in
   * seconds, interpreted as travel time with the maximal allowed or average
   * speed for the given highway type.
   *
   * @param mode The transportation mode to use
   * @return The cost of the edge
   */
  public double getCost(ETransportationMode mode);
}
