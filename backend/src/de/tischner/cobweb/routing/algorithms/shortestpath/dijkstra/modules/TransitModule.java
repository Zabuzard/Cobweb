package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.link.LinkEdge;
import de.tischner.cobweb.routing.model.graph.transit.ITransitNode;
import de.tischner.cobweb.util.RoutingUtil;

public final class TransitModule<N extends INode, E extends IEdge<N>> implements IModule<N, E> {
  private static final int SECONDS_OF_DAY = 24 * 60 * 60;

  public static <N extends INode, E extends IEdge<N>> TransitModule<N, E> of(final long depTime) {
    return new TransitModule<>(depTime);
  }

  private static double computeEdgeCost(final long depTime, final double travelTime, final int connectionTime) {
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

  private final long mDepTime;

  public TransitModule(final long depTime) {
    mDepTime = depTime;
  }

  @Override
  public double provideEdgeCost(final E edge, final double tentativeDistance) {
    // Only interested in link edges entering the transit graph
    if (!(edge instanceof LinkEdge && edge.getDestination() instanceof ITransitNode)) {
      return edge.getCost();
    }

    final ITransitNode destination = (ITransitNode) edge.getDestination();
    final int connectionTime = destination.getTime();
    return TransitModule.computeEdgeCost(mDepTime, tentativeDistance, connectionTime);
  }

}
