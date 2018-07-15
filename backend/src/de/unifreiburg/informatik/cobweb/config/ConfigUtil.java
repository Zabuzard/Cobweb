package de.unifreiburg.informatik.cobweb.config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class that provides common methods and values used for dealing with
 * configuration files. Such as name of a key and default values.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
final class ConfigUtil {
  /**
   * Name of the key that stores the path to the SQL script to execute when
   * cleaning the external database.
   */
  static final String KEY_CLEAN_DB_SCRIPT = "cleanDbScript";
  /**
   * Name of the key that stores the path to the database info object.
   */
  static final String KEY_DB_INFO = "dbInfo";
  /**
   * Name of the key that stores the path to the graph cache.
   */
  static final String KEY_GRAPH_CACHE = "graphCache";
  /**
   * Name of the key that stores the path to the graph cache info object.
   */
  static final String KEY_GRAPH_CACHE_INFO = "graphCacheInfo";
  /**
   * Name of the key that stores the path to the directory where all GTFS input
   * data are stored.
   */
  static final String KEY_GTFS_DIRECTORY = "gtfsDirectory";
  /**
   * Name of the key that stores the path to the SQL script to execute when
   * initializing the external database.
   */
  static final String KEY_INIT_DB_SCRIPT = "initDbScript";
  /**
   * Name of the key that stores the JDBC URL to use when connecting to the
   * external database.
   */
  static final String KEY_JDBC_URL = "jdbcUrl";
  /**
   * Name of the key that stores the maximal amount of matches the name search
   * server should send.
   */
  static final String KEY_NAME_SEARCH_SERVER_MATCH_LIMIT = "nameSearchServerMatchLimit";
  /**
   * Name of the key that stores the port the name search server should use.
   */
  static final String KEY_NAME_SEARCH_SERVER_PORT = "nameSearchServerPort";
  /**
   * Name of the key that stores the port the nearest search server should use.
   */
  static final String KEY_NEAREST_SEARCH_SERVER_PORT = "nearestSearchServerPort";
  /**
   * Name of the key that stores the path to the directory where all OSM input
   * data are stored.
   */
  static final String KEY_OSM_DIRECTORY = "osmDirectory";
  /**
   * Name of the key that stores the path to the filter file used to filter road
   * ways in OSM data.
   */
  static final String KEY_OSM_ROAD_FILTER = "osmRoadFilter";
  /**
   * Name of the key that stores the mode to use for the routing model.
   */
  static final String KEY_ROUTING_MODEL_MODE = "routingModelMode";
  /**
   * Name of the key that stores the port the routing server should use.
   */
  static final String KEY_ROUTING_SERVER_PORT = "routingServerPort";
  /**
   * Name of the key that stores whether the external or an internal in-memory
   * database should be used.
   */
  static final String KEY_USE_EXTERNAL_DB = "useExternalDb";
  /**
   * Name of the key that stores whether or not the graph cache should be used.
   */
  static final String KEY_USE_GRAPH_CACHE = "useGraphCache";
  /**
   * Default path to the SQL script that is executed when cleaning the external
   * database.
   */
  static final Path VALUE_CLEAN_DB_SCRIPT = Paths.get("res", "cache", "db", "scripts", "cleanDb.sql");
  /**
   * Default path to the database info object.
   */
  static final Path VALUE_DB_INFO = Paths.get("res", "cache", "db", "db.info");
  /**
   * Default path to the graph cache.
   */
  static final Path VALUE_GRAPH_CACHE = Paths.get("res", "cache", "graph", "graphCache.ser");
  /**
   * Default path to the graph cache info object.
   */
  static final Path VALUE_GRAPH_CACHE_INFO = Paths.get("res", "cache", "graph", "graphCache.info");
  /**
   * Default path to the directory that contains all GTFS data.
   */
  static final Path VALUE_GTFS_DIRECTORY = Paths.get("res", "input", "gtfs");
  /**
   * Default path to the SQL script that is executed when initializing the
   * external database.
   */
  static final Path VALUE_INIT_DB_SCRIPT = Paths.get("res", "cache", "db", "scripts", "initDb.sql");
  /**
   * Default JDBC URL to use when connecting to the external database.
   */
  static final String VALUE_JDBC_URL = "jdbc:sqlite:res/cache/db/routing.db";
  /**
   * Default maximal amount of matches the name search server sends.
   */
  static final int VALUE_NAME_SEARCH_SERVER_MATCH_LIMIT = 1_000;
  /**
   * Default port to use by the name search server.
   */
  static final int VALUE_NAME_SEARCH_SERVER_PORT = 2846;
  /**
   * Default port to use by the nearest search server.
   */
  static final int VALUE_NEAREST_SEARCH_SERVER_PORT = 2847;
  /**
   * Default path to the directory that contains all OSM data.
   */
  static final Path VALUE_OSM_DIRECTORY = Paths.get("res", "input", "osm");
  /**
   * Default path to the filter file used to filter road ways in OSM data.
   */
  static final Path VALUE_OSM_ROAD_FILTER = Paths.get("res", "filter", "osm", "road.filter");
  /**
   * The default mode to use for the routing model.
   */
  static final String VALUE_ROUTING_MODEL_MODE = "GRAPH_WITH_TIMETABLE";
  /**
   * Default port to use by the routing server.
   */
  static final int VALUE_ROUTING_SERVER_PORT = 2845;
  /**
   * Whether an external or an internal in-memory database should be used.
   */
  static final boolean VALUE_USE_EXTERNAL_DB = true;
  /**
   * Whether or not the graph cache should be used.
   */
  static final boolean VALUE_USE_GRAPH_CACHE = true;

  /**
   * Utility class. No implementation.
   */
  private ConfigUtil() {

  }
}
