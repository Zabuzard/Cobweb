package de.tischner.cobweb.config;

import java.nio.file.Path;
import java.nio.file.Paths;

final class ConfigUtil {
  final static String KEY_DB_INFO = "dbInfo";
  final static String KEY_GRAPH_CACHE = "graphCache";
  final static String KEY_GRAPH_CACHE_INFO = "graphCacheInfo";
  final static String KEY_GTFS_DIRECTORY = "gtfsDirectory";
  final static String KEY_INIT_DB_SCRIPT = "initDbScript";
  final static String KEY_JDBC_URL = "jdbcUrl";
  final static String KEY_OSM_DIRECTORY = "osmDirectory";
  final static String KEY_OSM_ROAD_FILTER = "osmRoadFilter";
  final static String KEY_ROUTING_SERVER_PORT = "routingServerPort";
  final static String KEY_USE_EXTERNAL_DB = "useExternalDb";
  final static String KEY_USE_GRAPH_CACHE = "useGraphCache";
  final static Path VALUE_DB_INFO = Paths.get("backend", "res", "cache", "db", "db.info");
  final static Path VALUE_GRAPH_CACHE = Paths.get("backend", "res", "cache", "graph", "graphCache.ser");
  final static Path VALUE_GRAPH_CACHE_INFO = Paths.get("backend", "res", "cache", "graph", "graphCache.info");
  final static Path VALUE_GTFS_DIRECTORY = Paths.get("backend", "res", "input", "gtfs");
  final static Path VALUE_INIT_DB_SCRIPT = Paths.get("backend", "res", "cache", "db", "initDb.sql");
  final static String VALUE_JDBC_URL = "jdbc:sqlite:backend/res/cache/db/routing.db";
  final static Path VALUE_OSM_DIRECTORY = Paths.get("backend", "res", "input", "osm");
  final static Path VALUE_OSM_ROAD_FILTER = Paths.get("backend", "res", "filter", "osm", "road.filter");
  final static int VALUE_ROUTING_SERVER_PORT = 845;
  final static boolean VALUE_USE_EXTERNAL_DB = true;
  final static boolean VALUE_USE_GRAPH_CACHE = true;

  /**
   * Utility class. No implementation.
   */
  private ConfigUtil() {

  }
}
