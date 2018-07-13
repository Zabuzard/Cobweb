package de.tischner.cobweb.routing.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.IRoutingConfigProvider;
import de.tischner.cobweb.db.IRoutingDatabase;
import de.tischner.cobweb.parsing.ParseException;
import de.tischner.cobweb.parsing.gtfs.IGtfsFileHandler;
import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.tischner.cobweb.parsing.osm.IOsmFilter;
import de.tischner.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric;
import de.tischner.cobweb.routing.algorithms.nearestneighbor.CoverTree;
import de.tischner.cobweb.routing.algorithms.nearestneighbor.INearestNeighborComputation;
import de.tischner.cobweb.routing.algorithms.shortestpath.ShortestPathComputationFactory;
import de.tischner.cobweb.routing.algorithms.shortestpath.hybridmodel.ITranslationWithTime;
import de.tischner.cobweb.routing.algorithms.shortestpath.hybridmodel.RoadToNearestQueryTransitTranslation;
import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.IGetNodeById;
import de.tischner.cobweb.routing.model.graph.link.LinkGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.tischner.cobweb.routing.model.graph.transit.TransitGraph;
import de.tischner.cobweb.routing.model.graph.transit.TransitStop;
import de.tischner.cobweb.routing.model.timetable.Timetable;
import de.tischner.cobweb.routing.parsing.gtfs.GtfsConnectionBuilder;
import de.tischner.cobweb.routing.parsing.gtfs.GtfsRealisticTimeExpandedHandler;
import de.tischner.cobweb.routing.parsing.gtfs.GtfsTimetableHandler;
import de.tischner.cobweb.routing.parsing.gtfs.IGtfsConnectionBuilder;
import de.tischner.cobweb.routing.parsing.osm.IOsmRoadBuilder;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadBuilder;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadFilter;
import de.tischner.cobweb.routing.parsing.osm.OsmRoadHandler;
import de.tischner.cobweb.util.SerializationUtil;

