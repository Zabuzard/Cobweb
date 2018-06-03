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

public interface IGtfsHandler extends IBaseEntityHandler {
  void handle(Agency agency);

  void handle(FareAttribute fareAttribute);

  void handle(FareRule fareRule);

  void handle(FeedInfo feedInfo);

  void handle(Frequency frequency);

  void handle(Pathway pathway);

  void handle(Route route);

  void handle(ServiceCalendar serviceCalendar);

  void handle(ServiceCalendarDate serviceCalendarDate);

  void handle(ShapePoint shapePoint);

  void handle(Stop stop);

  void handle(StopTime stopTime);

  void handle(Transfer transfer);

  void handle(Trip trip);
}
