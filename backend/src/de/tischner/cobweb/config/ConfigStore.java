package de.tischner.cobweb.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class ConfigStore implements IConfigProvider, IParseConfigProvider, IRoutingConfigProvider {
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
