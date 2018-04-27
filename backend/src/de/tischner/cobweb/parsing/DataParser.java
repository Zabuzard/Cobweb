package de.tischner.cobweb.parsing;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import de.tischner.cobweb.config.IParseConfigProvider;
import de.tischner.cobweb.parsing.osm.IOsmFileHandler;
import de.tischner.cobweb.parsing.osm.OsmParser;

public final class DataParser {
  private final IParseConfigProvider mConfig;
  private final Set<IOsmFileHandler> mOsmHandler;

  public DataParser(final IParseConfigProvider config) {
    mConfig = config;
    mOsmHandler = new HashSet<>();
  }

  public void addOsmHandler(final IOsmFileHandler handler) {
    mOsmHandler.add(handler);
  }

  public void parseData() throws ParseException {
    // Parse OSM data
    if (!mOsmHandler.isEmpty()) {
      final Path osmDirectory = mConfig.getOsmDirectory();
      final OsmParser osmParser = new OsmParser(osmDirectory, mOsmHandler);
      osmParser.parseOsmFiles();
    }
  }

  public void removeOsmHandler(final IOsmFileHandler handler) {
    mOsmHandler.remove(handler);
  }
}
