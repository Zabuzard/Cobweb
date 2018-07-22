package de.unifreiburg.informatik.cobweb.routing.model.timetable;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.stream.Stream;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unifreiburg.informatik.cobweb.routing.model.graph.UniqueIdGenerator;
import de.unifreiburg.informatik.cobweb.util.RoutingUtil;
import de.unifreiburg.informatik.cobweb.util.collections.RangedOverflowListIterator;

/**
 * A timetable for representing a transit network consisting of stops, trips,
 * connections and footpaths.<br>
 * <br>
 * Use methods like {@link #addConnections(Collection)}, {@link #addStop(Stop)},
 * {@link #addTrip(Trip)} and {@link #addFootpath(Footpath)} to modify the
 * table. After finishing modifying use {@link #correctFootpaths(int, int)} to
 * correct the footpath model. Methods like
 * {@link #getConnectionsStartingSince(int)} and other getters can be used to
 * retrieve data.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class Timetable implements ITimetableIdGenerator, Serializable {
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(Timetable.class);
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The amount of footpaths contained in the timetable.
   */
  private int mAmountOfFootpaths;
  /**
   * The list of all connections, sorted ascending in departure time.
   */
  private final List<Connection> mConnections;
  /**
   * Data-structure mapping stop IDs to all IDs of stops that can be reached
   * from them by foot.
   */
  private final MutableIntObjectMap<MutableIntSet> mFootpathReachability;
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
   * Data-structure mapping stop IDs to all outgoing footpaths.
   */
  private final MutableIntObjectMap<Collection<Footpath>> mStopIdToOutgoingFootpaths;
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
    mStopIdToOutgoingFootpaths = IntObjectMaps.mutable.empty();
    mFootpathReachability = IntObjectMaps.mutable.empty();
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
   * Adds the given footpath to the timetable.
   *
   * @param footpath The footpath to add
   */
  public void addFootpath(final Footpath footpath) {
    mStopIdToOutgoingFootpaths.getIfAbsentPut(footpath.getDepStopId(), FastList::new).add(footpath);
    mFootpathReachability.getIfAbsentPut(footpath.getDepStopId(), IntSets.mutable.empty()).add(footpath.getArrStopId());
    mAmountOfFootpaths++;
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

  /**
   * Corrects the footpath model by adding missing self-loops and all missing
   * edges such that the graph is transitively closed.
   *
   * @param transferDelay        The amount in seconds a transfer at the same
   *                             stop takes, in case there was no such transfer
   *                             added before
   * @param footpathReachability The range in meters stops are connected by
   *                             footpaths, in case there was no such transfer
   *                             added before. Walking duration is approximated
   *                             based on the stop coordinates
   */
  public void correctFootpaths(final int transferDelay, final int footpathReachability) {
    LOGGER.debug("Correcting footpaths");

    // Add missing self-loops
    LOGGER.debug("Computing missing self-loops");
    final Collection<Footpath> selfLoopsToAdd = FastList.newList();
    mIdToStop.keysView().forEach(fromId -> {
      final MutableIntSet reachableStopIds = mFootpathReachability.get(fromId);
      if (reachableStopIds == null || !reachableStopIds.contains(fromId)) {
        // Self-loop is missing
        selfLoopsToAdd.add(new Footpath(fromId, fromId, transferDelay));
      }
    });
    selfLoopsToAdd.forEach(this::addFootpath);
    LOGGER.debug("Adding {} self-loops", selfLoopsToAdd.size());

    // Connect close stops
    LOGGER.debug("Connecting close stops");
    final Collection<Footpath> closeFootpathsToAdd = FastList.newList();
    mIdToStop.values().forEach(fromStop -> {
      final int fromStopId = fromStop.getId();
      final MutableIntSet reachableStopIds = mFootpathReachability.get(fromStopId);
      mIdToStop.values().stream().forEach(toStop -> {
        // Ignore already reachable stops
        if (reachableStopIds != null && reachableStopIds.contains(toStop.getId())) {
          return;
        }

        // Do not consider stop as target if not close enough
        final double distance = RoutingUtil.distanceEquiRect(fromStop, toStop);
        if (distance > footpathReachability) {
          return;
        }

        // Construct footpath
        final double speed = RoutingUtil.getWalkingSpeed();
        final int duration = (int) RoutingUtil.travelTime(distance, speed);
        closeFootpathsToAdd.add(new Footpath(fromStopId, toStop.getId(), duration));
      });
    });
    closeFootpathsToAdd.forEach(this::addFootpath);
    LOGGER.debug("Adding {} footpaths to close stops", closeFootpathsToAdd.size());

    // Compute transitive closure
    LOGGER.debug("Computing transitive closure");
    final Collection<Footpath> transitiveClosureToAdd = FastList.newList();
    // Breadth-first-search per stop
    mIdToStop.keysView().forEach(fromStopId -> {
      // Find all reachable stops
      final Queue<Integer> stopsToRelax = new ArrayDeque<>();
      stopsToRelax.add(fromStopId);
      final MutableIntSet deepReachable = IntSets.mutable.empty();
      deepReachable.add(fromStopId);

      while (!stopsToRelax.isEmpty()) {
        final int currentStop = stopsToRelax.poll();
        final MutableIntSet directReachable = mFootpathReachability.get(currentStop);
        if (directReachable == null) {
          continue;
        }
        directReachable.forEach(directTarget -> {
          // Target was not visited already
          if (!deepReachable.contains(directTarget)) {
            stopsToRelax.add(directTarget);
            deepReachable.add(directTarget);
          }
        });
      }

      // Compute the difference between direct and deep reachable, those are the
      // edges to add for the transitive closure
      if (mFootpathReachability != null) {
        deepReachable.removeAll(mFootpathReachability.get(fromStopId));
      }
      final Stop fromStop = mIdToStop.get(fromStopId);
      deepReachable.forEach(toStopId -> {
        final Stop toStop = mIdToStop.get(toStopId);
        // Construct footpath
        final double distance = RoutingUtil.distanceEquiRect(fromStop, toStop);
        final double speed = RoutingUtil.getWalkingSpeed();
        final int duration = (int) RoutingUtil.travelTime(distance, speed);
        transitiveClosureToAdd.add(new Footpath(fromStopId, toStopId, duration));
      });
    });
    transitiveClosureToAdd.forEach(this::addFootpath);
    LOGGER.debug("Adding {} footpaths for transitive closure", transitiveClosureToAdd.size());
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
    return mStopIdToOutgoingFootpaths.get(stopId).stream();
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
    sj.add("footpaths=" + mAmountOfFootpaths);
    return sj.toString();
  }
}
