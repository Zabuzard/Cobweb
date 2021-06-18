package de.unifreiburg.informatik.cobweb.parsing;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unifreiburg.informatik.cobweb.config.IParseConfigProvider;
import de.unifreiburg.informatik.cobweb.parsing.gtfs.GtfsParser;
import de.unifreiburg.informatik.cobweb.parsing.gtfs.IGtfsFileHandler;
import de.unifreiburg.informatik.cobweb.parsing.osm.IOsmFileHandler;
import de.unifreiburg.informatik.cobweb.parsing.osm.OsmParser;

/**
 * Class used to parse data like GTFS and OSM files using a given
 * configuration.<br>
 * <br>
 * Add handlers to the parser using {@link #addOsmHandler(IOsmFileHandler)} and
 * similar methods before parsing the data using {@link #parseData()}. The data
 * parser will then notify all registered handlers.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class DataParser {
  /**
   * Logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(DataParser.class);
  /**
   * Configuration that provides paths for the data to parse.
   */
  private final IParseConfigProvider mConfig;
  /**
   * Collection of files that contain the files to parse or <code>null</code> if the
   * directory set in the configuration file should be used.
   */
  private final Collection<Path> mFiles;
  /**
   * Currently registered GTFS handler.
   */
  private final Collection<IGtfsFileHandler> mGtfsHandler;
  /**
   * Currently registered OSM handler.
   */
  private final Collection<IOsmFileHandler> mOsmHandler;
  /**
   * <code>True</code> if the parser is used for the reducer command, will use a
   * different configuration then.
   */
  private final boolean mUseReducerConfiguration;

  /**
   * Creates a new data parser using the given configuration.
   *
   * @param config The configuration provider to use
   * @param files  Collection of files that contain the files to parse or
   *               <code>null</code> if the directory set in the configuration file
   *               should be used.
   */
  public DataParser(final IParseConfigProvider config, final Collection<Path> files) {
    this(config, files, false);
  }

  /**
   * Creates a new data parser using the given configuration.
   *
   * @param config                  The configuration provider to use
   * @param files                   Collection of files that contain the files
   *                                to parse or <code>null</code> if the directory
   *                                set in the configuration file should be
   *                                used.
   * @param useReducerConfiguration <code>True</code> if the parser is used for the
   *                                reducer command, will use a different
   *                                configuration then
   */
  public DataParser(final IParseConfigProvider config, final Collection<Path> files,
      final boolean useReducerConfiguration) {
    mConfig = config;
    mFiles = files;
    mUseReducerConfiguration = useReducerConfiguration;
    mOsmHandler = new ArrayList<>();
    mGtfsHandler = new ArrayList<>();
  }

  /**
   * Registers the given GTFS handler. The handler will be notified when parsing
   * GTFS data using the {@link #parseData()} method.
   *
   * @param handler The handler to register
   */
  public void addGtfsHandler(final IGtfsFileHandler handler) {
    mGtfsHandler.add(handler);
  }

  /**
   * Registers the given OSM handler. The handler will be notified when parsing
   * OSM data using the {@link #parseData()} method.
   *
   * @param handler The handler to register
   */
  public void addOsmHandler(final IOsmFileHandler handler) {
    mOsmHandler.add(handler);
  }

  /**
   * Unregisters all registered handler from this parser.
   */
  public void clearHandler() {
    mOsmHandler.clear();
    mGtfsHandler.clear();
  }

  /**
   * Parses all data given by the configuration provider, like OSM and GTFS
   * data. Notifies all registered handler when parsing elements.<br>
   * <br>
   * Use methods like {@link #addOsmHandler(IOsmFileHandler)} to register a
   * handler.
   *
   * @throws ParseException If a parse exception occurred while parsing the data
   */
  public void parseData() throws ParseException {
    LOGGER.info("Parsing data");
    // Parse OSM data
    if (!mOsmHandler.isEmpty()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Parsing OSM data for {} handler", mOsmHandler.size());
      }

      // Decide whether to use the directory set in the configuration or a given
      // collection of files instead
      final Path directoryToUse;
      final Collection<Path> filesToUse;
      if (mFiles != null) {
        directoryToUse = null;
        filesToUse = mFiles;
      } else {
        directoryToUse = mConfig.getOsmDirectory();
        filesToUse = null;
      }
      // Choose the right configuration
      final OsmParser osmParser;
      if (mUseReducerConfiguration) {
        osmParser = new OsmParser(directoryToUse, filesToUse, mOsmHandler, true, true);
      } else {
        osmParser = new OsmParser(directoryToUse, filesToUse, mOsmHandler);
      }

      osmParser.parseOsmFiles();
    } else if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Skipping parsing OSM data since no handler are registered");
    }

    // Parse GTFS data
    if (!mGtfsHandler.isEmpty()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Parsing GTFS data for {} handler", mGtfsHandler.size());
      }

      // Decide whether to use the directory set in the configuration or a given
      // collection of files instead
      final Path directoryToUse;
      final Collection<Path> filesToUse;
      if (mFiles != null) {
        directoryToUse = null;
        filesToUse = mFiles;
      } else {
        directoryToUse = mConfig.getGtfsDirectory();
        filesToUse = null;
      }
      // Choose the right configuration
      final GtfsParser gtfsParser = new GtfsParser(directoryToUse, filesToUse, mGtfsHandler);

      gtfsParser.parseGtfsFiles();
    } else if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Skipping parsing GTFS data since no handler are registered");
    }
  }

  /**
   * Unregisters the given GTFS handler. The handler will not be notified
   * anymore when parsing GTFS data using the {@link #parseData()} method.
   *
   * @param handler The handler to unregister
   */
  public void removeGtfsHandler(final IGtfsFileHandler handler) {
    mGtfsHandler.remove(handler);
  }

  /**
   * Unregisters the given OSM handler. The handler will not be notified anymore
   * when parsing OSM data using the {@link #parseData()} method.
   *
   * @param handler The handler to unregister
   */
  public void removeOsmHandler(final IOsmFileHandler handler) {
    mOsmHandler.remove(handler);
  }
}
