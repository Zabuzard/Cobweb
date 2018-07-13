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

/**
 * A timetable for representing a transit network consisting of stops, trips,
 * connections and footpaths.<br>
 * <br>
 * Use methods like {@link #addConnections(Collection)}, {@link #addStop(Stop)}
 * and {@link #addTrip(Trip)} to modify the table. Methods are
 * {@link #getConnectionsStartingSince(int)} and other getters can be used to
 * retrieve data.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class Timetable implements ITimetableIdGenerator, Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * Transfer time in seconds.
   */
  private static final int TRANSFER_DELAY = 5 * 60;
  /**
   * The list of all connections, sorted ascending in departure time.
   */
  private final List<Connection> mConnections;
  /**
   * The greatest ID currently in use for a stop in this table.
   */
  private int mGreatestStopId;
  /**
   * The greatest ID currently in use for a trip in this table.
   */
  private int mGreatestTripId;
  /**
   * Data-structure mapping IDs to their corresponding stops.
   */
  private final MutableIntObjectMap<Stop> mIdToStop;
  /**
   * Data-structure mapping IDs to their corresponding trips.
   */
  private final MutableIntObjectMap<Trip> mIdToTrip;
  /**
   * The unique ID generator used for stops.
   */
  private final UniqueIdGenerator mStopIdGenerator;
  /**
   * The unique ID generator used for trips.
   */
  private final UniqueIdGenerator mTripIdGenerator;

  /**
   * Creates a new initially empty timetable.
   */
  public Timetable() {
    mStopIdGenerator = new UniqueIdGenerator();
    mTripIdGenerator = new UniqueIdGenerator();
    mConnections = new ArrayList<>();
    mIdToStop = IntObjectMaps.mutable.empty();
    mIdToTrip = IntObjectMaps.mutable.empty();
  }

  /**
   * Adds the given connections to the timetable.<br>
   * <br>
   * The method invokes a sort of all connections. Thus it should not be called
   * often and preferable with lots of connections at once.
   *
   * @param connections The connections to add
   */
  public void addConnections(final Collection<Connection> connections) {
    final boolean hasChanged = mConnections.addAll(connections);
    if (hasChanged) {
      Collections.sort(mConnections);
    }
  }

  /**
   * Adds the given stop to the table.
   *
   * @param stop The stop to add
   */
  public void addStop(final Stop stop) {
    mIdToStop.put(stop.getId(), stop);
  }

  /**
   * Adds the given trip to the table.
   *
   * @param trip The trip to add
   */
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

  /**
   * Creates an iterator which returns all connections of this table, starting
   * with the first connection departing after, or exactly at, the given
   * time.<br>
   * <br>
   * Note that this also includes connections departing at the day after. The
   * iterator ends after all connections have been traversed.
   *
   * @param time The time to get connections since, in seconds since midnight.
   * @return An iterator over all connections, starting with the first
   *         connection departing not before the given time
   */
  public Iterator<Connection> getConnectionsStartingSince(final int time) {
    final Connection searchNeedle = new Connection(-1, -1, -1, -1, time, time);
    final int indexOfNext = -1 * Collections.binarySearch(mConnections, searchNeedle) - 1;

    // If all connections are before the given time
    if (indexOfNext == mConnections.size()) {
      // Use a regular iterator starting from the first element
      return mConnections.iterator();
    }

    // Use a ranged overflow iterator based on random access
    return new RangedOverflowListIterator<>(mConnections, indexOfNext);
  }

  /**
   * Gets the greatest ID currently in use for a stop in this table.
   *
   * @return The greatest ID currently in use for a stop in this table
   */
  public int getGreatestStopId() {
    return mGreatestStopId;
  }

  /**
   * Gets the greatest ID currently in use for a trip in this table.
   *
   * @return The greatest ID currently in use for a trip in this table
   */
  public int getGreatestTripId() {
    return mGreatestTripId;
  }

  /**
   * Gets a stream over all footpaths going out of the given stop.
   *
   * @param stopId The unique ID fo the stop to get footpaths from
   * @return A stream over all footpaths going out of the given stop
   */
  public Stream<Footpath> getOutgoingFootpaths(final int stopId) {
    final Stop stop = mIdToStop.get(stopId);
    if (stop == null) {
      return Stream.empty();
    }

    // TODO It may be inefficient to construct footpaths on the fly to all other
    // stops
    return mIdToStop.values().stream().map(arrStop -> {
      final int arrId = arrStop.getId();

      // Footpath to same stop
      if (arrId == stopId) {
        return new Footpath(stopId, arrId, TRANSFER_DELAY);
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
   * i.e. the amount of stops, trips and connections.
   *
   * @return A human readable string containing size information
   */
  public String getSizeInformation() {
    return toString();
  }

  /**
   * Gets the stop with the given ID.
   *
   * @param id The unique ID of the stop to get
   * @return The stop with the given ID
   */
  public Stop getStop(final int id) {
    return mIdToStop.get(id);
  }

  /**
   * Gets a collection of all stops contained in the table.
   *
   * @return A collection of all stops contained in the table
   */
  public Collection<Stop> getStops() {
    return mIdToStop.values();
  }

  /**
   * Gets the trip with the given ID.
   *
   * @param id The unique ID of the trip to get
   * @return The trip with the given ID
   */
  public Trip getTrip(final int id) {
    return mIdToTrip.get(id);
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
