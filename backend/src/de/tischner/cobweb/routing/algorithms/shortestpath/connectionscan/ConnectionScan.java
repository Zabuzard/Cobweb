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
import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.IPath;
import de.tischner.cobweb.routing.model.graph.transit.TransitEdge;
import de.tischner.cobweb.routing.model.graph.transit.TransitNode;
import de.tischner.cobweb.routing.model.timetable.Connection;
import de.tischner.cobweb.routing.model.timetable.Stop;
import de.tischner.cobweb.routing.model.timetable.Timetable;

public final class ConnectionScan extends AShortestPathComputation<TransitNode, TransitEdge<TransitNode>> {

  /**
   * Amount of seconds of a day.
   */
  private static final int SECONDS_OF_DAY = 24 * 60 * 60;

  private static void addEdgeToPath(final EdgePath<TransitNode, TransitEdge<TransitNode>> path,
      final TransitNode source, final TransitNode destination) {
    final double cost = destination.getTime() - source.getTime();
    path.addEdge(new TransitEdge<>(0, source, destination, cost), cost);
  }

  private static double computeDuration(final int depTime, final int arrTime) {
    if (depTime > arrTime) {
      throw new IllegalArgumentException();
    }

    return arrTime - depTime;
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
  public Collection<TransitNode> computeSearchSpace(final Collection<TransitNode> sources,
      final TransitNode destination) {
    final ConnectionScanResult result =
        computeShortestPathHelper(sources, destination, sources.iterator().next().getTime());

    // Collect all visited stops
    final Collection<TransitNode> searchSpace = new ArrayList<>();
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
  public Optional<IPath<TransitNode, TransitEdge<TransitNode>>>
      computeShortestPath(final Collection<TransitNode> sources, final TransitNode destination) {
    final int startingTime = sources.iterator().next().getTime();
    final ConnectionScanResult result = computeShortestPathHelper(sources, destination, startingTime);
    final int[] stopToArrTime = result.getStopToArrTime();

    // Not reachable
    if (stopToArrTime[destination.getId()] == Integer.MAX_VALUE) {
      return Optional.empty();
    }

    // Construct path
    final JourneyPointer[] stopToJourney = result.getStopToJourney();
    final EdgePath<TransitNode, TransitEdge<TransitNode>> path = new EdgePath<>(true);

    int currentStopId = destination.getId();
    TransitNode currentDestination = createNodeForStop(currentStopId, stopToArrTime[currentStopId]);
    while (stopToJourney[currentStopId] != null) {
      final JourneyPointer pointer = stopToJourney[currentStopId];
      final Connection exitConnection = pointer.getExitConnection();

      // TODO Path construction is likely wrong, investigate

      // Footpath to destination
      final TransitNode betweenExitAndFootpath =
          createNodeForStop(exitConnection.getArrStopId(), exitConnection.getArrTime());
      ConnectionScan.addEdgeToPath(path, betweenExitAndFootpath, currentDestination);

      // Exit to footpath
      final TransitNode exitDeparture = createNodeForStop(exitConnection.getDepStopId(), exitConnection.getDepTime());
      ConnectionScan.addEdgeToPath(path, exitDeparture, betweenExitAndFootpath);

      // Prepare for next round
      currentStopId = pointer.getEnterConnection().getDepStopId();
      currentDestination = exitDeparture;
    }

    return Optional.of(path);
  }

  @Override
  public Optional<Double> computeShortestPathCost(final Collection<TransitNode> sources,
      final TransitNode destination) {
    final int startingTime = sources.iterator().next().getTime();
    final ConnectionScanResult result = computeShortestPathHelper(sources, destination, startingTime);

    final int arrTime = result.getStopToArrTime()[destination.getId()];

    // Not reachable
    if (arrTime == Integer.MAX_VALUE) {
      return Optional.empty();
    }

    return Optional.of(ConnectionScan.computeDuration(startingTime, arrTime));
  }

  @Override
  public Map<TransitNode, ? extends IHasPathCost>
      computeShortestPathCostsReachable(final Collection<TransitNode> sources) {

    final int startingTime = sources.iterator().next().getTime();
    final ConnectionScanResult result = computeShortestPathHelper(sources, null, startingTime);

    // Collect all reachable stops
    final Map<TransitNode, PathCost> stopToCost = new HashMap<>();
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

  private ConnectionScanResult computeShortestPathHelper(final Collection<TransitNode> sources,
      final TransitNode pathDestination, final int startingTime) {
    final Integer destinationStop;
    if (pathDestination == null) {
      destinationStop = null;
    } else {
      destinationStop = pathDestination.getId();
    }

    // Initialize data-structures
    final int[] stopToTentativeArrTime = new int[mTable.getGreatestStopId()];
    Arrays.fill(stopToTentativeArrTime, Integer.MAX_VALUE);
    final Connection[] tripToEarliestReachableConnection = new Connection[mTable.getGreatestTripId()];
    final JourneyPointer[] stopToJourney = new JourneyPointer[mTable.getGreatestStopId()];

    // Relax all initial footpaths
    sources.stream().map(IHasId::getId).flatMap(mTable::getOutgoingFootpaths).forEach(footpath -> {
      stopToTentativeArrTime[footpath.getArrId()] = startingTime + footpath.getDuration();
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
        final int footpathArrStopId = footpath.getArrId();
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
