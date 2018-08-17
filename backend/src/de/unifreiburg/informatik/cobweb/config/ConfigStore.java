package de.unifreiburg.informatik.cobweb.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unifreiburg.informatik.cobweb.routing.model.ERoutingModelMode;

/**
 * Stores configuration properties for the application. Used together with
 * {@link ConfigLoader} to load and save the configuration from and to a
 * file.<br>
 * <br>
 * The store provides default configuration values for all properties.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ConfigStore implements IConfigProvider, IParseConfigProvider, IRoutingConfigProvider,
    INameSearchConfigProvider, IDatabaseConfigProvider, INearestSearchConfigProvider {
  /**
   * The logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigStore.class);
  /**
   * The default settings as key-value pair.
   */
  private final Map<String, String> mDefaultSettings;
  /**
   * The current settings as key-value pair.
   */
  private final Map<String, String> mSettings;

  /**
   * Creates a configuration store that is initialized with default values.
   */
  public ConfigStore() {
    mDefaultSettings = new HashMap<>();
    setupDefaultSettings();
    // Initially use the default settings
    mSettings = new HashMap<>(mDefaultSettings);
  }

  @Override
  public int getAbortTravelTime() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_ABORT_TRAVEL_TIME));
  }

  @Override
  public int getAccessNodesMaximum() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_ACCESS_NODES_MAXIMUM));
  }

  @Override
  public Map<String, String> getAllSettings() {
    return mSettings;
  }

  @Override
  public int getAmountOfLandmarks() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_AMOUNT_OF_LANDMARKS));
  }

  @Override
  public Path getCleanDbScript() {
    return Paths.get(getSetting(ConfigUtil.KEY_CLEAN_DB_SCRIPT));
  }

  @Override
  public Path getDbInfo() {
    return Paths.get(getSetting(ConfigUtil.KEY_DB_INFO));
  }

  @Override
  public int getFootpathReachability() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_FOOTPATH_REACHABILITY));
  }

  @Override
  public Path getGraphCache() {
    return Paths.get(getSetting(ConfigUtil.KEY_GRAPH_CACHE));
  }

  @Override
  public Path getGraphCacheInfo() {
    return Paths.get(getSetting(ConfigUtil.KEY_GRAPH_CACHE_INFO));
  }

  @Override
  public Path getGtfsDirectory() {
    return Paths.get(getSetting(ConfigUtil.KEY_GTFS_DIRECTORY));
  }

  @Override
  public Path getInitDbScript() {
    return Paths.get(getSetting(ConfigUtil.KEY_INIT_DB_SCRIPT));
  }

  @Override
  public String getJdbcUrl() {
    return getSetting(ConfigUtil.KEY_JDBC_URL);
  }

  @Override
  public int getMatchLimit() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_NAME_SEARCH_SERVER_MATCH_LIMIT));
  }

  @Override
  public int getNameSearchServerPort() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_NAME_SEARCH_SERVER_PORT));
  }

  @Override
  public int getNearestSearchServerPort() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_NEAREST_SEARCH_SERVER_PORT));
  }

  @Override
  public Path getOsmDirectory() {
    return Paths.get(getSetting(ConfigUtil.KEY_OSM_DIRECTORY));
  }

  @Override
  public Path getOsmRoadFilter() {
    return Paths.get(getSetting(ConfigUtil.KEY_OSM_ROAD_FILTER));
  }

  @Override
  public ERoutingModelMode getRoutingModelMode() {
    return ERoutingModelMode.valueOf(getSetting(ConfigUtil.KEY_ROUTING_MODEL_MODE));
  }

  @Override
  public int getRoutingServerPort() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_ROUTING_SERVER_PORT));
  }

  @Override
  public String getSetting(final String key) {
    final String value = mSettings.get(key);
    if (value == null || value.isEmpty()) {
      return getDefaultValue(key);
    }
    return value;
  }

  @Override
  public int getTransferDelay() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_TRANSFER_DELAY));
  }

  /**
   * Resets all settings of the store to their default values.
   */
  public void resetToDefaultValues() {
    mSettings.clear();
    mSettings.putAll(mDefaultSettings);
  }

  @Override
  public void setSetting(final String key, final String value) {
    mSettings.put(key, value);
  }

  @Override
  public boolean useExternalDb() {
    return Boolean.valueOf(getSetting(ConfigUtil.KEY_USE_EXTERNAL_DB));
  }

  @Override
  public boolean useGraphCache() {
    return Boolean.valueOf(getSetting(ConfigUtil.KEY_USE_GRAPH_CACHE));
  }

  /**
   * Gets the default value stored for the given key or <tt>null</tt> if there
   * is no.
   *
   * @param key The key to get the default value for
   * @return The default value or <tt>null</tt>
   */
  private String getDefaultValue(final String key) {
    return mDefaultSettings.get(key);
  }

  /**
   * Initializes the map that holds all default values.
   */
  private void setupDefaultSettings() {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Loading default settings");
    }
    // Database settings
    mDefaultSettings.put(ConfigUtil.KEY_JDBC_URL, ConfigUtil.VALUE_JDBC_URL);
    mDefaultSettings.put(ConfigUtil.KEY_INIT_DB_SCRIPT, ConfigUtil.VALUE_INIT_DB_SCRIPT.toString());
    mDefaultSettings.put(ConfigUtil.KEY_CLEAN_DB_SCRIPT, ConfigUtil.VALUE_CLEAN_DB_SCRIPT.toString());
    mDefaultSettings.put(ConfigUtil.KEY_USE_EXTERNAL_DB, String.valueOf(ConfigUtil.VALUE_USE_EXTERNAL_DB));
    mDefaultSettings.put(ConfigUtil.KEY_DB_INFO, ConfigUtil.VALUE_DB_INFO.toString());

    // Parse settings
    mDefaultSettings.put(ConfigUtil.KEY_OSM_DIRECTORY, ConfigUtil.VALUE_OSM_DIRECTORY.toString());
    mDefaultSettings.put(ConfigUtil.KEY_GTFS_DIRECTORY, ConfigUtil.VALUE_GTFS_DIRECTORY.toString());

    // Routing settings
    mDefaultSettings.put(ConfigUtil.KEY_GRAPH_CACHE, ConfigUtil.VALUE_GRAPH_CACHE.toString());
    mDefaultSettings.put(ConfigUtil.KEY_USE_GRAPH_CACHE, String.valueOf(ConfigUtil.VALUE_USE_GRAPH_CACHE));
    mDefaultSettings.put(ConfigUtil.KEY_GRAPH_CACHE_INFO, ConfigUtil.VALUE_GRAPH_CACHE_INFO.toString());
    mDefaultSettings.put(ConfigUtil.KEY_ROUTING_SERVER_PORT, String.valueOf(ConfigUtil.VALUE_ROUTING_SERVER_PORT));
    mDefaultSettings.put(ConfigUtil.KEY_OSM_ROAD_FILTER, ConfigUtil.VALUE_OSM_ROAD_FILTER.toString());
    mDefaultSettings.put(ConfigUtil.KEY_ROUTING_MODEL_MODE, ConfigUtil.VALUE_ROUTING_MODEL_MODE);
    mDefaultSettings.put(ConfigUtil.KEY_ACCESS_NODES_MAXIMUM, String.valueOf(ConfigUtil.VALUE_ACCESS_NODES_MAXIMUM));
    mDefaultSettings.put(ConfigUtil.KEY_FOOTPATH_REACHABILITY, String.valueOf(ConfigUtil.VALUE_FOOTPATH_REACHABILITY));
    mDefaultSettings.put(ConfigUtil.KEY_TRANSFER_DELAY, String.valueOf(ConfigUtil.VALUE_TRANSFER_DELAY));
    mDefaultSettings.put(ConfigUtil.KEY_ABORT_TRAVEL_TIME, String.valueOf(ConfigUtil.VALUE_ABORT_TRAVEL_TIME));
    mDefaultSettings.put(ConfigUtil.KEY_AMOUNT_OF_LANDMARKS, String.valueOf(ConfigUtil.VALUE_AMOUNT_OF_LANDMARKS));

    // Name search settings
    mDefaultSettings.put(ConfigUtil.KEY_NAME_SEARCH_SERVER_PORT,
        String.valueOf(ConfigUtil.VALUE_NAME_SEARCH_SERVER_PORT));
    mDefaultSettings.put(ConfigUtil.KEY_NAME_SEARCH_SERVER_MATCH_LIMIT,
        String.valueOf(ConfigUtil.VALUE_NAME_SEARCH_SERVER_MATCH_LIMIT));

    // Nearest search settings
    mDefaultSettings.put(ConfigUtil.KEY_NEAREST_SEARCH_SERVER_PORT,
        String.valueOf(ConfigUtil.VALUE_NEAREST_SEARCH_SERVER_PORT));
  }

}
