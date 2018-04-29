package de.tischner.cobweb.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigStore
    implements IConfigProvider, IParseConfigProvider, IRoutingConfigProvider, IDatabaseConfigProvider {
  private final static Logger LOGGER = LoggerFactory.getLogger(ConfigStore.class);
  private final Map<String, String> mDefaultSettings;
  private final Map<String, String> mSettings;

  public ConfigStore() {
    mDefaultSettings = new HashMap<>();
    setupDefaultSettings();
    // Initially use the default settings
    mSettings = new HashMap<>(mDefaultSettings);
  }

  @Override
  public Map<String, String> getAllSettings() {
    return mSettings;
  }

  @Override
  public Path getGraphCache() {
    return Paths.get(getSetting(ConfigUtil.KEY_GRAPH_CACHE));
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
  public String getJDBCUrl() {
    return getSetting(ConfigUtil.KEY_JDBC_URL);
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

  public void resetToDefaultValues() {
    mSettings.clear();
    mSettings.putAll(mDefaultSettings);
  }

  @Override
  public void setSetting(final String key, final String value) {
    mSettings.put(key, value);
  }

  @Override
  public boolean useGraphCache() {
    return Boolean.valueOf(getSetting(ConfigUtil.KEY_USE_GRAPH_CACHE));
  }

  private String getDefaultValue(final String key) {
    return mDefaultSettings.get(key);
  }

  private void setupDefaultSettings() {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Loading default settings");
    }
    // Database settings
    mDefaultSettings.put(ConfigUtil.KEY_JDBC_URL, ConfigUtil.VALUE_JDBC_URL);
    mDefaultSettings.put(ConfigUtil.KEY_INIT_DB_SCRIPT, ConfigUtil.VALUE_INIT_DB_SCRIPT.toString());

    // Parse settings
    mDefaultSettings.put(ConfigUtil.KEY_OSM_DIRECTORY, ConfigUtil.VALUE_OSM_DIRECTORY.toString());
    mDefaultSettings.put(ConfigUtil.KEY_GTFS_DIRECTORY, ConfigUtil.VALUE_GTFS_DIRECTORY.toString());

    // Routing settings
    mDefaultSettings.put(ConfigUtil.KEY_GRAPH_CACHE, ConfigUtil.VALUE_GRAPH_CACHE.toString());
    mDefaultSettings.put(ConfigUtil.KEY_USE_GRAPH_CACHE, String.valueOf(ConfigUtil.VALUE_USE_GRAPH_CACHE));
    mDefaultSettings.put(ConfigUtil.KEY_ROUTING_SERVER_PORT, String.valueOf(ConfigUtil.VALUE_ROUTING_SERVER_PORT));
    mDefaultSettings.put(ConfigUtil.KEY_OSM_ROAD_FILTER, ConfigUtil.VALUE_OSM_ROAD_FILTER.toString());
  }

}
