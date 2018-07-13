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

public final class RoutingModel {
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingModel.class);
  private final IRoutingConfigProvider mConfig;
  private final IRoutingDatabase mDatabase;
  private int mGraphSizeBeforeData;
  /**
   * Link graph to route on.
   */
  private LinkGraph mLinkGraph;
  private final ERoutingModelMode mMode;
  private INearestNeighborComputation<ICoreNode> mNearestNeighborComputation;
  /**
   * Road graph to route on.
   */
  private RoadGraph<ICoreNode, ICoreEdge<ICoreNode>> mRoadGraph;
  private Timetable mTimetable;
  /**
   * Transit graph to route on.
   */
  private TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> mTransitGraph;

  public RoutingModel(final IRoutingDatabase database, final IRoutingConfigProvider config) {
    mDatabase = database;
    mConfig = config;
    mMode = config.getRoutingModelMode();
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
  public Iterable<IGtfsFileHandler> createGtfsHandler() throws ParseException {
    switch (mMode) {
      case CONNECTION_SCAN:
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

  public ShortestPathComputationFactory createShortestPathComputationFactory() {
    final Instant preCompTimeStart = Instant.now();
    final ShortestPathComputationFactory factory;
    switch (mMode) {
      case CONNECTION_SCAN:
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
      case CONNECTION_SCAN:
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
        case CONNECTION_SCAN:
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

  public INearestNeighborComputation<ICoreNode> getNearestNeighborComputation() {
    return mNearestNeighborComputation;
  }

  public IGetNodeById<ICoreNode> getNodeProvider() {
    switch (mMode) {
      case CONNECTION_SCAN:
        return mRoadGraph;
      case LINK_GRAPH:
        return mLinkGraph;
      default:
        throw new AssertionError();
    }
  }

  public String getSizeInformation() {
    switch (mMode) {
      case CONNECTION_SCAN:
        return mRoadGraph.getSizeInformation() + ", " + mTimetable.getSizeInformation();
      case LINK_GRAPH:
        return mLinkGraph.getSizeInformation();
      default:
        throw new AssertionError();
    }
  }

  public void prepareModelAfterData() {
    initializeNearestNeighborComputation();
    switch (mMode) {
      case CONNECTION_SCAN:
        // TODO Link road graph to timetable
        break;
      case LINK_GRAPH:
        linkGraphs();
        break;
      default:
        throw new AssertionError();
    }
  }

  public void prepareModelBeforeData() {
    LOGGER.info("Initializing model");

    if (mMode == ERoutingModelMode.CONNECTION_SCAN) {
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
        case CONNECTION_SCAN:
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
      case CONNECTION_SCAN:
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

  private void initializeNearestNeighborComputation() {
    LOGGER.info("Initializing nearest neighbor computation");
    final Instant nearestNeighborsStartTime = Instant.now();

    final CoverTree<ICoreNode> nearestNeighborComputation = new CoverTree<>(new AsTheCrowFliesMetric<>());
    for (final ICoreNode node : mRoadGraph.getNodes()) {
      nearestNeighborComputation.insert(node);
    }

    mNearestNeighborComputation = nearestNeighborComputation;

    final Instant nearestNeighborsEndTime = Instant.now();
    LOGGER.info("Nearest neighbors took: {}", Duration.between(nearestNeighborsStartTime, nearestNeighborsEndTime));
  }

  /**
   * Links the road and transit graph together.
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
      final ICoreNode hubNode = mNearestNeighborComputation.getNearestNeighbor(stopLocationWrapper).get();
      hubConnections.put(hubNode, stop);
    }
    mLinkGraph.initializeHubConnections(hubConnections);

    final Instant hubEndTime = Instant.now();
    LOGGER.info("Hub connections took: {}", Duration.between(hubStartTime, hubEndTime));
  }
}
