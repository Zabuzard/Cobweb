package de.tischner.cobweb.parsing.osm;

import de.tischner.cobweb.parsing.IFileHandler;
import de.topobyte.osm4j.core.access.OsmHandler;

/**
 * Interface for OSM handler that can accept or reject given files.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IOsmFileHandler extends OsmHandler, IFileHandler {
  // The interface is currently empty and serves as combination of several
  // interfaces
}
