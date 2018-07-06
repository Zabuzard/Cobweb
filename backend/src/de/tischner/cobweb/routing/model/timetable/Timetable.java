package de.tischner.cobweb.routing.model.timetable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringJoiner;
import java.util.stream.Stream;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;

import de.tischner.cobweb.routing.model.graph.UniqueIdGenerator;
import de.tischner.cobweb.util.RoutingUtil;
import de.tischner.cobweb.util.collections.RangedOverflowListIterator;

public final class Timetable implements ITimetableIdGenerator, Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  private final List<Connection> mConnections;
  private int mGreatestStopId;
  private int mGreatestTripId;
  private final MutableIntObjectMap<Stop> mIdToStop;
  private final MutableIntObjectMap<Trip> mIdToTrip;
  /**
   * The unique ID generator used for stops.
   */
  private final UniqueIdGenerator mStopIdGenerator;
  /**
   * The unique ID generator used for trips.
   */
  private final UniqueIdGenerator mTripIdGenerator;

  public Timetable() {
    mStopIdGenerator = new UniqueIdGenerator();
    mTripIdGenerator = new UniqueIdGenerator();
    mConnections = new ArrayList<>();
    mIdToStop = IntObjectMaps.mutable.empty();
    mIdToTrip = IntObjectMaps.mutable.empty();
  }

  public void addConnections(final Collection<Connection> connections) {
    final boolean hasChanged = mConnections.addAll(connections);
    if (hasChanged) {
      Collections.sort(mConnections);
    }
  }

  public void addStop(final Stop stop) {
    mIdToStop.put(stop.getId(), stop);
  }

  public void addTrip(final Trip trip) {
    mIdToTrip.put(trip.getId(), trip);
  }

  @Override
  public int generateUniqueStopId() throws NoSuchElementException {
    final int id = mStopIdGenerator.generateUniqueId();
    if (id > mGreatestStopId) {
      mGreatestStopId = id;
    }
    return id;
  }

  @Override
  public int generateUniqueTripId() throws NoSuchElementException {
    final int id = mTripIdGenerator.generateUniqueId();
    if (id > mGreatestTripId) {
      mGreatestTripId = id;
    }
    return id;
  }

  public Iterator<Connection> getConnectionsStartingSince(final int time) {
    final Connection searchNeedle = new Connection(0, 0, 0, time, time);
    final int indexOfNext = -1 * Collections.binarySearch(mConnections, searchNeedle) - 1;

    // If all connections are before the given time
    if (indexOfNext == mConnections.size()) {
      // Use a regular iterator starting from the first element
      return mConnections.iterator();
    }

    // Use a ranged overflow iterator based on random access
    return new RangedOverflowListIterator<>(mConnections, indexOfNext);
  }

  public int getGreatestStopId() {
    return mGreatestStopId;
  }

  public int getGreatestTripId() {
    return mGreatestTripId;
  }

  public Stream<Footpath> getOutgoingFootpaths(final int stopId) {
    final Stop stop = mIdToStop.get(stopId);
    if (stop == null) {
      return Stream.empty();
    }

    // TODO It may be inefficient to construct footpaths on the fly
    return mIdToStop.values().stream().map(arrStop -> {
      final int arrId = arrStop.getId();

      // Footpath to same stop
      if (arrId == stopId) {
        // TODO Model transfer time but also allow zero transfer time for same
        // trip
        return new Footpath(stopId, arrId, 0);
      }

      // Compute walking time
      final double distance = RoutingUtil.distanceEquiRect(stop, arrStop);
      final double speed = RoutingUtil.getWalkingSpeed();
      final int duration = (int) RoutingUtil.travelTime(distance, speed);

      return new Footpath(stopId, arrId, duration);
    });
  }

  /**
   * Gets a human readable string that contains size information of the table,
   * i.e. the amount of stopsm trips and connections.
   *
   * @return A human readable string containing size information
   */
  public String getSizeInformation() {
    return toString();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringJoiner sj = new StringJoiner(", ", getClass().getSimpleName() + "[", "]");
    sj.add("stops=" + mIdToStop.size());
    sj.add("trips=" + mIdToTrip.size());
    sj.add("connections=" + mConnections.size());
    return sj.toString();
  }
}
