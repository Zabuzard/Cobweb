package de.unifreiburg.informatik.cobweb;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.util.ContextInitializer;
import de.unifreiburg.informatik.cobweb.commands.CommandData;
import de.unifreiburg.informatik.cobweb.commands.CommandParser;
import de.unifreiburg.informatik.cobweb.commands.ECommand;
import de.unifreiburg.informatik.cobweb.config.ConfigLoader;
import de.unifreiburg.informatik.cobweb.config.ConfigStore;
import de.unifreiburg.informatik.cobweb.db.ADatabase;
import de.unifreiburg.informatik.cobweb.db.ExternalDatabase;
import de.unifreiburg.informatik.cobweb.db.MemoryDatabase;
import de.unifreiburg.informatik.cobweb.db.OsmDatabaseHandler;
import de.unifreiburg.informatik.cobweb.parsing.DataParser;
import de.unifreiburg.informatik.cobweb.parsing.ParseException;
import de.unifreiburg.informatik.cobweb.parsing.osm.IOsmFileHandler;
import de.unifreiburg.informatik.cobweb.parsing.osm.OsmReducer;
import de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.INearestNeighborComputation;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.ShortestPathComputationFactory;
import de.unifreiburg.informatik.cobweb.routing.model.RoutingModel;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IGetNodeById;
import de.unifreiburg.informatik.cobweb.routing.parsing.osm.OsmRoadFilter;
import de.unifreiburg.informatik.cobweb.routing.server.RoutingServer;
import de.unifreiburg.informatik.cobweb.searching.name.server.NameSearchServer;
import de.unifreiburg.informatik.cobweb.searching.nearest.server.NearestSearchServer;
import de.unifreiburg.informatik.cobweb.util.CleanUtil;

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
   * The model to use for routing.
   */
  private RoutingModel mRoutingModel;
  /**
   * Server to use for responding to routing requests. Offers a REST API.
   */
  private RoutingServer mRoutingServer;
  /**
   * Whether the application was requested to shutdown.
   */
  private volatile boolean mWasShutdownRequested;

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
    mWasShutdownRequested = false;
    mCommandData = CommandParser.parseCommands(args);
  }

  /**
   * Initializes the application. Use this method after construction and before
   * using {@link #start()}.
   */
  public void initialize() {
    initializeLogger();

    // Add shutdown hook for a controlled shutdown when killed
    Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));

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
  public synchronized void shutdown() {
    if (mWasShutdownRequested) {
      return;
    }
    mWasShutdownRequested = true;
    mLogger.info("Shutting down application");
    try {
      mRoutingServer.shutdown();
      mDatabase.shutdown();
    } catch (final Throwable e) {
      mLogger.error("Error while shutting down application", e);
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
   * Returns whether the application was requested to shutdown.
   *
   * @return <tt>True</tt> if the application was requested to shutdown,
   *         <tt>false</tt> otherwise
   */
  public synchronized boolean wasShutdownRequested() {
    return mWasShutdownRequested;
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
    mRoutingModel = new RoutingModel(mDatabase, mConfig);
    mRoutingModel.prepareModelBeforeData();

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
    mRoutingModel.createOsmHandler().forEach(dataParser::addOsmHandler);
    // Add GTFS handler
    mRoutingModel.createGtfsHandler().forEach(dataParser::addGtfsHandler);

    // Parse all data
    final Instant parseStartTime = Instant.now();
    dataParser.parseData();
    final Instant parseEndTime = Instant.now();
    mLogger.info("Parsing took: {}", Duration.between(parseStartTime, parseEndTime));
    dataParser.clearHandler();

    mRoutingModel.prepareModelAfterData();
    mNearestNeighborComputation = mRoutingModel.getNearestRoadNodeComputation();

    mRoutingModel.finishModel();
    mLogger.info("Model size: {}", mRoutingModel.getSizeInformation());

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

    final IGetNodeById<ICoreNode> nodeProvider = mRoutingModel.getNodeProvider();
    final ShortestPathComputationFactory computationFactory = mRoutingModel.createShortestPathComputationFactory();

    mRoutingServer = new RoutingServer(mConfig, nodeProvider, computationFactory, mDatabase);
    mRoutingServer.initialize();
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
