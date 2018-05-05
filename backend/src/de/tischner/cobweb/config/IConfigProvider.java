package de.tischner.cobweb.config;

import java.util.Map;

/**
 * Interface for classes that provide configuration settings. Offers methods to
 * access the settings in a key-value pair like fashion.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public interface IConfigProvider {

  /**
   * Gets all settings stored in the provider.
   *
   * @return All settings in a key-value map
   */
  Map<String, String> getAllSettings();

  /**
   * Gets the value of the setting with the given key or <tt>null</tt> if there is
   * no.
   *
   * @param key The key to get the value
   * @return The value corresponding to the given key or <tt>null</tt>
   */
  String getSetting(String key);

  /**
   * Sets the value of the setting with the given key to the given value. Setting
   * <tt>null</tt> should be avoided, the behavior is undefined.
   *
   * @param key   The key to set the value for
   * @param value The value to set
   */
  void setSetting(String key, String value);
}
