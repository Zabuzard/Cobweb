package de.tischner.cobweb.parsing.osm;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.impl.factory.primitive.LongSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

/**
 * OSM handler which is able to reduce the given OSM file according to a given
 * filter.<br>
 * <br>
 * The reducer expects that each file which is to be reduced is streamed twice,
 * directly after each other. In the first pass it will collect information
 * about which entities to keep, based on the given filter. In the second pass
 * it will write entities to a new file next to unreduced file.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class OsmReducer implements IOsmFileHandler {
  /**
   * Amount after how many parsed entities a message should be logged.
   */
  private static final int LOG_EVERY_ENTITY_AMOUNT = 1_000_000;
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(OsmReducer.class);
  /**
   * The default locale of the system.
   */
  private final Locale mDefaultLocale;
  /**
   * The amount of entities already parsed for the current pass.
   */
  private int mEntityAmount;
  /**
   * The path of the file currently streamed.
   */
  private Path mFile;
  /**
   * The filter to use for filtering ways.
   */
  private final IOsmFilter mFilter;
  /**
   * Whether or not the file currently streamed is streamed for the first time.
   */
  private boolean mFirstPass;
  /**
   * Set containing the OSM IDs of the nodes to keep for the currently streamed
   * file.
   */
  private MutableLongSet mNodeIdsToKeep;
  /**
   * The output stream to use for writing the reduced file to, only valid in the
   * second pass. Will be closed after usage.
   */
  private OutputStream mOutputStream;
  /**
   * The writer to use for writing the reduced file, only valid in the second
   * pass.
   */
  private OsmOutputStream mWriter;

  /**
   * Creates a new OSM reducer with the given way filter.
   *
   * @param wayFilter The filter to use for filtering ways. The filter is
   *                  ignored for other entities.
   */
  public OsmReducer(final IOsmFilter wayFilter) {
    mFilter = wayFilter;
    mFirstPass = true;
    mDefaultLocale = Locale.getDefault();
  }

  /*
   * (non-Javadoc)
   * @see de.topobyte.osm4j.core.access.OsmHandler#complete()
   */
  @Override
  public void complete() throws IOException {
    if (mFirstPass) {
      // Prepare second pass
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Second pass for: " + mFile);
      }
      final String reducedName = OsmParser.REDUCED_PREFIX + mFile.getFileName();
      final Path outputPath = mFile.getParent().resolve(reducedName);
      // We need to use the PrintWriter constructor else we will not be able to
      // write in UTF-8 with this OSM library
      try {
        mOutputStream = Files.newOutputStream(outputPath);
        mWriter = new OsmXmlOutputStream(new PrintWriter(new OutputStreamWriter(mOutputStream, StandardCharsets.UTF_8)),
            false);
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
      // Make sure everything is written in the expected format since the
      // OSM library doesn't handle it by itself
      Locale.setDefault(Locale.US);
    } else {
      // End second pass
      mWriter.complete();
      Locale.setDefault(mDefaultLocale);
      mOutputStream.close();
    }

    mFirstPass = !mFirstPass;
    mEntityAmount = 0;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model.iface.OsmBounds)
   */
  @Override
  public void handle(final OsmBounds bounds) throws IOException {
    handleEntityCallback();
    if (mFirstPass) {
      return;
    }
    mWriter.write(bounds);
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model.iface.OsmNode)
   */
  @Override
  public void handle(final OsmNode node) throws IOException {
    handleEntityCallback();
    if (mFirstPass) {
      return;
    }
    if (mNodeIdsToKeep.contains(node.getId())) {
      mWriter.write(node);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model.iface.OsmRelation)
   */
  @Override
  public void handle(final OsmRelation relation) throws IOException {
    handleEntityCallback();
    // Do not write any relations at all
  }

  /*
   * (non-Javadoc)
   * @see
   * de.topobyte.osm4j.core.access.OsmHandler#handle(de.topobyte.osm4j.core.
   * model.iface.OsmWay)
   */
  @Override
  public void handle(final OsmWay way) throws IOException {
    handleEntityCallback();
    // Ignore the way if it does not pass the filter
    if (!mFilter.filter(way)) {
      return;
    }

    if (!mFirstPass) {
      mWriter.write(way);
      return;
    }

    // Collect all nodes belonging to this way
    final int amountOfNodes = way.getNumberOfNodes();
    for (int i = 0; i < amountOfNodes; i++) {
      mNodeIdsToKeep.add(way.getNodeId(i));
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.parsing.IFileHandler#isAcceptingFile(java.nio.file.Path)
   */
  @Override
  public boolean isAcceptingFile(final Path file) {
    // Accept all OSM files
    LOGGER.info("Accepts file {}", file);
    if (mFirstPass) {
      // Prepare first pass
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("First pass for: " + file);
      }
      mNodeIdsToKeep = LongSets.mutable.empty();
      mFile = file;
    }
    return true;
  }

  /**
   * Callback used for every entity that is handled by this handler.
   */
  private void handleEntityCallback() {
    mEntityAmount++;

    if (LOGGER.isDebugEnabled() && mEntityAmount % LOG_EVERY_ENTITY_AMOUNT == 0) {
      LOGGER.debug("Handled {} entities", mEntityAmount);
    }
  }

}
