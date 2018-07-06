package de.tischner.cobweb.routing.parsing.gtfs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.FareAttribute;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.Frequency;
import org.onebusaway.gtfs.model.Pathway;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.parsing.gtfs.GtfsEntityForwarder;
import de.tischner.cobweb.parsing.gtfs.IGtfsFileHandler;
import de.tischner.cobweb.routing.model.timetable.Connection;
import de.tischner.cobweb.routing.model.timetable.ITimetableIdGenerator;
import de.tischner.cobweb.routing.model.timetable.SequenceStopTime;
import de.tischner.cobweb.routing.model.timetable.Stop;
import de.tischner.cobweb.routing.model.timetable.Timetable;
import de.tischner.cobweb.routing.model.timetable.Trip;
import de.tischner.cobweb.util.collections.CollectionUtil;

public final class GtfsTimetableHandler extends GtfsEntityForwarder implements IGtfsFileHandler {
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(GtfsTimetableHandler.class);
  /**
   * Map connecting external stop IDs to their corresponding object.
   */
  private final MutableMap<AgencyAndId, Stop> mExtIdToStop;
  /**
   * Map connecting trip IDs to their to their corresponding object.
   */
  private final MutableMap<AgencyAndId, Trip> mExtIdToTrip;
  private final ITimetableIdGenerator mIdGenerator;
  private final Timetable mTable;
  /**
   * Map connecting trip IDs to sequence stop times in the sequence of the trip.
   */
  private final MutableMap<AgencyAndId, List<SequenceStopTime>> mTripToSequence;

  public GtfsTimetableHandler(final Timetable table, final ITimetableIdGenerator idGenerator) {
    mTable = table;
    mIdGenerator = idGenerator;
    mExtIdToStop = Maps.mutable.empty();
    mExtIdToTrip = Maps.mutable.empty();
    mTripToSequence = Maps.mutable.empty();
  }

  @Override
  public void complete() throws IOException {
    // Approximate amount of connections
    final int amountOfConnections = mTripToSequence.stream().mapToInt(list -> list.size() - 1).sum();
    final Collection<Connection> connections = FastList.newList(amountOfConnections);

    // Process the sequences and create connections
    mTripToSequence.forEachKeyValue((extTripId, sequence) -> {
      final Trip trip = mExtIdToTrip.get(extTripId);

      final Iterator<SequenceStopTime> sequenceIter = sequence.iterator();
      // Some faulty feeds do not start with a fixed sequence index. In that
      // case we need to skip until we found a fully connected sequence.
      SequenceStopTime sequenceStopTime = null;
      while (sequenceStopTime == null) {
        sequenceStopTime = sequenceIter.next();
      }

      int lastDepStopId = mExtIdToStop.get(sequenceStopTime.getStopId()).getId();
      int lastDepTime = sequenceStopTime.getDepTime();

      while (sequenceIter.hasNext()) {
        sequenceStopTime = sequenceIter.next();
        // Connect last departure to current arrival
        final int arrStopId = mExtIdToStop.get(sequenceStopTime.getStopId()).getId();
        final int arrTime = sequenceStopTime.getArrTime();

        final Connection connection = new Connection(trip.getId(), lastDepStopId, arrStopId, lastDepTime, arrTime);
        connections.add(connection);

        // Prepare next round
        lastDepStopId = arrStopId;
        lastDepTime = sequenceStopTime.getDepTime();
      }
    });

    // Add all connections to the table
    mTable.addConnections(connections);

    // Prepare for possible next round
    mExtIdToStop.clear();
    mExtIdToTrip.clear();
    mTripToSequence.clear();
  }

  @Override
  public void handle(final Agency agency) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final FareAttribute fareAttribute) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final FareRule fareRule) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final FeedInfo feedInfo) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final Frequency frequency) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final org.onebusaway.gtfs.model.Stop stopEntity) {
    // Create an unique ID for each stop and add it to the table
    if (mExtIdToStop.containsKey(stopEntity.getId())) {
      return;
    }

    final Stop stop =
        new Stop(mIdGenerator.generateUniqueStopId(), (float) stopEntity.getLat(), (float) stopEntity.getLon());
    mExtIdToStop.put(stopEntity.getId(), stop);

    mTable.addStop(stop);
  }

  @Override
  public void handle(final org.onebusaway.gtfs.model.Trip tripEntity) {
    // Create an unique ID for each trip and add it to the table
    if (mExtIdToTrip.containsKey(tripEntity.getId())) {
      return;
    }

    final Trip trip = new Trip(mIdGenerator.generateUniqueTripId());
    mExtIdToTrip.put(tripEntity.getId(), trip);

    mTable.addTrip(trip);
  }

  @Override
  public void handle(final Pathway pathway) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final Route route) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final ServiceCalendar serviceCalendar) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final ServiceCalendarDate serviceCalendarDate) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final ShapePoint shapePoint) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final StopTime stopTime) {
    // Remember the sequence to later create the connections
    final int sequenceIndex = stopTime.getStopSequence() - 1;
    final SequenceStopTime sequenceStopTime =
        new SequenceStopTime(stopTime.getArrivalTime(), stopTime.getDepartureTime(), stopTime.getStop().getId());

    final List<SequenceStopTime> sequence = mTripToSequence.getIfAbsentPut(stopTime.getTrip().getId(), FastList::new);
    if (sequence.size() <= sequenceIndex) {
      // Fill with null values until the index is available
      CollectionUtil.increaseCapacity(sequence, sequenceIndex + 1);
    }
    sequence.set(sequenceIndex, sequenceStopTime);
  }

  @Override
  public void handle(final Transfer transfer) {
    // Ignore, not interested in
  }

  @Override
  public boolean isAcceptingFile(final Path file) {
    // Accept all GTFS files
    LOGGER.info("Accepts file {}", file);
    return true;
  }

}
