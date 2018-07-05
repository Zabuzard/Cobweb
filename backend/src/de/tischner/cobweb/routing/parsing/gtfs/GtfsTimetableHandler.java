package de.tischner.cobweb.routing.parsing.gtfs;

import java.io.IOException;
import java.nio.file.Path;

import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.FareAttribute;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.Frequency;
import org.onebusaway.gtfs.model.Pathway;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.parsing.gtfs.GtfsEntityForwarder;
import de.tischner.cobweb.parsing.gtfs.IGtfsFileHandler;
import de.tischner.cobweb.routing.model.timetable.Timetable;

public final class GtfsTimetableHandler extends GtfsEntityForwarder implements IGtfsFileHandler {
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(GtfsTimetableHandler.class);
  private final Timetable mTable;

  public GtfsTimetableHandler(final Timetable table) {
    mTable = table;
  }

  @Override
  public void complete() throws IOException {
    // TODO Implement
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
  public void handle(final Stop stop) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final StopTime stopTime) {
    // TODO Implement
  }

  @Override
  public void handle(final Transfer transfer) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final Trip trip) {
    // Ignore, not interested in
  }

  @Override
  public boolean isAcceptingFile(final Path file) {
    // Accept all GTFS files
    LOGGER.info("Accepts file {}", file);
    return true;
  }

}
