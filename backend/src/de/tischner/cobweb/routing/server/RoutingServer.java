package de.tischner.cobweb.routing.server;

import de.tischner.cobweb.parsing.ParseException;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;

public final class RoutingServer {

  private final RoutingConfig mConfig;
  private final RoadGraph<RoadNode, RoadEdge<RoadNode>> mGraph;

  public RoutingServer(final RoutingConfig config) {
    mConfig = config;
    mGraph = null;
  }

  public void initialize() {
    // TODO Implement
    initializeGraph();
  }

  public boolean isRunning() {
    // TODO Implement
    return false;
  }

  public void start() {
    // TODO Implement
  }

  public void terminate() {
    // TODO Implement
  }

  private void initializeGraph() throws ParseException {
    // TODO Should get graph already in constructor
  }

}
