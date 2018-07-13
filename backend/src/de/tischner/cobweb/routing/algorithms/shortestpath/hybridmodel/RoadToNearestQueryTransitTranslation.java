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

/**
 * Implementation of a translation that translates road nodes to their nearest
 * transit node representing a stop at the given time.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public class RoadToNearestQueryTransitTranslation implements ITranslationWithTime<ICoreNode, ICoreNode> {
  /**
   * Creates a data-structure for fast computation of nearest stops based on the
   * stops contained in the given timetable.
   *
   * @param table The timetable containing the stops to consider
   * @return The constructed data-structure for fast nearest stop computation
   */
  private static INearestNeighborComputation<Stop> createNearestStopComputation(final Timetable table) {
    final CoverTree<Stop> nearestStopComputation = new CoverTree<>(new AsTheCrowFliesMetric<>());
    table.getStops().forEach(nearestStopComputation::insert);
    return nearestStopComputation;
  }

  /**
   * The data-structure to use for fast nearest stop computation.
   */
  private final INearestNeighborComputation<Stop> mNearestStopComputation;

  /**
   * Creates a new translation that translates to the stops contained in the
   * given timetable.
   *
   * @param table The timetable that contains the stops to consider
   */
  public RoadToNearestQueryTransitTranslation(final Timetable table) {
    mNearestStopComputation = RoadToNearestQueryTransitTranslation.createNearestStopComputation(table);
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
