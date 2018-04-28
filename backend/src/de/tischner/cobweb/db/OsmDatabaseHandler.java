package de.tischner.cobweb.db;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

public final class OsmDatabaseHandler implements IOsmFileHandler {
  private static final int BUFFER_SIZE = 100_000;
  private int mBufferIndex;
  private final IRoutingDatabase mDatabase;
  private final OsmEntity[] mEntityBuffer;

  public OsmDatabaseHandler(final IRoutingDatabase database) {
    mEntityBuffer = new OsmEntity[BUFFER_SIZE];
    mDatabase = database;
    mBufferIndex = 0;
  }

  @Override
  public boolean acceptFile(final Path file) {
    // TODO Check cache to see which files are needed
    // We are interested in all OSM files
    return true;
  }

  @Override
  public void complete() throws IOException {
    // Submit buffer
    offerBuffer();
  }

  @Override
  public void handle(final OsmBounds bounds) throws IOException {
    // Ignore
  }

  @Override
  public void handle(final OsmNode node) throws IOException {
    handleEntity(node);
  }

  @Override
  public void handle(final OsmRelation relation) throws IOException {
    handleEntity(relation);
  }

  @Override
  public void handle(final OsmWay way) throws IOException {
    handleEntity(way);
  }

  private void handleEntity(final OsmEntity entity) {
    // If buffer is full, offer it
    if (mBufferIndex >= mEntityBuffer.length) {
      offerBuffer();
    }

    // Collect the entity, index has changed due to offer
    mEntityBuffer[mBufferIndex] = entity;

    // Increase the index
    mBufferIndex++;
  }

  private void offerBuffer() {
    // Offer all items up to the current index
    final int size = mBufferIndex + 1;
    mDatabase.offerOsmEntities(Arrays.stream(mEntityBuffer).limit(size), size);

    // Reset index since buffer is empty again
    mBufferIndex = 0;
  }

}
