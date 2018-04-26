package de.tischner.cobweb.parsing;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import de.tischner.cobweb.parsing.osm.AOsmFileHandler;
import de.tischner.cobweb.parsing.osm.OsmParser;

public final class DataParser {
  private final ParseConfig mConfig;
  private final Set<AOsmFileHandler> mOsmHandler;

  public DataParser(final ParseConfig config) {
    mConfig = config;
    mOsmHandler = new HashSet<>();
  }

  public void addOsmHandler(final AOsmFileHandler handler) {
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

  public void removeOsmHandler(final AOsmFileHandler handler) {
    mOsmHandler.remove(handler);
  }
}
