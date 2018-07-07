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
import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.IGetNodeById;
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
  private INearestNeighborComputation<ICoreNode> mNearestNeighborComputation;
  /**
   * Road graph to route on.
   */
  private RoadGraph<ICoreNode, ICoreEdge<ICoreNode>> mRoadGraph;
  /**
   * Transit graph to route on.
   */
  private TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> mTransitGraph;

  public RoutingModel(final IRoutingDatabase database, final IRoutingConfigProvider config) {
    mDatabase = database;
    mConfig = config;
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

  public ShortestPathComputationFactory<ICoreNode, ICoreEdge<ICoreNode>> createShortestPathComputationFactory() {
    final Instant preCompTimeStart = Instant.now();

    final ShortestPathComputationFactory<ICoreNode, ICoreEdge<ICoreNode>> factory =
        new ShortestPathComputationFactory<>(mLinkGraph);
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
    if (!mConfig.useGraphCache() || mLinkGraph.size() == mGraphSizeBeforeData) {
      return;
    }

    final Path graphCache = mConfig.getGraphCache();
    LOGGER.info("Serializing model to: {}", graphCache);
    final Instant serializeStartTime = Instant.now();

    final SerializationUtil<LinkGraph> serializationUtil = new SerializationUtil<>();
    try {
      serializationUtil.serialize(mLinkGraph, graphCache);
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
    return mLinkGraph;
  }

  public String getSizeInformation() {
    return mLinkGraph.getSizeInformation();
  }

  public void prepareModelAfterData() {
    initializeNearestNeighborComputation();
    linkGraphs();
  }

  public void prepareModelBeforeData() {
    LOGGER.info("Initializing model");

    final Path graphCache = mConfig.getGraphCache();
    if (!mConfig.useGraphCache() || !Files.isRegularFile(graphCache)) {
      mRoadGraph = new RoadGraph<>();
      mTransitGraph = new TransitGraph<>();
      mLinkGraph = new LinkGraph(mRoadGraph, mTransitGraph);
      return;
    }

    // Deserialize model
    LOGGER.info("Deserializing model from: {}", graphCache);
    final Instant deserializeStartTime = Instant.now();

    final SerializationUtil<LinkGraph> serializationUtil = new SerializationUtil<>();
    try {
      mLinkGraph = serializationUtil.deserialize(graphCache);
    } catch (ClassNotFoundException | ClassCastException | IOException e) {
      throw new ParseException(e);
    }
    mRoadGraph = mLinkGraph.getRoadGraph();
    mTransitGraph = mLinkGraph.getTransitGraph();

    mGraphSizeBeforeData = mLinkGraph.size();

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
