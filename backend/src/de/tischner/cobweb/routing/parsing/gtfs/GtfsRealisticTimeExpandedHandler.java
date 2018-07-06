package de.tischner.cobweb.routing.parsing.gtfs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.FareAttribute;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.Frequency;
import org.onebusaway.gtfs.model.Pathway;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.IRoutingConfigProvider;
import de.tischner.cobweb.parsing.RecentHandler;
import de.tischner.cobweb.parsing.gtfs.GtfsEntityForwarder;
import de.tischner.cobweb.parsing.gtfs.IGtfsFileHandler;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGetNodeById;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.ISpatial;
import de.tischner.cobweb.routing.model.graph.transit.IHasTransitStops;
import de.tischner.cobweb.routing.model.graph.transit.NodeTime;
import de.tischner.cobweb.routing.model.graph.transit.TransitStop;
import de.tischner.cobweb.util.collections.CollectionUtil;

/**
 * Implementation of an {@link IGtfsFileHandler} which constructs a realistic
 * time expanded transit graph that consists of transit nodes and edges out of
 * the given GTFS data.<br>
 * <br>
 * The graph can be cached, then the handler will only parse files that provide
 * data the graph does not already contain. The node and edge instances itself
 * are created using a given builder.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 * @param <G> Type of the graph
 */