/**
 * The routing model to use for routing. The model and algorithms can be changed
 * by using modes.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RoutingModel {
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingModel.class);
  /**
   * The routing configuration provider.
   */
  private final IRoutingConfigProvider mConfig;
  /**
   * The database for routing.
   */
  private final IRoutingDatabase mDatabase;
  /**
   * The size of the graph before data was read, i.e. after deserializing but
   * before reading in new data. May refer to a different graph depending on the
   * mode.
   */
  private int mGraphSizeBeforeData;
  /**
   * Link graph to route on or <tt>null</tt> if not used according to the mode.
   */
  private LinkGraph mLinkGraph;
  /**
   * The routing model mode to use.
   */
  private final ERoutingModelMode mMode;
  /**
   * The algorithm to use for computing nearest road nodes.
   */
  private INearestNeighborComputation<ICoreNode> mNearestRoadNodeComputation;
  /**
   * Road graph to route on.
   */
  private RoadGraph<ICoreNode, ICoreEdge<ICoreNode>> mRoadGraph;
  /**
   * The timetable to route on or <tt>null</tt> if not used according to the
   * mode.
   */
  private Timetable mTimetable;
  /**
   * Transit graph to route on or <tt>null</tt> if not used according to the
   * mode.
   */
  private TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> mTransitGraph;

  /**
   * Creates a new routing model.
   *
   * @param database The database to use
   * @param config   The configuration to use
   */
  public RoutingModel(final IRoutingDatabase database, final IRoutingConfigProvider config) {
    mDatabase = database;
    mConfig = config;
    mMode = config.getRoutingModelMode();
  }

  /**
   * Creates file handler that handle GTFS files for routing. If they are
   * notified when parsing GTFS data, they will adjust models used for routing
   * like a graph or timetable accordingly.
   *
   * @return An iterable consisting of all routing file handlers that adjust
   *         routing models
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files
   */
  public Iterable<IGtfsFileHandler> createGtfsHandler() throws ParseException {
    switch (mMode) {
      case GRAPH_WITH_TIMETABLE:
        final IGtfsFileHandler timetableHandler = new GtfsTimetableHandler(mTimetable, mTimetable);
        return Collections.singletonList(timetableHandler);
      case LINK_GRAPH:
        final IGtfsConnectionBuilder<ICoreNode, ICoreEdge<ICoreNode>> connectionBuilder =
            new GtfsConnectionBuilder(mTransitGraph);
        try {
          final IGtfsFileHandler transitHandler =
              new GtfsRealisticTimeExpandedHandler<>(mTransitGraph, connectionBuilder, mConfig);
          return Collections.singletonList(transitHandler);
        } catch (final IOException e) {
          throw new ParseException(e);
        }
      default:
        throw new AssertionError();
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
  public Iterable<IOsmFileHandler> createOsmHandler() throws ParseException {
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
   * Creates a factory used for creating shortest path computation algorithm.
   * The exact factory depends on the routing model mode.
   *
   * @return The constructed factory
   */
  public ShortestPathComputationFactory createShortestPathComputationFactory() {
    final Instant preCompTimeStart = Instant.now();
    final ShortestPathComputationFactory factory;
    switch (mMode) {
      case GRAPH_WITH_TIMETABLE:
        // TODO Exchange with a translation that is based on access nodes or a
        // perimeter
        final ITranslationWithTime<ICoreNode, ICoreNode> roadToTransitTranslation =
            new RoadToNearestQueryTransitTranslation(mTimetable);
        factory = new ShortestPathComputationFactory(mRoadGraph, mTimetable, roadToTransitTranslation, mMode);
        break;
      case LINK_GRAPH:
        factory = new ShortestPathComputationFactory(mLinkGraph, null, null, mMode);
        break;
      default:
        throw new AssertionError();
    }
    factory.initialize();

    final Instant preCompTimeEnd = Instant.now();
    LOGGER.info("Precomputation took: {}", Duration.between(preCompTimeStart, preCompTimeEnd));

    return factory;
  }

  /**
   * Finishes the preparation of the model. This may serialize the model.
   *
   * @throws ParseException If an exception occurred while parsing data like
   *                        configuration files or if an exception at
   *                        serialization occurred
   */
  public void finishModel() throws ParseException {
    final int currentGraphSize;
    switch (mMode) {
      case GRAPH_WITH_TIMETABLE:
        currentGraphSize = mRoadGraph.size();
        break;
      case LINK_GRAPH:
        currentGraphSize = mLinkGraph.size();
        break;
      default:
        throw new AssertionError();
    }
    if (!mConfig.useGraphCache() || currentGraphSize == mGraphSizeBeforeData) {
      return;
    }

    final Path graphCache = mConfig.getGraphCache();
    LOGGER.info("Serializing model to: {}", graphCache);
    final Instant serializeStartTime = Instant.now();

    try {
      switch (mMode) {
        case GRAPH_WITH_TIMETABLE:
          final SerializationUtil<RoadGraph<ICoreNode, ICoreEdge<ICoreNode>>> serializationUtilRoad =
              new SerializationUtil<>();
          serializationUtilRoad.serialize(mRoadGraph, graphCache);
          break;
        case LINK_GRAPH:
          final SerializationUtil<LinkGraph> serializationUtilLink = new SerializationUtil<>();
          serializationUtilLink.serialize(mLinkGraph, graphCache);
          break;
        default:
          throw new AssertionError();
      }
    } catch (final IOException e) {
      throw new ParseException(e);
    }

    final Instant serializeEndTime = Instant.now();
    LOGGER.info("Serialization took: {}", Duration.between(serializeStartTime, serializeEndTime));
  }

  /**
   * Gets the algorithm to use for nearest road node computation.
   *
   * @return The algorithm to use for nearest road node computation
   */
  public INearestNeighborComputation<ICoreNode> getNearestRoadNodeComputation() {
    return mNearestRoadNodeComputation;
  }

  /**
   * Gets a node provider that is able to get nodes by their ID.
   *
   * @return A node provider
   */
  public IGetNodeById<ICoreNode> getNodeProvider() {
    switch (mMode) {
      case GRAPH_WITH_TIMETABLE:
        return mRoadGraph;
      case LINK_GRAPH:
        return mLinkGraph;
      default:
        throw new AssertionError();
    }
  }

  /**
   * Gets information about the size of the routing model.
   *
   * @return A human readable information of the model size
   */
  public String getSizeInformation() {
    return toString();
  }

  /**
   * Prepares the model after reading in new data. Should be called after
   * {@link #prepareModelBeforeData()} and before {@link #finishModel()}.
   */
  public void prepareModelAfterData() {
    initializeNearestRoadNodeComputation();
    switch (mMode) {
      case GRAPH_WITH_TIMETABLE:
        // TODO Link road graph to timetable
        break;
      case LINK_GRAPH:
        linkGraphs();
        break;
      default:
        throw new AssertionError();
    }
  }

  /**
   * Prepares the model before reading in new data. Should be called before
   * {@link #prepareModelAfterData()} and before {@link #finishModel()}.<br>
   * <br>
   * This may deserialize the model which might take a while depending on the
   * size of the model.
   */
  public void prepareModelBeforeData() {
    LOGGER.info("Initializing model");

    if (mMode == ERoutingModelMode.GRAPH_WITH_TIMETABLE) {
      // TODO Timetable may be cached too
      mTimetable = new Timetable();
    }

    final Path graphCache = mConfig.getGraphCache();
    if (!mConfig.useGraphCache() || !Files.isRegularFile(graphCache)) {
      mRoadGraph = new RoadGraph<>();
      if (mMode == ERoutingModelMode.LINK_GRAPH) {
        mTransitGraph = new TransitGraph<>();
        mLinkGraph = new LinkGraph(mRoadGraph, mTransitGraph);
      }
      return;
    }

    // Deserialize model
    LOGGER.info("Deserializing model from: {}", graphCache);
    final Instant deserializeStartTime = Instant.now();
    try {
      switch (mMode) {
        case GRAPH_WITH_TIMETABLE:
          final SerializationUtil<RoadGraph<ICoreNode, ICoreEdge<ICoreNode>>> serializationUtilRoad =
              new SerializationUtil<>();
          mRoadGraph = serializationUtilRoad.deserialize(graphCache);
          break;
        case LINK_GRAPH:
          final SerializationUtil<LinkGraph> serializationUtilLink = new SerializationUtil<>();
          mLinkGraph = serializationUtilLink.deserialize(graphCache);
          break;
        default:
          throw new AssertionError();
      }
    } catch (ClassNotFoundException | ClassCastException | IOException e) {
      throw new ParseException(e);
    }

    if (mMode == ERoutingModelMode.LINK_GRAPH) {
      mRoadGraph = mLinkGraph.getRoadGraph();
      mTransitGraph = mLinkGraph.getTransitGraph();
    }

    switch (mMode) {
      case GRAPH_WITH_TIMETABLE:
        mGraphSizeBeforeData = mRoadGraph.size();
        break;
      case LINK_GRAPH:
        mGraphSizeBeforeData = mLinkGraph.size();
        break;
      default:
        throw new AssertionError();
    }

    final Instant deserializeEndTime = Instant.now();
    LOGGER.info("Deserialization took: {}", Duration.between(deserializeStartTime, deserializeEndTime));
  }

  @Override
  public String toString() {
    switch (mMode) {
      case GRAPH_WITH_TIMETABLE:
        return mRoadGraph.getSizeInformation() + ", " + mTimetable.getSizeInformation();
      case LINK_GRAPH:
        return mLinkGraph.getSizeInformation();
      default:
        throw new AssertionError();
    }
  }

  /**
   * Initializes the nearest road node computation.
   */
  private void initializeNearestRoadNodeComputation() {
    LOGGER.info("Initializing nearest road node computation");
    final Instant nearestNeighborsStartTime = Instant.now();

    final CoverTree<ICoreNode> nearestRoadNodeComputation = new CoverTree<>(new AsTheCrowFliesMetric<>());
    for (final ICoreNode node : mRoadGraph.getNodes()) {
      nearestRoadNodeComputation.insert(node);
    }

    mNearestRoadNodeComputation = nearestRoadNodeComputation;

    final Instant nearestNeighborsEndTime = Instant.now();
    LOGGER.info("Nearest road node took: {}", Duration.between(nearestNeighborsStartTime, nearestNeighborsEndTime));
  }

  /**
   * Links the road and transit graph together. Must only be called if the
   * routing model mode is {@link ERoutingModelMode#LINK_GRAPH}.
   */
  private void linkGraphs() {
    if (mConfig.useGraphCache() && mLinkGraph.size() == mGraphSizeBeforeData) {
      return;
    }
    LOGGER.debug("Initializing hub connections");
    final Instant hubStartTime = Instant.now();

    final Map<ICoreNode, TransitStop<ICoreNode>> hubConnections = new HashMap<>();
    // For each transit stop retrieve the nearest road node
    final RoadNode stopLocationWrapper = new RoadNode(-1, 0, 0);
    for (final TransitStop<ICoreNode> stop : mTransitGraph.getStops()) {
      stopLocationWrapper.setLatitude(stop.getLatitude());
      stopLocationWrapper.setLongitude(stop.getLongitude());
      final ICoreNode hubNode = mNearestRoadNodeComputation.getNearestNeighbor(stopLocationWrapper).get();
      hubConnections.put(hubNode, stop);
    }
    mLinkGraph.initializeHubConnections(hubConnections);

    final Instant hubEndTime = Instant.now();
    LOGGER.info("Hub connections took: {}", Duration.between(hubStartTime, hubEndTime));
  }
}
