package de.tischner.cobweb.parsing.osm;

import java.io.IOException;

import de.topobyte.osm4j.core.access.OsmHandler;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

public final class OsmHandlerForwarder implements OsmHandler {

  private final Iterable<? extends OsmHandler> mAllHandler;

  public OsmHandlerForwarder(final Iterable<? extends OsmHandler> allHandler) {
    mAllHandler = allHandler;
  }

  @Override
  public void complete() throws IOException {
    for (final OsmHandler handler : mAllHandler) {
      handler.complete();
    }
  }

  @Override
  public void handle(final OsmBounds bounds) throws IOException {
    for (final OsmHandler handler : mAllHandler) {
      handler.handle(bounds);
    }
  }

  @Override
  public void handle(final OsmNode node) throws IOException {
    for (final OsmHandler handler : mAllHandler) {
      handler.handle(node);
    }
  }

  @Override
  public void handle(final OsmRelation relation) throws IOException {
    for (final OsmHandler handler : mAllHandler) {
      handler.handle(relation);
    }
  }

  @Override
  public void handle(final OsmWay way) throws IOException {
    for (final OsmHandler handler : mAllHandler) {
      handler.handle(way);
    }
  }

}
