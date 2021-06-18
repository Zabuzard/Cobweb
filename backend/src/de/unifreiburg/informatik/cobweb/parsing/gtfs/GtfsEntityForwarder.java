package de.unifreiburg.informatik.cobweb.parsing.gtfs;

import org.onebusaway.csv_entities.EntityHandler;
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
 * Implementation of a {@link EntityHandler} which extracts the type of the
 * <code>GTFS</code> entity and forwards the entities to the appropriate methods of
 * the {@link IGtfsHandler} interface.<br>
 * <br>
 * Primarily meant for subclasses to ease access to the entities.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public abstract class GtfsEntityForwarder implements IGtfsHandler {

  @Override
  public void handleEntity(final Object entity) throws IllegalArgumentException {
    // Order for most likely distribution, most common types first
    if (entity instanceof StopTime) {
      handle((StopTime) entity);
    } else if (entity instanceof ShapePoint) {
      handle((ShapePoint) entity);
    } else if (entity instanceof ServiceCalendarDate) {
      handle((ServiceCalendarDate) entity);
    } else if (entity instanceof Stop) {
      handle((Stop) entity);
    } else if (entity instanceof ServiceCalendar) {
      handle((ServiceCalendar) entity);
    } else if (entity instanceof Route) {
      handle((Route) entity);
    } else if (entity instanceof Trip) {
      handle((Trip) entity);
    } else if (entity instanceof FareAttribute) {
      handle((FareAttribute) entity);
    } else if (entity instanceof FareRule) {
      handle((FareRule) entity);
    } else if (entity instanceof Frequency) {
      handle((Frequency) entity);
    } else if (entity instanceof Pathway) {
      handle((Pathway) entity);
    } else if (entity instanceof Transfer) {
      handle((Transfer) entity);
    } else if (entity instanceof Agency) {
      handle((Agency) entity);
    } else if (entity instanceof FeedInfo) {
      handle((FeedInfo) entity);
    } else {
      throw new IllegalArgumentException();
    }
  }

}