public final class GtfsRealisticTimeExpandedHandler<N extends INode & IHasId & ISpatial, E extends IEdge<N>,
    G extends IGraph<N, E> & IGetNodeById<N> & IHasTransitStops<N>> extends GtfsEntityForwarder
    implements IGtfsFileHandler {
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(GtfsRealisticTimeExpandedHandler.class);
  /**
   * Transfer time in seconds.
   */
  private static final int TRANSFER_DELAY = 5 * 60;

  /**
   * Builder to use for constructing edges and nodes that are to be inserted
   * into the graph.
   */
  private final IGtfsConnectionBuilder<N, E> mBuilder;
  /**
   * The graph to insert parsed nodes and edges into.
   */
  private final G mGraph;
  /**
   * The handler to use which determines the GTFS files that contain more recent
   * or new data than the data already stored in the graph. Will only be used if
   * the configuration has set the use of a graph cache.
   */
  private final RecentHandler mRecentHandler;
  /**
   * Map connecting stop IDs to arrival nodes.
   */
  private final MutableMap<AgencyAndId, List<NodeTime<N>>> mStopToArrNodes;
  /**
   * Map connecting stop IDs to departure nodes.
   */
  private final MutableMap<AgencyAndId, List<NodeTime<N>>> mStopToDepNodes;
  /**
   * Map connecting stop IDs to transfer nodes.
   */
  private final MutableMap<AgencyAndId, List<NodeTime<N>>> mStopToTransferNodes;
  /**
   * Map connecting trip IDs to stop nodes in the sequence of the trip.
   */
  private final MutableMap<AgencyAndId, List<TripStopNodes<N>>> mTripToSequence;

  /**
   * Whether or not a graph cache is to be used. This determines if GTFS files
   * should be filtered by a {@link RecentHandler} or not.
   */
  private final boolean mUseGraphCache;

  /**
   * Creates a new GTFS realistic time expanded handler which operates on the
   * given graph using the given configuration.<br>
   * <br>
   * The builder will be used to construct the nodes and edges that are to be
   * inserted into the graph.
   *
   * @param graph   The graph to insert nodes and edges into
   * @param builder Builder to use for constructing edges and nodes that are to
   *                be inserted into the graph.
   * @param config  Configuration provider which provides graph cache
   *                information
   * @throws IOException If an I/O exception occurred while reading the graph
   *                     cache information
   */
  public GtfsRealisticTimeExpandedHandler(final G graph, final IGtfsConnectionBuilder<N, E> builder,
      final IRoutingConfigProvider config) throws IOException {
    // TODO Improve amount of GTFS data actually used for transit graph creation
    mGraph = graph;
    mBuilder = builder;
    mTripToSequence = Maps.mutable.empty();
    mStopToTransferNodes = Maps.mutable.empty();
    mStopToDepNodes = Maps.mutable.empty();
    mStopToArrNodes = Maps.mutable.empty();

    mUseGraphCache = config.useGraphCache();
    if (mUseGraphCache) {
      mRecentHandler = new RecentHandler(config.getGraphCacheInfo());
    } else {
      mRecentHandler = null;
    }
  }

  @Override
  public void complete() throws IOException {
    // Process the sequences and connect departure to next arrival nodes
    mTripToSequence.forEachValue(sequence -> {
      final Iterator<TripStopNodes<N>> sequenceIter = sequence.iterator();
      // Some faulty feeds do not start with a fixed sequence index. In that
      // case we need to skip until we found a fully connected sequence.
      TripStopNodes<N> tripStopNodes = null;
      while (tripStopNodes == null) {
        tripStopNodes = sequenceIter.next();
      }
      N lastDepNode = tripStopNodes.getDepNode();
      int lastDepTime = tripStopNodes.getDepTime();

      while (sequenceIter.hasNext()) {
        tripStopNodes = sequenceIter.next();
        // Connect last departure to current arrival node
        final N arrNode = tripStopNodes.getArrNode();
        final int arrTime = tripStopNodes.getArrTime();
        final E depToNextArrEdge = mBuilder.buildEdge(lastDepNode, arrNode, arrTime - lastDepTime);
        mGraph.addEdge(depToNextArrEdge);

        // Prepare next round
        lastDepNode = tripStopNodes.getDepNode();
        lastDepTime = tripStopNodes.getDepTime();
      }
    });

    // Process the transfer nodes and connect them to each other
    mStopToTransferNodes.forEachValue(transferNodes -> {
      // Sort the transfer nodes ascending in time
      transferNodes.sort(Comparators.naturalOrder());

      // Connect them in that order
      final Iterator<NodeTime<N>> transferNodeTimeIter = transferNodes.iterator();
      NodeTime<N> transferNodeTime = transferNodeTimeIter.next();
      N lastTransferNode = transferNodeTime.getNode();
      int lastTransferTime = transferNodeTime.getTime();

      while (transferNodeTimeIter.hasNext()) {
        transferNodeTime = transferNodeTimeIter.next();
        final N currentTransferNode = transferNodeTime.getNode();
        final int currentTransferTime = transferNodeTime.getTime();
        final E transferConnectionEdge =
            mBuilder.buildEdge(lastTransferNode, currentTransferNode, currentTransferTime - lastTransferTime);
        mGraph.addEdge(transferConnectionEdge);

        // Prepare next round
        lastTransferNode = transferNodeTime.getNode();
        lastTransferTime = transferNodeTime.getTime();
      }
    });

    // Process the departure nodes and connect them to their previous transfer
    // node
    final NodeTime<N> nodeTimeNeedle = new NodeTime<>(null, 0);
    mStopToDepNodes.forEachKeyValue((stopId, depNodes) -> {
      final List<NodeTime<N>> transferNodes = mStopToTransferNodes.get(stopId);
      depNodes.forEach(depNode -> {
        // Retrieve the previous transfer node
        // Note that this requires that the transfer nodes were sorted
        // before
        final int depTime = depNode.getTime();
        nodeTimeNeedle.setTime(depTime);
        final int indexOfPrevious = -1 * Collections.binarySearch(transferNodes, nodeTimeNeedle) - 2;
        // Check if there is a previous transfer node
        if (indexOfPrevious < 0) {
          return;
        }
        final NodeTime<N> transferNode = transferNodes.get(indexOfPrevious);

        // Connect the transfer to the departure node
        final E transferToDepEdge =
            mBuilder.buildEdge(transferNode.getNode(), depNode.getNode(), depTime - transferNode.getTime());
        mGraph.addEdge(transferToDepEdge);
      });
    });

    // Process the arrival nodes and pass them to the graph
    mStopToArrNodes.forEachValue(arrivalNodes -> {
      Collections.sort(arrivalNodes);
      final N anyNode = arrivalNodes.stream().findAny().get().getNode();
      final TransitStop<N> stop = new TransitStop<>(arrivalNodes, anyNode.getLatitude(), anyNode.getLongitude());
      mGraph.addStop(stop);
    });

    // Prepare for possible next round
    mStopToArrNodes.clear();
    mStopToDepNodes.clear();
    mStopToTransferNodes.clear();
    mTripToSequence.clear();

    // Update cache information
    if (mUseGraphCache) {
      mRecentHandler.updateInfo();
    }
  }

  @Override
  public void handle(final Agency agency) {
    // TODO Agency ID must be pushed to the database to fully identify internal
    // transit node IDs
  }

  @Override
  public void handle(final FareAttribute fareAttribute) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final FareRule fareRule) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final FeedInfo feedInfo) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final Frequency frequency) {
    // TODO Needed, specifies how trips are repeated and thus generates the
    // final list of trips
  }

  @Override
  public void handle(final Pathway pathway) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final Route route) {
    // TODO Implement
  }

  @Override
  public void handle(final ServiceCalendar serviceCalendar) {
    // TODO Implement
  }

  @Override
  public void handle(final ServiceCalendarDate serviceCalendarDate) {
    // TODO Implement
  }

  @Override
  public void handle(final ShapePoint shapePoint) {
    // TODO Push to database. Trips specify a shapeID and several ShapePoints
    // form a Shape with that ID. The database should hold all coordinates for a
    // ShapeID. It is used at decoration of the final path.
  }

  @Override
  public void handle(final Stop stop) {
    // TODO Implement
  }

  @Override
  public void handle(final StopTime stopTime) {
    final Stop stop = stopTime.getStop();
    final AgencyAndId stopId = stop.getId();
    final AgencyAndId tripId = stopTime.getTrip().getId();

    final int arrTime = stopTime.getArrivalTime();
    final int depTime = stopTime.getDepartureTime();
    final int transferTime = arrTime + TRANSFER_DELAY;

    // Build nodes
    final N arrNode = mBuilder.buildNode((float) stop.getLat(), (float) stop.getLon(), arrTime);
    final N depNode = mBuilder.buildNode((float) stop.getLat(), (float) stop.getLon(), depTime);
    final N transferNode = mBuilder.buildNode((float) stop.getLat(), (float) stop.getLon(), transferTime);
    mGraph.addNode(arrNode);
    mGraph.addNode(depNode);
    mGraph.addNode(transferNode);

    // Connect arrival with departure and arrival with transfer
    final E arrToDepEdge = mBuilder.buildEdge(arrNode, depNode, depTime - arrTime);
    final E arrToTransferEdge = mBuilder.buildEdge(arrNode, transferNode, TRANSFER_DELAY);
    mGraph.addEdge(arrToDepEdge);
    mGraph.addEdge(arrToTransferEdge);

    // Remember the sequence to later connect departure with next arrival
    final int sequenceIndex = stopTime.getStopSequence() - 1;
    final TripStopNodes<N> tripStopNodes = new TripStopNodes<>(arrNode, depNode, arrTime, depTime);

    final List<TripStopNodes<N>> sequence = mTripToSequence.getIfAbsentPut(tripId, FastList::new);
    if (sequence.size() <= sequenceIndex) {
      // Fill with null values until the index is available
      CollectionUtil.increaseCapacity(sequence, sequenceIndex + 1);
    }
    sequence.set(sequenceIndex, tripStopNodes);

    // Remember transfer node sequence per stop to connect them later
    final List<NodeTime<N>> transferNodes = mStopToTransferNodes.getIfAbsentPut(stopId, FastList::new);
    transferNodes.add(new NodeTime<>(transferNode, transferTime));

    // Remember departure nodes per stop to connect them later to their previous
    // transfer node
    final List<NodeTime<N>> departureNodes = mStopToDepNodes.getIfAbsentPut(stopId, FastList::new);
    departureNodes.add(new NodeTime<>(depNode, depTime));

    // Remember arrival nodes per stop to pass them later to the graph for
    // retrieval of the correct node corresponding to a query time
    final List<NodeTime<N>> arrivalNodes = mStopToArrNodes.getIfAbsentPut(stopId, FastList::new);
    arrivalNodes.add(new NodeTime<>(arrNode, arrTime));

    // TODO Implement
  }

  @Override
  public void handle(final Transfer transfer) {
    // TODO Implement
  }

  @Override
  public void handle(final Trip trip) {
    // TODO Implement
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.parsing.IFileHandler#acceptFile(java.nio.file.Path)
   */
  @Override
  public boolean isAcceptingFile(final Path file) {
    // Check if the files content is not already included in the cache
    if (mUseGraphCache && !mRecentHandler.isAcceptingFile(file)) {
      return false;
    }

    // Accept all GTFS files
    LOGGER.info("Accepts file {}", file);
    return true;
  }
}
