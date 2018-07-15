package de.unifreiburg.informatik.cobweb.parsing.gtfs;

import de.unifreiburg.informatik.cobweb.parsing.IFileHandler;

/**
 * Interface for GTFS handler that can accept or reject given files.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IGtfsFileHandler extends IGtfsHandler, IFileHandler {
  // The interface is currently empty and serves as combination of several
  // interfaces
}
