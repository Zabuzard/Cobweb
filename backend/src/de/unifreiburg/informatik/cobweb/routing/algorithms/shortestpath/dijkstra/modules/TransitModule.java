package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.OptionalDouble;

import de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.link.LinkEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.ITransitNode;
import de.unifreiburg.informatik.cobweb.util.RoutingUtil;

/**
 * Module for a {@link ModuleDijkstra} that dynamically provides the correct
 * edge costs for {@link LinkEdge}s connecting road and transit graphs based on
 * the departure and current travel time.<br>
 * <br>
 * The factory method {@link #of(long)} can be used for convenient instance
 * creation.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the nodes
 * @param <E> Type of the edges
 */
public final class TransitModule<N extends INode, E extends IEdge<N>> implements IModule<N, E> {
  /**
   * Amount of seconds of a day.
   */
  private static final int SECONDS_OF_DAY = 24 * 60 * 60;

  /**
   * Creates a transit module instance which respects the given departure time.
   *
   * @param         <N> Type of the nodes
   * @param         <E> Type of the edges
   * @param depTime The departure time in milliseconds since epoch, i.e. the
   *                time when routing starts at the source node
   * @return The created transit module instance
   */
  public static <N extends INode, E extends IEdge<N>> TransitModule<N, E> of(final long depTime) {
    return new TransitModule<>(depTime);
  }

  /**
   * Computes the time in seconds when the given connection is available again
   * with respect to the departure and travel time.
   *
   * @param depTime        The departure time in milliseconds since epoch, i.e.
   *                       the time when routing starts at the source node
   * @param travelTime     The travel time in seconds, i.e. offset to the
   *                       departure time
   * @param connectionTime The time of the day when this connection is
   *                       available, in seconds since midnight. Is allowed to
   *                       overflow a day as this is irrelevant for the
   *                       connection at the given day.
   * @return The time in seconds when the given connection is available again
   */
  private static double computeWaitTime(final long depTime, final double travelTime, final int connectionTime) {
    // Get the date time at which the edge is relaxed
    final LocalDateTime departure = LocalDateTime.ofInstant(Instant.ofEpochMilli(depTime), ZoneId.systemDefault());
    final LocalDateTime relaxDateTime = departure.plusNanos(RoutingUtil.secondsToNanos(travelTime));

    // Get the date time at which the edge can be taken next
    final LocalTime timeOfConnection = LocalTime.ofSecondOfDay(connectionTime % SECONDS_OF_DAY);
    LocalDateTime nextConnectionDateTime = timeOfConnection.atDate(relaxDateTime.toLocalDate());
    // TODO Respect connection schedule according to GTFS
    // Wait to the next day
    if (nextConnectionDateTime.isBefore(relaxDateTime)) {
      nextConnectionDateTime = nextConnectionDateTime.plusDays(1);
    }

    // Compute duration between both date times in seconds
    return RoutingUtil.millisToSeconds(Duration.between(relaxDateTime, nextConnectionDateTime).toMillis());
  }

  /**
   * The departure time in milliseconds since epoch, i.e. the time when routing
   * starts at the source node.
   */
  private final long mDepTime;

  /**
   * Creates a transit module instance which respects the given departure time.
   *
   * @param depTime The departure time in milliseconds since epoch, i.e. the
   *                time when routing starts at the source node
   */
  public TransitModule(final long depTime) {
    mDepTime = depTime;
  }

  /**
   * Provides the cost of {@link LinkEdge}s that have a destination node of type
   * {@link ITransitNode}. Those edges go from a road into a transit
   * network.<br>
   * <br>
   * The edge cost is the time needed to wait, in seconds, until the connection
   * represented by the destination is available again.
   */
  @Override
  public OptionalDouble provideEdgeCost(final E edge, final double tentativeDistance) {
    // Only interested in link edges entering the transit graph
    if (!(edge instanceof LinkEdge && edge.getDestination() instanceof ITransitNode)) {
      return OptionalDouble.empty();
    }

    final ITransitNode destination = (ITransitNode) edge.getDestination();
    final int connectionTime = destination.getTime();
    return OptionalDouble.of(TransitModule.computeWaitTime(mDepTime, tentativeDistance, connectionTime));
  }

}
