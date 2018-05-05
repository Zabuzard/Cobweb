package de.tischner.cobweb.parsing;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.IParseConfigProvider;
import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.tischner.cobweb.parsing.osm.OsmParser;

/**
 * Class used to parse data like GTFS and OSM files using a given
 * configuration.<br>
 * <br>
 * Add handlers to the parser using {@link #addOsmHandler(IOsmFileHandler)} and
 * similar methods before parsing the data using {@link #parseData()}. The data
 * parser will then notify all registered handlers.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class DataParser {
  /**
   * Logger to use for logging.
   */
  private final static Logger LOGGER = LoggerFactory.getLogger(DataParser.class);
  /**
   * Configuration that provides paths for the data to parse.
   */
  private final IParseConfigProvider mConfig;
  /**
   * Currently registered OSM handler.
   */
  private final Collection<IOsmFileHandler> mOsmHandler;

  /**
   * Creates a new data parser using the given configuration.
   *
   * @param config The configuration provider to use
   */
  public DataParser(final IParseConfigProvider config) {
    mConfig = config;
    mOsmHandler = new ArrayList<>();
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
   * Parses all data given by the configuration provider, like OSM and GTFS data.
   * Notifies all registered handler when parsing elements.<br>
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

      final Path osmDirectory = mConfig.getOsmDirectory();
      final OsmParser osmParser = new OsmParser(osmDirectory, mOsmHandler);
      osmParser.parseOsmFiles();
    } else if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Skipping parsing OSM data since no handler are registered");
    }
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
