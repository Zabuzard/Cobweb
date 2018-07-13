package de.tischner.cobweb.routing.algorithms.shortestpath.hybridmodel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import de.tischner.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric;
import de.tischner.cobweb.routing.algorithms.nearestneighbor.CoverTree;
import de.tischner.cobweb.routing.algorithms.nearestneighbor.INearestNeighborComputation;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.road.IRoadNode;
import de.tischner.cobweb.routing.model.graph.transit.TransitNode;
import de.tischner.cobweb.routing.model.timetable.Stop;
import de.tischner.cobweb.routing.model.timetable.Timetable;

public class RoadToNearestQueryTransitTranslation implements ITranslationWithTime<ICoreNode, ICoreNode> {

  private static INearestNeighborComputation<Stop> createNearestStopComputation(final Timetable table) {
    final CoverTree<Stop> nearestStopComputation = new CoverTree<>(new AsTheCrowFliesMetric<>());
    table.getStops().forEach(nearestStopComputation::insert);
    return nearestStopComputation;
  }

  private final INearestNeighborComputation<Stop> mNearestStopComputation;

  private final Timetable mTable;

  public RoadToNearestQueryTransitTranslation(final Timetable table) {
    mTable = table;
    mNearestStopComputation = RoadToNearestQueryTransitTranslation.createNearestStopComputation(mTable);
  }

  @Override
  public ICoreNode translate(final ICoreNode element, final long time) {
    if (!(element instanceof IRoadNode)) {
      throw new IllegalArgumentException();
    }

    // Search stop nearest to the given road node
    final Stop searchNeedle = new Stop(0, element.getLatitude(), element.getLongitude());
    final Optional<Stop> possibleNearestStop = mNearestStopComputation.getNearestNeighbor(searchNeedle);
    if (!possibleNearestStop.isPresent()) {
      return null;
    }
    final Stop nearestStop = possibleNearestStop.get();

    // Convert millis since epoch to seconds since midnight at the given date
    final LocalDateTime dateTimeAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    final int secondsSinceMidnight = dateTimeAt.toLocalTime().toSecondOfDay();

    // Construct the query transit node
    return new TransitNode(nearestStop.getId(), nearestStop.getLatitude(), nearestStop.getLongitude(),
        secondsSinceMidnight);
  }

}
