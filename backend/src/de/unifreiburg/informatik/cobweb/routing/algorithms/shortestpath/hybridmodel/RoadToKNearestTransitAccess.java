package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.hybridmodel;

import java.util.Collection;
import java.util.Collections;

import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric;
import de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree;
import de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.INearestNeighborComputation;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.road.IRoadNode;
import de.unifreiburg.informatik.cobweb.routing.model.timetable.Stop;
import de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable;

/**
 * Implementation of an access node computation that, given a road node,
 * provides the <tt>k</tt> nearest transit stops as access nodes.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public class RoadToKNearestTransitAccess implements IAccessNodeComputation<ICoreNode, ICoreNode> {
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
   * The maximal amount of access nodes to search.
   */
  private final int mAmount;
  /**
   * The data-structure to use for fast nearest stop computation.
   */
  private final INearestNeighborComputation<Stop> mNearestStopComputation;

  /**
   * Creates a new translation that translates to the stops contained in the
   * given timetable.
   *
   * @param table  The timetable that contains the stops to consider
   * @param amount The maximal amount of access nodes to search
   */
  public RoadToKNearestTransitAccess(final Timetable table, final int amount) {
    mNearestStopComputation = RoadToKNearestTransitAccess.createNearestStopComputation(table);
    mAmount = amount;
  }

  @Override
  public Collection<ICoreNode> computeAccessNodes(final ICoreNode element) {
    if (!(element instanceof IRoadNode)) {
      throw new IllegalArgumentException();
    }

    // Search k nearest stops
    final Stop searchNeedle = new Stop(0, element.getLatitude(), element.getLongitude());
    return Collections.unmodifiableCollection(mNearestStopComputation.getKNearestNeighbors(searchNeedle, mAmount));
  }

}
