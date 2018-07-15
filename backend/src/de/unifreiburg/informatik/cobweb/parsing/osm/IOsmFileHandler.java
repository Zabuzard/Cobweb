package de.unifreiburg.informatik.cobweb.parsing.osm;

import de.topobyte.osm4j.core.access.OsmHandler;
import de.unifreiburg.informatik.cobweb.parsing.IFileHandler;

/**
 * Interface for OSM handler that can accept or reject given files.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IOsmFileHandler extends OsmHandler, IFileHandler {
  // The interface is currently empty and serves as combination of several
  // interfaces
}
