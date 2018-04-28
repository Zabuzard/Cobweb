package de.tischner.cobweb;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import de.tischner.cobweb.config.ConfigLoader;
import de.tischner.cobweb.config.ConfigStore;
import de.tischner.cobweb.db.Database;
import de.tischner.cobweb.db.OsmDatabaseHandler;
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
  private ConfigStore mConfig;
  private ConfigLoader mConfigLoader;
  private Database mDatabase;
  private RoadGraph<RoadNode, RoadEdge<RoadNode>> mGraph;
  private RoutingServer<RoadNode, RoadEdge<RoadNode>, RoadGraph<RoadNode, RoadEdge<RoadNode>>> mRoutingServer;

  public Application(final String[] args) {
    mArgs = args;
  }

  public void initialize() {
    // TODO Error handling
    mConfig = new ConfigStore();
    mConfigLoader = new ConfigLoader();
    mConfigLoader.loadConfig(mConfig);

    // TODO Choose based on arguments what to do
    // TODO Provide clean caches command, provide reducer command
    initializeApi();
  }

  public void shutdown() {
    // TODO Make sure this is always called
    // TODO Implement something
    mDatabase.shutdown();
  }

  public void start() {
    // TODO Do something, based on arguments
  }

  private Iterable<IOsmFileHandler> createOsmDatabaseHandler() {
    final IOsmFileHandler databaseHandler = new OsmDatabaseHandler(mDatabase);
    return Collections.singletonList(databaseHandler);
  }

  private Iterable<IOsmFileHandler> createOsmRoutingHandler() throws ParseException {
    final IOsmRoadBuilder<RoadNode, RoadEdge<RoadNode>> roadBuilder = new OsmRoadBuilder<>(mGraph);
    final IOsmFilter roadFilter = new OsmRoadFilter(mConfig);
    final IOsmFileHandler roadHandler = new OsmRoadHandler<>(mGraph, roadFilter, roadBuilder, mDatabase);
    return Collections.singletonList(roadHandler);
  }

  private void initializeApi() throws ParseException {
    initializeDatabase();
    initializeGraph();

    // TODO Decide if parsing is need by checking caches and setting up file filter
    final DataParser dataParser = new DataParser(mConfig);
    // Add OSM handler
    createOsmDatabaseHandler().forEach(dataParser::addOsmHandler);
    createOsmRoutingHandler().forEach(dataParser::addOsmHandler);
    // Parse all data
    dataParser.parseData();

    initializeRouting();
  }

  private void initializeDatabase() throws ParseException {
    mDatabase = new Database(mConfig);
    try {
      mDatabase.initialize();
    } catch (SQLException | IOException e) {
      throw new ParseException(e);
    }
  }

  private void initializeGraph() {
    // TODO Check cache and deserialize
    mGraph = new RoadGraph<>();
  }

  private void initializeRouting() {
    // TODO Pass some algorithm
    mRoutingServer = new RoutingServer<>(mConfig, mGraph, mDatabase);
  }

}
