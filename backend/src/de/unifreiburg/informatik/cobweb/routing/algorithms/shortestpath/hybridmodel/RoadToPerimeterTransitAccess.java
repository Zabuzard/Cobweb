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
import de.unifreiburg.informatik.cobweb.util.RoutingUtil;

/**
 * Implementation of an access node computation that, given a road node,
 * provides all transit stops in a given range as access nodes.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public class RoadToPerimeterTransitAccess implements IAccessNodeComputation<ICoreNode, ICoreNode> {
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
   * The range to search access nodes in, in seconds need to travel the
   * distance.
   */
  private final double mRangeInTravelTime;

  /**
   * Creates a new translation that translates to the stops contained in the
   * given timetable.
   *
   * @param table The timetable that contains the stops to consider
   * @param range The range to search access nodes in, in metres
   */
  public RoadToPerimeterTransitAccess(final Timetable table, final int range) {
    mNearestStopComputation = RoadToPerimeterTransitAccess.createNearestStopComputation(table);

    final double maximalSpeed = RoutingUtil.maximalRoadSpeed();
    mRangeInTravelTime = RoutingUtil.travelTime(range, maximalSpeed);
  }

  @Override
  public Collection<ICoreNode> computeAccessNodes(final ICoreNode element) {
    if (!(element instanceof IRoadNode)) {
      throw new IllegalArgumentException();
    }

    // Search stops in the given range to the given road node
    final Stop searchNeedle = new Stop(0, element.getLatitude(), element.getLongitude());
    return Collections
        .unmodifiableCollection(mNearestStopComputation.getNeighborhood(searchNeedle, mRangeInTravelTime));
  }

}
