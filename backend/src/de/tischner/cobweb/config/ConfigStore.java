package de.tischner.cobweb.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    INameSearchConfigProvider, IDatabaseConfigProvider {
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

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IConfigProvider#getAllSettings()
   */
  @Override
  public Map<String, String> getAllSettings() {
    return mSettings;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IDatabaseConfigProvider#getCleanDbScript()
   */
  @Override
  public Path getCleanDbScript() {
    return Paths.get(getSetting(ConfigUtil.KEY_CLEAN_DB_SCRIPT));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IDatabaseConfigProvider#getDbInfo()
   */
  @Override
  public Path getDbInfo() {
    return Paths.get(getSetting(ConfigUtil.KEY_DB_INFO));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IRoutingConfigProvider#getGraphCache()
   */
  @Override
  public Path getGraphCache() {
    return Paths.get(getSetting(ConfigUtil.KEY_GRAPH_CACHE));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IRoutingConfigProvider#getGraphCacheInfo()
   */
  @Override
  public Path getGraphCacheInfo() {
    return Paths.get(getSetting(ConfigUtil.KEY_GRAPH_CACHE_INFO));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IParseConfigProvider#getGtfsDirectory()
   */
  @Override
  public Path getGtfsDirectory() {
    return Paths.get(getSetting(ConfigUtil.KEY_GTFS_DIRECTORY));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IDatabaseConfigProvider#getInitDbScript()
   */
  @Override
  public Path getInitDbScript() {
    return Paths.get(getSetting(ConfigUtil.KEY_INIT_DB_SCRIPT));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IDatabaseConfigProvider#getJDBCUrl()
   */
  @Override
  public String getJdbcUrl() {
    return getSetting(ConfigUtil.KEY_JDBC_URL);
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.INameSearchConfigProvider#getMatchLimit()
   */
  @Override
  public int getMatchLimit() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_NAME_SEARCH_SERVER_MATCH_LIMIT));
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.config.INameSearchConfigProvider#getNameSearchServerPort
   * ()
   */
  @Override
  public int getNameSearchServerPort() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_NAME_SEARCH_SERVER_PORT));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IParseConfigProvider#getOsmDirectory()
   */
  @Override
  public Path getOsmDirectory() {
    return Paths.get(getSetting(ConfigUtil.KEY_OSM_DIRECTORY));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IRoutingConfigProvider#getOsmRoadFilter()
   */
  @Override
  public Path getOsmRoadFilter() {
    return Paths.get(getSetting(ConfigUtil.KEY_OSM_ROAD_FILTER));
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.config.IRoutingConfigProvider#getRoutingServerPort()
   */
  @Override
  public int getRoutingServerPort() {
    return Integer.valueOf(getSetting(ConfigUtil.KEY_ROUTING_SERVER_PORT));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IConfigProvider#getSetting(java.lang.String)
   */
  @Override
  public String getSetting(final String key) {
    final String value = mSettings.get(key);
    if (value == null || value.isEmpty()) {
      return getDefaultValue(key);
    }
    return value;
  }

  /**
   * Resets all settings of the store to their default values.
   */
  public void resetToDefaultValues() {
    mSettings.clear();
    mSettings.putAll(mDefaultSettings);
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IConfigProvider#setSetting(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void setSetting(final String key, final String value) {
    mSettings.put(key, value);
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IDatabaseConfigProvider#useExternalDb()
   */
  @Override
  public boolean useExternalDb() {
    return Boolean.valueOf(getSetting(ConfigUtil.KEY_USE_EXTERNAL_DB));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.config.IRoutingConfigProvider#useGraphCache()
   */
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

    // Name search settings
    mDefaultSettings.put(ConfigUtil.KEY_NAME_SEARCH_SERVER_PORT,
        String.valueOf(ConfigUtil.VALUE_NAME_SEARCH_SERVER_PORT));
    mDefaultSettings.put(ConfigUtil.KEY_NAME_SEARCH_SERVER_MATCH_LIMIT,
        String.valueOf(ConfigUtil.VALUE_NAME_SEARCH_SERVER_MATCH_LIMIT));
  }

}
