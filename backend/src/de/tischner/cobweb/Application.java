package de.tischner.cobweb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.util.ContextInitializer;
import de.tischner.cobweb.commands.CommandData;
import de.tischner.cobweb.commands.CommandParser;
import de.tischner.cobweb.commands.ECommand;
import de.tischner.cobweb.config.ConfigLoader;
import de.tischner.cobweb.config.ConfigStore;
import de.tischner.cobweb.db.ADatabase;
import de.tischner.cobweb.db.ExternalDatabase;
import de.tischner.cobweb.db.MemoryDatabase;
import de.tischner.cobweb.db.OsmDatabaseHandler;
import de.tischner.cobweb.parsing.DataParser;
import de.tischner.cobweb.parsing.ParseException;
import de.tischner.cobweb.parsing.gtfs.IGtfsFileHandler;
import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.tischner.cobweb.parsing.osm.IOsmFilter;
import de.tischner.cobweb.parsing.osm.OsmReducer;
import de.tischner.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric;
import de.tischner.cobweb.routing.algorithms.nearestneighbor.CoverTree;
import de.tischner.cobweb.routing.algorithms.nearestneighbor.INearestNeighborComputation;
import de.tischner.cobweb.routing.algorithms.shortestpath.ShortestPathComputationFactory;
import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.link.LinkGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.tischner.cobweb.routing.model.graph.transit.TransitGraph;
import de.tischner.cobweb.routing.model.graph.transit.TransitStop;
import de.tischner.cobweb.routing.parsing.gtfs.GtfsConnectionBuilder;
import de.tischner.cobweb.routing.parsing.gtfs.GtfsRealisticTimeExpandedHandler;
import de.tischner.cobweb.routing.parsing.gtfs.IGtfsConnectionBuilder;
import de.tischner.cobweb.routing.parsing.osm.IOsmRoadBuilder;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadBuilder;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadFilter;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadHandler;
import de.tischner.cobweb.routing.server.RoutingServer;
import de.tischner.cobweb.searching.name.server.NameSearchServer;
import de.tischner.cobweb.searching.nearest.server.NearestSearchServer;
import de.tischner.cobweb.util.CleanUtil;
import de.tischner.cobweb.util.SerializationUtil;

