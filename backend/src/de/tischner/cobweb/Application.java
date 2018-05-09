package de.tischner.cobweb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.util.ContextInitializer;
import de.tischner.cobweb.config.ConfigLoader;
import de.tischner.cobweb.config.ConfigStore;
import de.tischner.cobweb.db.ExternalDatabase;
import de.tischner.cobweb.db.IRoutingDatabase;
import de.tischner.cobweb.db.MemoryDatabase;
import de.tischner.cobweb.db.OsmDatabaseHandler;
import de.tischner.cobweb.parsing.DataParser;
import de.tischner.cobweb.parsing.ParseException;
import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.tischner.cobweb.parsing.osm.IOsmFilter;
import de.tischner.cobweb.routing.algorithms.metrics.IMetric;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.ILandmarkProvider;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.LandmarkMetric;
import de.tischner.cobweb.routing.algorithms.metrics.landmark.RandomLandmarks;
import de.tischner.cobweb.routing.algorithms.shortestpath.IShortestPathComputation;
import de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.AStar;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.tischner.cobweb.routing.parsing.osm.IOsmRoadBuilder;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadBuilder;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadFilter;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadHandler;
import de.tischner.cobweb.routing.server.RoutingServer;
import de.tischner.cobweb.util.SerializationUtil;

/**
 * The whole application. Supports various commands, see the documentation of
 * the constructors for detail.<br>
 * <br>
 * Use {@link #initialize()} after creation. The application can then be
 * operated using {@link #start()} and {@link #shutdown()}.<br>
 * <br>
 * The application consists of a routing server which offers a REST API, a
 * database that stores meta information and a search server used as utility
 * which offers a REST API too.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class Application {
  /**
   * Path to the configuration of the logger.
   */
  private static final Path LOGGER_CONFIG = Paths.get("backend", "res", "logging", "logConfig.xml");
  /**
   * Provided arguments that are used to determine the commands to use.
   */
  private final String[] mArgs;
  /**
   * Algorithm to use for shortest path computation.
   */
  private IShortestPathComputation<RoadNode, RoadEdge<RoadNode>> mComputation;
  /**
   * Provides the configuration of the application.
   */
  private ConfigStore mConfig;
  /**
   * Utility used to load and save the configuration.
   */
  private ConfigLoader mConfigLoader;
  /**
   * Database to use for storing routing meta data.
   */
  private IRoutingDatabase mDatabase;
  /**
   * Graph to route on.
   */
  private RoadGraph<RoadNode, RoadEdge<RoadNode>> mGraph;
  /**
   * Logger to use for logging.
   */
  private Logger mLogger;
  /**
   * Server to use for responding to routing requests. Offers a REST API.
   */
  private RoutingServer<RoadNode, RoadEdge<RoadNode>, RoadGraph<RoadNode, RoadEdge<RoadNode>>> mRoutingServer;

  /**
   * Creates a new application using the given arguments. After creation use
   * {@link #initialize()} and then {@link #start()} and {@link #shutdown()} to
   * control the application.<br>
   * <br>
   * Supported commands are
   * <ul>
   * <li><b><tt>empty</tt></b> or <b><tt>args[0] = start</tt></b>: Starts the
   * default service which answers routing requests over a REST API.</li>
   * <li><b><tt>args[0] = reduce</tt></b>: Reduces all input data such that the
   * default service will run faster.</li>
   * <li><b><tt>args[0] = clear</tt></b>: Clears the database and all cached and
   * serialized data.</li>
   * </ul>
   *
   * @param args The arguments that specify which command to use.
   */
  public Application(final String[] args) {
    mArgs = args;
  }

  /**
   * Initializes the application. Use this method after construction and before
   * using {@link #start()}.
   */
  public void initialize() {
    initializeLogger();
    mLogger.info("Initializing application");
    try {
      final Instant initStartTime = Instant.now();
      mConfig = new ConfigStore();
      mConfigLoader = new ConfigLoader();
      mConfigLoader.loadConfig(mConfig);
      // Save to ensure old configuration files get new default parameter
      mConfigLoader.saveConfig(mConfig);

      // TODO Choose based on arguments what to do
      // TODO Provide clean caches command, provide reducer command
      initializeApi();
      final Instant initEndTime = Instant.now();
      mLogger.info("Initialization took: {}", Duration.between(initStartTime, initEndTime));
    } catch (final Throwable e) {
      mLogger.error("Error at initialization of application", e);
      throw e;
    }
  }

  /**
   * Shuts the application down. Use this method after {@link #start()} has been
   * called. The application should not be used anymore after this method. Instead
   * create a new one.
   */
  public void shutdown() {
    // TODO Make sure this is always called
    mLogger.info("Shutting down application");
    try {
      mRoutingServer.shutdown();
      mDatabase.shutdown();
    } catch (final Throwable e) {
      mLogger.error("Error while shutting down database", e);
      throw e;
    }
  }

  /**
   * Starts the application and the service set at construction. Use this method
   * after {@link #initialize()}. The method {@link #shutdown()} can be used to
   * shut the application down after this method has been used.<br>
   * <br>
   * The method should not be used to start an application again after
   * {@link #shutdown()} has been used.
   */
  public void start() {
    try {
      mLogger.info("Starting application");
      // TODO Do something, based on arguments
      mRoutingServer.start();
    } catch (final Throwable e) {
      mLogger.error("Error while starting application", e);
      throw e;
    }
  }

  /**
   * Creates file handler that handle OSM files for the database. If they are
   * notified when parsing OSM data, they will adjust the database accordingly.
   *
   * @return An iterable consisting of all OSM file handlers that adjust the
   *         database
   */
  private Iterable<IOsmFileHandler> createOsmDatabaseHandler() {
    try {
      final IOsmFileHandler databaseHandler = new OsmDatabaseHandler(mDatabase, mConfig);
      return Collections.singletonList(databaseHandler);
    } catch (final IOException e) {
      throw new ParseException(e);
    }
  }

  /**
   * Creates file handler that handle OSM files for routing. If they are notified
   * when parsing OSM data, they will adjust models used for routing like the
   * graph accordingly.
   *
   * @return An iterable consisting of all routing file handlers that adjust
   *         routing models
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files
   */
  private Iterable<IOsmFileHandler> createOsmRoutingHandler() throws ParseException {
    final IOsmRoadBuilder<RoadNode, RoadEdge<RoadNode>> roadBuilder = new OsmRoadBuilder<>(mGraph);
    final IOsmFilter roadFilter = new OsmRoadFilter(mConfig);
    try {
      final IOsmFileHandler roadHandler = new OsmRoadHandler<>(mGraph, roadFilter, roadBuilder, mDatabase, mConfig);
      return Collections.singletonList(roadHandler);
    } catch (final IOException e) {
      throw new ParseException(e);
    }
  }

  /**
   * Initializes the API of the application. This consists of the routing server,
   * the database, the search server and all corresponding utilities and models.
   *
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files
   */
  private void initializeApi() throws ParseException {
    final Instant initAPIStartTime = Instant.now();

    initializeDatabase();
    initializeGraph();

    final int graphSizeBefore = mGraph.size();

    // Prepare data parsing
    final DataParser dataParser = new DataParser(mConfig);
    // Add OSM handler
    createOsmDatabaseHandler().forEach(dataParser::addOsmHandler);
    createOsmRoutingHandler().forEach(dataParser::addOsmHandler);
    // Parse all data
    final Instant parseStartTime = Instant.now();
    dataParser.parseData();
    final Instant parseEndTime = Instant.now();
    mLogger.info("Parsing took: {}", Duration.between(parseStartTime, parseEndTime));

    serializeGraphIfDesired(graphSizeBefore);

    mLogger.info("Graph size: {}", mGraph.getSizeInformation());

    initializeRouting();

    final Instant initEndTime = Instant.now();
    mLogger.info("Initialization of API took: {}", Duration.between(initAPIStartTime, initEndTime));
  }

  /**
   * Initializes the database which stores meta data used for routing. Depending
   * on the configuration this may create tables in an external database or create
   * an internal in-memory database.
   *
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files. Or when a problem with an
   *                        external database occurred like passing invalid SQL or
   *                        if a connection could not be established.
   */
  private void initializeDatabase() throws ParseException {
    mLogger.info("Initializing database");

    if (mConfig.useExternalDb()) {
      mDatabase = new ExternalDatabase(mConfig);
    } else {
      mDatabase = new MemoryDatabase();
    }

    mDatabase.initialize();
  }

  /**
   * Initializes the graph model to use for routing. Depending on the
   * configuration this may deserialize a previous serialized graph. If the
   * deserialized graph is big this method may take a while.
   *
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files or if the graph to deserialize is
   *                        invalid.
   */
  private void initializeGraph() throws ParseException {
    mLogger.info("Initializing graph");

    final Path graphCache = mConfig.getGraphCache();
    if (!mConfig.useGraphCache() || !Files.isRegularFile(graphCache)) {
      mGraph = new RoadGraph<>();
      return;
    }

    // Deserialize graph
    mLogger.info("Deserializing graph from: {}", graphCache);
    final SerializationUtil<RoadGraph<RoadNode, RoadEdge<RoadNode>>> serializationUtil = new SerializationUtil<>();
    try {
      mGraph = serializationUtil.deserialize(graphCache);
    } catch (ClassNotFoundException | ClassCastException | IOException e) {
      throw new ParseException(e);
    }
  }

  /**
   * Initializes the logger to use for logging. This sets system wide properties
   * such that all subsequent calls to {@link LoggerFactory} are affected. As
   * such, it should be used <b>as soon as possible</b> to ensure the logger is
   * ready to be used in.
   */
  private void initializeLogger() {
    System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, LOGGER_CONFIG.toString());
    mLogger = LoggerFactory.getLogger(Application.class);
  }

  /**
   * Initializes the routing server and algorithms used to answer routing
   * requests. Depending on the size of the graph and the used algorithms this
   * method may take a while for all precomputations to finish.
   */
  private void initializeRouting() {
    mLogger.info("Initializing routing");

    final Instant preCompTimeStart = Instant.now();
    // Create the shortest path algorithm
    final ILandmarkProvider<RoadNode> landmarkProvider = new RandomLandmarks<>(mGraph);
    // TODO Adjust the amount of landmarks, use some constant
    final IMetric<RoadNode> metric = new LandmarkMetric<>(50, mGraph, landmarkProvider);
    mComputation = new AStar<>(mGraph, metric);
    final Instant preCompTimeEnd = Instant.now();
    mLogger.info("Precomputation took: {}", Duration.between(preCompTimeStart, preCompTimeEnd));

    mRoutingServer = new RoutingServer<>(mConfig, mGraph, mComputation, mDatabase);
    mRoutingServer.initialize();
  }

  /**
   * Serializes the graph model if desired. That is, if the size of the graph has
   * changed and the configuration contains the property to use the graph cache.
   *
   * @param graphSizeBefore The size of the graph after it was deserialized. Used
   *                        to determine if the current graph has changed compared
   *                        to the serialized version.
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files or if an exception at
   *                        serialization occurred
   */
  private void serializeGraphIfDesired(final int graphSizeBefore) throws ParseException {
    if (!mConfig.useGraphCache() || mGraph.size() == graphSizeBefore) {
      return;
    }

    final Path graphCache = mConfig.getGraphCache();
    mLogger.info("Serializing graph to: {}", graphCache);
    final SerializationUtil<RoadGraph<RoadNode, RoadEdge<RoadNode>>> serializationUtil = new SerializationUtil<>();
    try {
      serializationUtil.serialize(mGraph, graphCache);
    } catch (final IOException e) {
      throw new ParseException(e);
    }
  }

}
