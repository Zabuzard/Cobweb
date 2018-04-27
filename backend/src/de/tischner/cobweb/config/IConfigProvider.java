package de.tischner.cobweb.config;

import java.util.Map;

public interface IConfigProvider {

  Map<String, String> getAllSettings();

  String getSetting(String key);

  void setSetting(String key, String value);
}