/**
 * The whole application. Supports various commands, see the documentation of
 * the constructors for detail.<br>
 * <br>
 * Use {@link #initialize()} after creation. The application can then be
 * operated using {@link #start()} and {@link #shutdown()}.<br>
 * <br>
 * The application consists of a routing server which offers a REST API, a
 * database that stores meta information and two search servers used as utility
 * which offer a REST API too.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class Application {
  /**
   * Path to the configuration of the logger.
   */
  private static final Path LOGGER_CONFIG = Paths.get("res", "logging", "logConfig.xml");
  /**
   * Parsed argument data used to determine the commands to use.
   */
  private final CommandData mCommandData;
  /**
   * Factory to use for generating algorithms for shortest path computation.
   */
  private ShortestPathComputationFactory<ICoreNode, ICoreEdge<ICoreNode>> mComputationFactory;
  /**
   * Provides the configuration of the application.
   */
  private ConfigStore mConfig;
  /**
   * Utility used to load and save the configuration.
   */
  private ConfigLoader mConfigLoader;
  /**
   * Database to use for storing meta data.
   */
  private ADatabase mDatabase;
  /**
   * Link graph to route on.
   */
  private LinkGraph mLinkGraph;
  /**
   * Logger to use for logging.
   */
  private Logger mLogger;
  /**
   * Server to use for responding to name search requests. Offers a REST API.
   */
  private NameSearchServer mNameSearchServer;
  /**
   * Algorithm used to compute nearest neighbors.
   */
  private INearestNeighborComputation<ICoreNode> mNearestNeighborComputation;
  /**
   * Server to use for responding to nearest search requests. Offers a REST API.
   */
  private NearestSearchServer mNearestSearchServer;
  /**
   * Road graph to route on.
   */
  private RoadGraph<ICoreNode, ICoreEdge<ICoreNode>> mRoadGraph;
  /**
   * Server to use for responding to routing requests. Offers a REST API.
   */
  private RoutingServer<ICoreNode, ICoreEdge<ICoreNode>, LinkGraph> mRoutingServer;
  /**
   * Transit graph to route on.
   */
  private TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> mTransitGraph;

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
   * <li><b><tt>args[0] = clean</tt></b>: Clears the database and all cached and
   * serialized data.</li>
   * <li><b><tt>args[1+]</tt></b>: Paths to data files that should be used by
   * the commands instead of the files from the directories set in the
   * configuration file.
   * <ul>
   * <li><tt>start</tt>: Uses the given files as data files (OSM, GTFS) instead
   * of the set directories.</li>
   * <li><tt>reduce</tt>: Reduces the given unreduced data files (OSM, GTFS)
   * instead of the unreduced files in the set directories.</li>
   * <li><tt>clean</tt>: Not supported, will ignore paths.</li>
   * </ul>
   * </li>
   * </ul>
   *
   * @param args The arguments that specify which command to use.
   */
  public Application(final String[] args) {
    mCommandData = CommandParser.parseCommands(args);
  }

  /**
   * Initializes the application. Use this method after construction and before
   * using {@link #start()}.
   */
  public void initialize() {
    initializeLogger();
    mLogger.info("Command is: {}", mCommandData.getCommand().getName());
    mLogger.info("Initializing application");
    try {
      final Instant initStartTime = Instant.now();
      mConfig = new ConfigStore();
      mConfigLoader = new ConfigLoader();
      mConfigLoader.loadConfig(mConfig);
      // Save to ensure old configuration files get new default parameter
      mConfigLoader.saveConfig(mConfig);

      if (mCommandData.getCommand() == ECommand.START) {
        initializeApi();
      }
      final Instant initEndTime = Instant.now();
      mLogger.info("Initialization took: {}", Duration.between(initStartTime, initEndTime));
    } catch (final Throwable e) {
      mLogger.error("Error at initialization of application", e);
      throw e;
    }
  }

  /**
   * Shuts the application down. Use this method after {@link #start()} has been
   * called. The application should not be used anymore after this method.
   * Instead create a new one.
   */
  public void shutdown() {
    // TODO Make sure this is always called, register some shutdown hook
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
      switch (mCommandData.getCommand()) {
        case START:
          mRoutingServer.start();
          mNameSearchServer.start();
          mNearestSearchServer.start();
          break;
        case CLEAN:
          CleanUtil.clean(mConfig, mConfig);
          break;
        case REDUCE:
          startReducer();
          break;
        default:
          throw new AssertionError();
      }
    } catch (final Throwable e) {
      mLogger.error("Error while starting application", e);
      throw e;
    }
  }

  /**
   * Completes the initialization of the graph model.
   *
   * @param graphSizeBefore The size of the graph after it was deserialized.
   *                        Used to determine if the current graph has changed
   *                        compared to the serialized version.
   */
  private void completeGraphInitialization(final int graphSizeBefore) {
    if (mConfig.useGraphCache() && mLinkGraph.size() == graphSizeBefore) {
      return;
    }
    mLogger.debug("Initializing hub connections");
    final Instant hubStartTime = Instant.now();

    final Map<ICoreNode, TransitStop<ICoreNode>> hubConnections = new HashMap<>();
    // For each transit stop retrieve the nearest road node
    final RoadNode stopLocationWrapper = new RoadNode(-1, 0, 0);
    for (final TransitStop<ICoreNode> stop : mTransitGraph.getStops()) {
      stopLocationWrapper.setLatitude(stop.getLatitude());
      stopLocationWrapper.setLongitude(stop.getLongitude());
      final ICoreNode hubNode = mNearestNeighborComputation.getNearestNeighbor(stopLocationWrapper).get();
      hubConnections.put(hubNode, stop);
    }
    mLinkGraph.initializeHubConnections(hubConnections);

    final Instant hubEndTime = Instant.now();
    mLogger.info("Hub connections took: {}", Duration.between(hubStartTime, hubEndTime));
  }

  /**
   * Creates file handler that handle GTFS files for routing. If they are
   * notified when parsing GTFS data, they will adjust models used for routing
   * like the graph accordingly.
   *
   * @return An iterable consisting of all routing file handlers that adjust
   *         routing models
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files
   */
  private Iterable<IGtfsFileHandler> createGtfsRoutingHandler() throws ParseException {
    try {
      final IGtfsConnectionBuilder<ICoreNode, ICoreEdge<ICoreNode>> connectionBuilder =
          new GtfsConnectionBuilder(mTransitGraph);
      final IGtfsFileHandler transitHandler =
          new GtfsRealisticTimeExpandedHandler<>(mTransitGraph, connectionBuilder, mConfig);
      return Collections.singletonList(transitHandler);
    } catch (final IOException e) {
      throw new ParseException(e);
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
   * Creates file handler that handle OSM files for routing. If they are
   * notified when parsing OSM data, they will adjust models used for routing
   * like the graph accordingly.
   *
   * @return An iterable consisting of all routing file handlers that adjust
   *         routing models
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files
   */
  private Iterable<IOsmFileHandler> createOsmRoutingHandler() throws ParseException {
    final IOsmRoadBuilder<ICoreNode, ICoreEdge<ICoreNode>> roadBuilder = new OsmRoadBuilder<>(mRoadGraph, mRoadGraph);
    final IOsmFilter roadFilter = new OsmRoadFilter(mConfig);
    try {
      final IOsmFileHandler roadHandler = new OsmRoadHandler<>(mRoadGraph, roadFilter, roadBuilder, mDatabase, mConfig);
      return Collections.singletonList(roadHandler);
    } catch (final IOException e) {
      throw new ParseException(e);
    }
  }

  /**
   * Initializes the nearest neighbor computation algorithm. Depending on the
   * size of the graph this method may take a while.
   */
  private void initalizeNearestNeighborComputation() {
    mLogger.info("Initializing nearest neighbor computation");
    final Instant nearestNeighborsStartTime = Instant.now();

    final CoverTree<ICoreNode> nearestNeighborComputation = new CoverTree<>(new AsTheCrowFliesMetric<>());
    for (final ICoreNode node : mRoadGraph.getNodes()) {
      nearestNeighborComputation.insert(node);
    }
    mNearestNeighborComputation = nearestNeighborComputation;

    final Instant nearestNeighborsEndTime = Instant.now();
    mLogger.info("Nearest neighbors took: {}", Duration.between(nearestNeighborsStartTime, nearestNeighborsEndTime));
  }

  /**
   * Initializes the API of the application. This consists of the routing
   * server, the database, the search server and all corresponding utilities and
   * models.
   *
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files
   */
  private void initializeApi() throws ParseException {
    final Instant initStartTime = Instant.now();

    initializeDatabase();
    initializeGraph();

    final int graphSizeBefore = mLinkGraph.size();

    // Prepare data parsing
    final Collection<Path> paths = mCommandData.getPaths();
    final DataParser dataParser;
    if (paths.isEmpty()) {
      // Use configuration file
      dataParser = new DataParser(mConfig, null);
    } else {
      // Override settings with the given paths
      dataParser = new DataParser(mConfig, paths);
    }

    // Add OSM handler
    createOsmDatabaseHandler().forEach(dataParser::addOsmHandler);
    createOsmRoutingHandler().forEach(dataParser::addOsmHandler);
    // Add GTFS handler
    createGtfsRoutingHandler().forEach(dataParser::addGtfsHandler);

    // Parse all data
    final Instant parseStartTime = Instant.now();
    dataParser.parseData();
    final Instant parseEndTime = Instant.now();
    mLogger.info("Parsing took: {}", Duration.between(parseStartTime, parseEndTime));
    dataParser.clearHandler();

    initalizeNearestNeighborComputation();

    completeGraphInitialization(graphSizeBefore);
    serializeGraphIfDesired(graphSizeBefore);

    mLogger.info("Graph size: {}", mLinkGraph.getSizeInformation());

    initializeRouting();
    initializeNameSearch();
    initializeNearestSearch();

    final Instant initEndTime = Instant.now();
    mLogger.info("Initialization of API took: {}", Duration.between(initStartTime, initEndTime));
  }

  /**
   * Initializes the database which stores meta data used for routing. Depending
   * on the configuration this may create tables in an external database or
   * create an internal in-memory database.
   *
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files. Or when a problem with an
   *                        external database occurred like passing invalid SQL
   *                        or if a connection could not be established.
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
   *                        configuration files or if the graph to deserialize
   *                        is invalid.
   */
  private void initializeGraph() throws ParseException {
    mLogger.info("Initializing graph");

    final Path graphCache = mConfig.getGraphCache();
    if (!mConfig.useGraphCache() || !Files.isRegularFile(graphCache)) {
      mRoadGraph = new RoadGraph<>();
      mTransitGraph = new TransitGraph<>();
      mLinkGraph = new LinkGraph(mRoadGraph, mTransitGraph);
      return;
    }

    // Deserialize graph
    mLogger.info("Deserializing graph from: {}", graphCache);
    final Instant deserializeStartTime = Instant.now();

    final SerializationUtil<LinkGraph> serializationUtil = new SerializationUtil<>();
    try {
      mLinkGraph = serializationUtil.deserialize(graphCache);
    } catch (ClassNotFoundException | ClassCastException | IOException e) {
      throw new ParseException(e);
    }
    mRoadGraph = mLinkGraph.getRoadGraph();
    mTransitGraph = mLinkGraph.getTransitGraph();

    final Instant deserializeEndTime = Instant.now();
    mLogger.info("Deserialization took: {}", Duration.between(deserializeStartTime, deserializeEndTime));
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
   * Initializes the name search server and algorithms used to answer name
   * search requests. Depending on the used algorithms this method may take a
   * while for all precomputations to finish.
   */
  private void initializeNameSearch() {
    mLogger.info("Initializing name search");
    mNameSearchServer = new NameSearchServer(mConfig, mDatabase);
    mNameSearchServer.initialize();
  }

  /**
   * Initializes the nearest search server and algorithms used to answer nearest
   * search requests. Depending on the used algorithms this method may take a
   * while for all precomputations to finish.
   */
  private void initializeNearestSearch() {
    mLogger.info("Initializing nearest search");
    mNearestSearchServer = new NearestSearchServer(mConfig, mNearestNeighborComputation, mDatabase);
    mNearestSearchServer.initialize();
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
    mComputationFactory = new ShortestPathComputationFactory<>(mLinkGraph);
    mComputationFactory.initialize();
    final Instant preCompTimeEnd = Instant.now();
    mLogger.info("Precomputation took: {}", Duration.between(preCompTimeStart, preCompTimeEnd));

    mRoutingServer = new RoutingServer<>(mConfig, mLinkGraph, mComputationFactory, mDatabase);
    mRoutingServer.initialize();
  }

  /**
   * Serializes the graph model if desired. That is, if the size of the graph
   * has changed and the configuration contains the property to use the graph
   * cache.
   *
   * @param graphSizeBefore The size of the graph after it was deserialized.
   *                        Used to determine if the current graph has changed
   *                        compared to the serialized version.
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files or if an exception at
   *                        serialization occurred
   */
  private void serializeGraphIfDesired(final int graphSizeBefore) throws ParseException {
    if (!mConfig.useGraphCache() || mLinkGraph.size() == graphSizeBefore) {
      return;
    }

    final Path graphCache = mConfig.getGraphCache();
    mLogger.info("Serializing graph to: {}", graphCache);
    final Instant serializeStartTime = Instant.now();

    final SerializationUtil<LinkGraph> serializationUtil = new SerializationUtil<>();
    try {
      serializationUtil.serialize(mLinkGraph, graphCache);
    } catch (final IOException e) {
      throw new ParseException(e);
    }

    final Instant serializeEndTime = Instant.now();
    mLogger.info("Serialization took: {}", Duration.between(serializeStartTime, serializeEndTime));
  }

  /**
   * Starts the reducer command.
   */
  private void startReducer() {
    // Prepare data parsing
    final Collection<Path> paths = mCommandData.getPaths();
    final DataParser dataParser;
    if (paths.isEmpty()) {
      // Use configuration file
      dataParser = new DataParser(mConfig, null, true);
    } else {
      // Override settings with the given paths
      dataParser = new DataParser(mConfig, paths, true);
    }

    // Add OSM handler
    dataParser.addOsmHandler(new OsmReducer(new OsmRoadFilter(mConfig)));

    // Parse all data
    final Instant parseStartTime = Instant.now();
    dataParser.parseData();
    final Instant parseEndTime = Instant.now();
    mLogger.info("Parsing took: {}", Duration.between(parseStartTime, parseEndTime));
  }

}
