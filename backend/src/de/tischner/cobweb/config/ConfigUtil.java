package de.tischner.cobweb.config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class that provides common methods and values used for dealing with
 * configuration files. Such as name of a key and default values.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
final class ConfigUtil {
  /**
   * Name of the key that stores the path to the database info object.
   */
  final static String KEY_DB_INFO = "dbInfo";
  /**
   * Name of the key that stores the path to the graph cache.
   */
  final static String KEY_GRAPH_CACHE = "graphCache";
  /**
   * Name of the key that stores the path to the graph cache info object.
   */
  final static String KEY_GRAPH_CACHE_INFO = "graphCacheInfo";
  /**
   * Name of the key that stores the path to the directory where all GTFS input
   * data are stored.
   */
  final static String KEY_GTFS_DIRECTORY = "gtfsDirectory";
  /**
   * Name of the key that stores the path to the SQL script to execute when
   * initializing the external database.
   */
  final static String KEY_INIT_DB_SCRIPT = "initDbScript";
  /**
   * Name of the key that stores the JDBC URL to use when connecting to the
   * external database.
   */
  final static String KEY_JDBC_URL = "jdbcUrl";
  /**
   * Name of the key that stores the path to the directory where all OSM input
   * data are stored.
   */
  final static String KEY_OSM_DIRECTORY = "osmDirectory";
  /**
   * Name of the key that stores the path to the filter file used to filter road
   * ways in OSM data.
   */
  final static String KEY_OSM_ROAD_FILTER = "osmRoadFilter";
  /**
   * Name of the key that stores the port the routing server should use.
   */
  final static String KEY_ROUTING_SERVER_PORT = "routingServerPort";
  /**
   * Name of the key that stores whether the external or an internal in-memory
   * database should be used.
   */
  final static String KEY_USE_EXTERNAL_DB = "useExternalDb";
  /**
   * Name of the key that stores whether or not the graph cache should be used.
   */
  final static String KEY_USE_GRAPH_CACHE = "useGraphCache";
  /**
   * Default path to the database info object.
   */
  final static Path VALUE_DB_INFO = Paths.get("backend", "res", "cache", "db", "db.info");
  /**
   * Default path to the graph cache.
   */
  final static Path VALUE_GRAPH_CACHE = Paths.get("backend", "res", "cache", "graph", "graphCache.ser");
  /**
   * Default path to the graph cache info object.
   */
  final static Path VALUE_GRAPH_CACHE_INFO = Paths.get("backend", "res", "cache", "graph", "graphCache.info");
  /**
   * Default path to the directory that contains all GTFS data.
   */
  final static Path VALUE_GTFS_DIRECTORY = Paths.get("backend", "res", "input", "gtfs");
  /**
   * Default path to the SQL script that is executed when initializing the
   * external database.
   */
  final static Path VALUE_INIT_DB_SCRIPT = Paths.get("backend", "res", "cache", "db", "initDb.sql");
  /**
   * Default JDBC URL to use when connecting to the external database.
   */
  final static String VALUE_JDBC_URL = "jdbc:sqlite:backend/res/cache/db/routing.db";
  /**
   * Default path to the directory that contains all OSM data.
   */
  final static Path VALUE_OSM_DIRECTORY = Paths.get("backend", "res", "input", "osm");
  /**
   * Default path to the filter file used to filter road ways in OSM data.
   */
  final static Path VALUE_OSM_ROAD_FILTER = Paths.get("backend", "res", "filter", "osm", "road.filter");
  /**
   * Default port to use by the routing server.
   */
  final static int VALUE_ROUTING_SERVER_PORT = 845;
  /**
   * Whether an external or an internal in-memory database should be used.
   */
  final static boolean VALUE_USE_EXTERNAL_DB = true;
  /**
   * Whether or not the graph cache should be used.
   */
  final static boolean VALUE_USE_GRAPH_CACHE = true;

  /**
   * Utility class. No implementation.
   */
  private ConfigUtil() {

  }
}
