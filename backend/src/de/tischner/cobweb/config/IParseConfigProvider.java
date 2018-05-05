package de.tischner.cobweb.config;

import java.nio.file.Path;

/**
 * Interface for classes that provide parsing related configuration settings.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public interface IParseConfigProvider {
  /**
   * Gets the path to the directory that contains all GTFS data.
   *
   * @return The path to the GTFS directory
   */
  Path getGtfsDirectory();

  /**
   * Gets the path to the directory that contains all OSM data.
   *
   * @return The path to the OSM data
   */
  Path getOsmDirectory();
}
