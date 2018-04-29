package de.tischner.cobweb.parsing;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.IParseConfigProvider;
import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.tischner.cobweb.parsing.osm.OsmParser;

public final class DataParser {
  private final static Logger LOGGER = LoggerFactory.getLogger(DataParser.class);
  private final IParseConfigProvider mConfig;
  private final Collection<IOsmFileHandler> mOsmHandler;

  public DataParser(final IParseConfigProvider config) {
    mConfig = config;
    mOsmHandler = new ArrayList<>();
  }

  public void addOsmHandler(final IOsmFileHandler handler) {
    mOsmHandler.add(handler);
  }

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

  public void removeOsmHandler(final IOsmFileHandler handler) {
    mOsmHandler.remove(handler);
  }
}
