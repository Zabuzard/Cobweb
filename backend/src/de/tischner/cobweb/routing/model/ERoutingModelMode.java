package de.tischner.cobweb.routing.model;

/**
 * Modes of the routing model.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public enum ERoutingModelMode {
  /**
   * Mode representing the usage of a road graph together with a timetable for
   * transit data. Using a graph-based algorithm for the road network and a
   * timetable-based algorithm for the transit network.
   */
  GRAPH_WITH_TIMETABLE,
  /**
   * Mode representing the usage of a combined graph that links a road and
   * transit graph together. Using a graph-based algorithm for the whole network
   * at once.
   */
  LINK_GRAPH
}
