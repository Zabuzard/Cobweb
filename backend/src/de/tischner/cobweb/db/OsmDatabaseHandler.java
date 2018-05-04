package de.tischner.cobweb.db;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.IDatabaseConfigProvider;
import de.tischner.cobweb.parsing.RecentHandler;
import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

public final class OsmDatabaseHandler implements IOsmFileHandler {
  private static final int BUFFER_SIZE = 100_000;
  private final static Logger LOGGER = LoggerFactory.getLogger(OsmDatabaseHandler.class);
  private int mBufferIndex;
  private final IRoutingDatabase mDatabase;
  private final OsmEntity[] mEntityBuffer;
  private final RecentHandler mRecentHandler;
  private final boolean mUseExternalDb;

  public OsmDatabaseHandler(final IRoutingDatabase database, final IDatabaseConfigProvider config) throws IOException {
    mEntityBuffer = new OsmEntity[BUFFER_SIZE];
    mDatabase = database;

    mUseExternalDb = config.useExternalDb();
    if (mUseExternalDb) {
      mRecentHandler = new RecentHandler(config.getDbInfo());
    } else {
      mRecentHandler = null;
    }
  }

  @Override
  public boolean acceptFile(final Path file) {
    // Check if the files content is not already included in the database
    if (mUseExternalDb && !mRecentHandler.acceptFile(file)) {
      return false;
    }

    // Accept all OSM files
    LOGGER.info("Accepts file {}", file);
    return true;
  }

  @Override
  public void complete() throws IOException {
    // Submit buffer
    offerBuffer();

    if (mUseExternalDb) {
      mRecentHandler.updateInfo();
    }
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
    final int size = mBufferIndex;
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Offering buffer of size: {}", size);
    }
    mDatabase.offerOsmEntities(Arrays.stream(mEntityBuffer).limit(size), size);

    // Reset index since buffer is empty again
    mBufferIndex = 0;
  }

}
