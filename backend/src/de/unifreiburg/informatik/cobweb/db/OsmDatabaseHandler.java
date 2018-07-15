package de.unifreiburg.informatik.cobweb.db;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.unifreiburg.informatik.cobweb.config.IDatabaseConfigProvider;
import de.unifreiburg.informatik.cobweb.parsing.RecentHandler;
import de.unifreiburg.informatik.cobweb.parsing.osm.IOsmFileHandler;

/**
 * Implementation of {@link IOsmFileHandler} which handles OSM data for a given
 * database. It is able to filter and pre-process data based on a given
 * configuration before it offers the data to the database.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class OsmDatabaseHandler implements IOsmFileHandler {
  /**
   * The size of the entity buffer. If the buffer reaches the limit the buffered
   * entities are pushed to the database.
   */
  private static final int BUFFER_SIZE = 100_000;
  /**
   * Logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(OsmDatabaseHandler.class);
  /**
   * Whether or not parsing of nodes has been finished already.
   */
  private boolean mAreNodesFinished;
  /**
   * The current index to use in the entity buffer. It points to the index where
   * the next element can be inserted. So it is always one greater than the
   * index of the last inserted element. By that it represents the current size
   * of the buffer.
   */
  private int mBufferIndex;
  /**
   * The database to push the OSM data to.
   */
  private final IRoutingDatabase mDatabase;
  /**
   * The buffer to use for buffering entities that are to be pushed to the
   * database. The buffer is used to avoid pushing every element in a single
   * connection to the database.
   */
  private final OsmEntity[] mEntityBuffer;
  /**
   * The handler to use which determines the OSM files that contain more recent
   * or new data than the data already stored in the database. Will only be used
   * if the configuration has set the use of an external database.
   */
  private final RecentHandler mRecentHandler;
  /**
   * Whether or not an external database is to be used. This determines if OSM
   * files should be filtered by a {@link RecentHandler} or not.
   */
  private final boolean mUseExternalDb;

  /**
   * Creates a new OSM database handler which operates on the given database
   * using the given configuration.
   *
   * @param database The database to push OSM data to
   * @param config   The configuration provider
   * @throws IOException If an I/O-Exception occurred while reading
   *                     configuration files
   */
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

  /*
   * (non-Javadoc)
   * @see de.topobyte.osm4j.core.access.OsmHandler#complete()
   */
  @Override
  public void complete() throws IOException {
    // Submit buffer
    offerBuffer();

    if (mUseExternalDb) {
      mRecentHandler.updateInfo();
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model. iface.OsmBounds)
   */
  @Override
  public void handle(final OsmBounds bounds) throws IOException {
    // Ignore
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model. iface.OsmNode)
   */
  @Override
  public void handle(final OsmNode node) throws IOException {
    handleEntity(node);
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model. iface.OsmRelation)
   */
  @Override
  public void handle(final OsmRelation relation) throws IOException {
    handleEntity(relation);
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model. iface.OsmWay)
   */
  @Override
  public void handle(final OsmWay way) throws IOException {
    if (!mAreNodesFinished) {
      // Flush the buffer to ensure database has all nodes
      mAreNodesFinished = true;
      offerBuffer();
    }
    handleEntity(way);
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.parsing.IFileHandler#acceptFile(java.nio.file.Path)
   */
  @Override
  public boolean isAcceptingFile(final Path file) {
    // Check if the files content is not already included in the database
    if (mUseExternalDb && !mRecentHandler.isAcceptingFile(file)) {
      return false;
    }

    // Accept all OSM files
    LOGGER.info("Accepts file {}", file);
    return true;
  }

  /**
   * Handles the given OSM entity. Therefore, it collects the entity to a
   * buffer. If the buffer is full it will be offered to the database in order
   * to push the collected data. Depending on the size of the buffer and the
   * type of database this method may take a while when the buffer is full.
   *
   * @param entity The entity to handle
   */
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

  /**
   * Offers the buffer to the database in order to push the data to the
   * database. Afterwards, the buffer index is reset to implicitly clear the
   * buffer.
   */
  private void offerBuffer() {
    // Offer all items up to the current index
    final int size = mBufferIndex;
    if (size == 0) {
      return;
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Offering buffer of size: {}", size);
    }
    mDatabase.offerOsmEntities(Arrays.stream(mEntityBuffer, 0, size), size);

    // Reset index since buffer is empty again
    mBufferIndex = 0;
  }

}
