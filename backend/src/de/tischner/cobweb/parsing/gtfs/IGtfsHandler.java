package de.tischner.cobweb.parsing.gtfs;

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

/**
 * Interface for classes that handle GTFS entities.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IGtfsHandler extends IBaseEntityHandler {
  /**
   * Handles the given agency.
   *
   * @param agency The agency to handle
   */
  void handle(Agency agency);

  /**
   * Handles the given fare attribute.
   *
   * @param fareAttribute The fare attribute to handle
   */
  void handle(FareAttribute fareAttribute);

  /**
   * Handles the given fare rule.
   *
   * @param fareRule The fare rule to handle
   */
  void handle(FareRule fareRule);

  /**
   * Handles the given feed info.
   *
   * @param feedInfo The feed info to handle
   */
  void handle(FeedInfo feedInfo);

  /**
   * Handles the given frequency.
   *
   * @param frequency The frequency to handle
   */
  void handle(Frequency frequency);

  /**
   * Handles the given pathway.
   *
   * @param pathway The pathway to handle
   */
  void handle(Pathway pathway);

  /**
   * Handles the given route.
   *
   * @param route The route to handle
   */
  void handle(Route route);

  /**
   * Handles the given service calendar.
   *
   * @param serviceCalendar The service calendar to handle
   */
  void handle(ServiceCalendar serviceCalendar);

  /**
   * Handles the given service calendar date.
   *
   * @param serviceCalendarDate The service calendar date to handle
   */
  void handle(ServiceCalendarDate serviceCalendarDate);

  /**
   * Handles the given shape point.
   *
   * @param shapePoint The shape point to handle
   */
  void handle(ShapePoint shapePoint);

  /**
   * Handles the given stop.
   *
   * @param stop The stop to handle
   */
  void handle(Stop stop);

  /**
   * Handles the given stop time.
   *
   * @param stopTime The stop time to handle
   */
  void handle(StopTime stopTime);

  /**
   * Handles the given transfer.
   *
   * @param transfer The transfer to handle
   */
  void handle(Transfer transfer);

  /**
   * Handles the given trip.
   *
   * @param trip The trip to handle
   */
  void handle(Trip trip);
}
