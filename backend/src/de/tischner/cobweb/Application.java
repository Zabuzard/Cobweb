package de.tischner.cobweb;

import java.util.Collections;

import de.tischner.cobweb.parsing.DataParser;
import de.tischner.cobweb.parsing.ParseException;
import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.tischner.cobweb.parsing.osm.IOsmFilter;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.tischner.cobweb.routing.parsing.osm.IOsmRoadBuilder;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadBuilder;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadFilter;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadHandler;
import de.tischner.cobweb.routing.server.RoutingServer;

public final class Application {
  private final String[] mArgs;
  private RoadGraph<RoadNode, RoadEdge<RoadNode>> mGraph;
  private RoutingServer<RoadNode, RoadEdge<RoadNode>, RoadGraph<RoadNode, RoadEdge<RoadNode>>> mRoutingServer;

  public Application(final String[] args) {
    mArgs = args;
    mGraph = null;
    mRoutingServer = null;
  }

  public void initialize() {
    // TODO Error handling
    // TODO Gather configuration files
    // TODO Choose based on arguments what to do
//    initializeReducer();
    initializeApi();
  }

  public void start() {
    // TODO Do something, based on arguments
  }

  private Iterable<IOsmFileHandler> createOsmDatabaseHandler() {
    // TODO Implement something
    return Collections.emptyList();
  }

  private Iterable<IOsmFileHandler> createOsmRoutingHandler() {
    final IOsmRoadBuilder<RoadNode, RoadEdge<RoadNode>> roadBuilder = new OsmRoadBuilder<>(mGraph);
    // TODO Filter needs to get some config
    final IOsmFilter roadFilter = new OsmRoadFilter();
    final IOsmFileHandler roadHandler = new OsmRoadHandler<>(mGraph, roadFilter, roadBuilder);
    return Collections.singletonList(roadHandler);
  }

  private void initializeApi() throws ParseException {
    // TODO Method should get some config files
    // TODO Decide if parsing is need by checking caches
    mGraph = new RoadGraph<>();
    final DataParser dataParser = new DataParser(null);
    // Add OSM handler
    createOsmDatabaseHandler().forEach(dataParser::addOsmHandler);
    createOsmRoutingHandler().forEach(dataParser::addOsmHandler);
    // Parse all data
    dataParser.parseData();

    // TODO Initialize routing and database
    initializeRouting();
    initializeDatabase();
  }

  private void initializeDatabase() {
    // TODO Implement something
  }

  private void initializeRouting() {
    // TODO Pass some config and algorithm
    mRoutingServer = new RoutingServer<>(null, mGraph);
  }

}
