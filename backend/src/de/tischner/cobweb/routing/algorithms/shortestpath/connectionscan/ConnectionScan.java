package de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import de.tischner.cobweb.routing.algorithms.shortestpath.AShortestPathComputation;
import de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath;
import de.tischner.cobweb.routing.algorithms.shortestpath.IHasPathCost;
import de.tischner.cobweb.routing.algorithms.shortestpath.PathCost;
import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.IPath;
import de.tischner.cobweb.routing.model.graph.transit.IHasTime;
import de.tischner.cobweb.routing.model.graph.transit.TransitEdge;
import de.tischner.cobweb.routing.model.graph.transit.TransitNode;
import de.tischner.cobweb.routing.model.timetable.Connection;
import de.tischner.cobweb.routing.model.timetable.Stop;
import de.tischner.cobweb.routing.model.timetable.Timetable;
import de.tischner.cobweb.routing.model.timetable.Trip;
import de.tischner.cobweb.util.collections.ReverseIterator;

public final class ConnectionScan extends AShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> {

  /**
   * Amount of seconds of a day.
   */
  private static final int SECONDS_OF_DAY = 24 * 60 * 60;

  private static void addEdgeToPath(final EdgePath<ICoreNode, ICoreEdge<ICoreNode>> path, final TransitNode source,
      final TransitNode destination, final boolean walkByFoot) {
    final double cost = destination.getTime() - source.getTime();
    final ICoreEdge<ICoreNode> edge;
    if (walkByFoot) {
      edge = new FootpathTransitEdge<>(0, source, destination, cost);
    } else {
      edge = new TransitEdge<>(0, source, destination, cost);
    }
    path.addEdge(edge, cost);
  }

  private static double computeDuration(final int depTime, final int arrTime) {
    if (depTime > arrTime) {
      throw new IllegalArgumentException();
    }

    return arrTime - depTime;
  }

  private static final int extractStartingTime(final ICoreNode node) {
    if (!(node instanceof IHasTime)) {
      throw new IllegalArgumentException();
    }
    return ((IHasTime) node).getTime();
  }

  private static int validateTimeBeforeAfter(final int time, final int threshold) {
    if (time < threshold) {
      return time + SECONDS_OF_DAY;
    }
    return time;
  }

  private final Timetable mTable;

  public ConnectionScan(final Timetable table) {
    mTable = table;
  }

  @Override
  public Collection<ICoreNode> computeSearchSpace(final Collection<ICoreNode> sources, final ICoreNode destination) {
    final int startingTime = ConnectionScan.extractStartingTime(sources.iterator().next());
    final ConnectionScanResult result = computeShortestPathHelper(sources, destination, startingTime);

    // Collect all visited stops
    final Collection<ICoreNode> searchSpace = new ArrayList<>();
    final int[] stopToArrTime = result.getStopToArrTime();
    for (int i = 0; i < stopToArrTime.length; i++) {
      final int arrTime = stopToArrTime[i];
      // Skip if not visited
      if (arrTime == Integer.MAX_VALUE) {
        continue;
      }
      final Stop stop = mTable.getStop(i);
      searchSpace.add(new TransitNode(i, stop.getLatitude(), stop.getLongitude(), arrTime));
    }

    return searchSpace;
  }

  @Override
  public Optional<IPath<ICoreNode, ICoreEdge<ICoreNode>>> computeShortestPath(final Collection<ICoreNode> sources,
      final ICoreNode destination) {
    final int startingTime = ConnectionScan.extractStartingTime(sources.iterator().next());
    final ConnectionScanResult result = computeShortestPathHelper(sources, destination, startingTime);
    final int[] stopToArrTime = result.getStopToArrTime();

    // Not reachable
    if (stopToArrTime[destination.getId()] == Integer.MAX_VALUE) {
      return Optional.empty();
    }

    // Construct path
    final JourneyPointer[] stopToJourney = result.getStopToJourney();
    final EdgePath<ICoreNode, ICoreEdge<ICoreNode>> path = new EdgePath<>(true);

    int currentStopId = destination.getId();
    TransitNode currentDestination = createNodeForStop(currentStopId, stopToArrTime[currentStopId]);
    while (stopToJourney[currentStopId] != null) {
      final JourneyPointer pointer = stopToJourney[currentStopId];
      final Trip trip = mTable.getTrip(pointer.getExitConnection().getTripId());
      final Connection exitConnection = pointer.getExitConnection();
      final Connection enterConnection = pointer.getEnterConnection();

      // Departure of footpath, arrival of trip exit
      final TransitNode tripPartArr = createNodeForStop(exitConnection.getArrStopId(), exitConnection.getArrTime());
      ConnectionScan.addEdgeToPath(path, tripPartArr, currentDestination, true);

      // Add the trip
      final Iterator<Connection> tripSequenceIter = new ReverseIterator<>(trip.getSequence());
      TransitNode currentConnectionArr = tripPartArr;

      boolean inSequencePart = false;
      while (tripSequenceIter.hasNext()) {
        final Connection connection = tripSequenceIter.next();
        // Identify the used part of the sequence, determined by exit and enter
        // connection
        if (!inSequencePart) {
          // Skip all connections until the exit connection
          if (connection != exitConnection) {
            continue;
          }
          inSequencePart = true;
        }

        final TransitNode connectionDep = createNodeForStop(connection.getDepStopId(), connection.getDepTime());
        ConnectionScan.addEdgeToPath(path, connectionDep, currentConnectionArr, false);

        // Prepare next connection of the trip
        currentConnectionArr = connectionDep;

        // End sequence part after processing the enter connection
        if (connection == enterConnection) {
          break;
        }
      }

      // Prepare next journey pointer
      currentStopId = enterConnection.getDepStopId();
      currentDestination = currentConnectionArr;
    }

    return Optional.of(path);
  }

