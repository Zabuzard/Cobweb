package de.tischner.cobweb.parsing.osm;

import java.io.IOException;

import de.topobyte.osm4j.core.access.OsmHandler;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

/**
 * OSM handler which forwards OSM entities to a collection of given handler.<br>
 * <br>
 * This is needed since the OSM API does not support adding multiple handlers.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class OsmHandlerForwarder implements OsmHandler {
  /**
   * The handler to forward OSM entities to.
   */
  private final Iterable<? extends OsmHandler> mAllHandler;

  /**
   * Creates a new OSM handler which forwards all OSM entities to the given
   * handler.
   *
   * @param allHandler The handler to forward entities to
   */
  public OsmHandlerForwarder(final Iterable<? extends OsmHandler> allHandler) {
    mAllHandler = allHandler;
  }

  /**
   * Forwards the method to all given handler.
   */
  @Override
  public void complete() throws IOException {
    for (final OsmHandler handler : mAllHandler) {
      handler.complete();
    }
  }

  /**
   * Forwards the OSM entity to all given handler.
   */
  @Override
  public void handle(final OsmBounds bounds) throws IOException {
    for (final OsmHandler handler : mAllHandler) {
      handler.handle(bounds);
    }
  }

  /**
   * Forwards the OSM entity to all given handler.
   */
  @Override
  public void handle(final OsmNode node) throws IOException {
    for (final OsmHandler handler : mAllHandler) {
      handler.handle(node);
    }
  }

  /**
   * Forwards the OSM entity to all given handler.
   */
  @Override
  public void handle(final OsmRelation relation) throws IOException {
    for (final OsmHandler handler : mAllHandler) {
      handler.handle(relation);
    }
  }

  /**
   * Forwards the OSM entity to all given handler.
   */
  @Override
  public void handle(final OsmWay way) throws IOException {
    for (final OsmHandler handler : mAllHandler) {
      handler.handle(way);
    }
  }

}
