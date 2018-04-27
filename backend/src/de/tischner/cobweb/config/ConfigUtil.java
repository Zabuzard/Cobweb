package de.tischner.cobweb.config;

import java.nio.file.Path;
import java.nio.file.Paths;

final class ConfigUtil {
  final static String KEY_GRAPH_CACHE = "graphCache";

  final static String KEY_GTFS_DIRECTORY = "gtfsDirectory";
  final static String KEY_OSM_DIRECTORY = "osmDirectory";
  final static String KEY_ROUTING_SERVER_PORT = "routingServerPort";
  final static String KEY_USE_GRAPH_CACHE = "useGraphCache";
  final static Path VALUE_GRAPH_CACHE = Paths.get("backend", "res", "cache");
  final static Path VALUE_GTFS_DIRECTORY = Paths.get("backend", "res", "gtfs");
  final static Path VALUE_OSM_DIRECTORY = Paths.get("backend", "res", "osm");
  final static int VALUE_ROUTING_SERVER_PORT = 845;
  final static boolean VALUE_USE_GRAPH_CACHE = true;

  /**
   * Utility class. No implementation.
   */
  private ConfigUtil() {

  }
}