  @Override
  public Optional<Double> computeShortestPathCost(final Collection<ICoreNode> sources, final ICoreNode destination) {
    final int startingTime = ConnectionScan.extractStartingTime(sources.iterator().next());
    final ConnectionScanResult result = computeShortestPathHelper(sources, destination, startingTime);

    final int arrTime = result.getStopToArrTime()[destination.getId()];

    // Not reachable
    if (arrTime == Integer.MAX_VALUE) {
      return Optional.empty();
    }

    return Optional.of(ConnectionScan.computeDuration(startingTime, arrTime));
  }

  @Override
  public Map<ICoreNode, ? extends IHasPathCost> computeShortestPathCostsReachable(final Collection<ICoreNode> sources) {

    final int startingTime = ConnectionScan.extractStartingTime(sources.iterator().next());
    final ConnectionScanResult result = computeShortestPathHelper(sources, null, startingTime);

    // Collect all reachable stops
    final Map<ICoreNode, PathCost> stopToCost = new HashMap<>();
    final int[] stopToArrTime = result.getStopToArrTime();
    for (int i = 0; i < stopToArrTime.length; i++) {
      final int arrTime = stopToArrTime[i];
      // Skip if not reachable
      if (arrTime == Integer.MAX_VALUE) {
        continue;
      }

      final PathCost cost = new PathCost(ConnectionScan.computeDuration(startingTime, arrTime));
      stopToCost.put(createNodeForStop(i, arrTime), cost);
    }

    return stopToCost;
  }

  private ConnectionScanResult computeShortestPathHelper(final Collection<ICoreNode> sources,
      final ICoreNode pathDestination, final int startingTime) {
    final Integer destinationStop;
    if (pathDestination == null) {
      destinationStop = null;
    } else {
      destinationStop = pathDestination.getId();
    }

    // Initialize data-structures
    final int[] stopToTentativeArrTime = new int[mTable.getGreatestStopId() + 1];
    Arrays.fill(stopToTentativeArrTime, Integer.MAX_VALUE);
    final Connection[] tripToEarliestReachableConnection = new Connection[mTable.getGreatestTripId() + 1];
    final JourneyPointer[] stopToJourney = new JourneyPointer[mTable.getGreatestStopId() + 1];

    // Relax all initial footpaths
    sources.stream().map(IHasId::getId).flatMap(mTable::getOutgoingFootpaths).forEach(footpath -> {
      stopToTentativeArrTime[footpath.getArrStopId()] = startingTime + footpath.getDuration();
    });

    // Process all connections ordered starting from the first after the
    // starting time
    final Iterator<Connection> connections = mTable.getConnectionsStartingSince(startingTime);
    while (connections.hasNext()) {
      final Connection connection = connections.next();
      final int depTime = ConnectionScan.validateTimeBeforeAfter(connection.getDepTime(), startingTime);
      final int depStopId = connection.getDepStopId();
      final int arrTime = ConnectionScan.validateTimeBeforeAfter(connection.getArrTime(), startingTime);
      final int arrStopId = connection.getArrStopId();
      final int tripId = connection.getTripId();

      // Arrived at destination before this connection. The connection can thus
      // not improve the time anymore and since connections are processed
      // ordered the algorithm has finished.
      if (destinationStop != null && stopToTentativeArrTime[destinationStop] <= depTime) {
        break;
      }

      if (tripToEarliestReachableConnection[tripId] == null) {
        // Only process connections that can be taken due to a previous arrival
        // at the departure stop before the departure time
        if (stopToTentativeArrTime[depStopId] > depTime) {
          continue;
        }

        // Trip is used for the first time
        tripToEarliestReachableConnection[tripId] = connection;
      }

      // Do not relax if connection does not improve arrival time at this stop
      if (arrTime >= stopToTentativeArrTime[arrStopId]) {
        continue;
      }

      // Relax all outgoing footpaths
      mTable.getOutgoingFootpaths(arrStopId).forEach(footpath -> {
        final int footpathArrStopId = footpath.getArrStopId();
        final int footpathTime = arrTime + footpath.getDuration();

        // Only use footpath if it improves the arrival time at the destination
        if (footpathTime >= stopToTentativeArrTime[footpathArrStopId]) {
          return;
        }

        // Take this footpath
        stopToTentativeArrTime[footpathArrStopId] = footpathTime;
        stopToJourney[footpathArrStopId] =
            new JourneyPointer(tripToEarliestReachableConnection[tripId], connection, footpath);
      });
    }

    return new ConnectionScanResult(stopToTentativeArrTime, stopToJourney);
  }

  private TransitNode createNodeForStop(final int stopId, final int time) {
    final Stop stop = mTable.getStop(stopId);
    return new TransitNode(stopId, stop.getLatitude(), stop.getLongitude(), time);
  }
}
