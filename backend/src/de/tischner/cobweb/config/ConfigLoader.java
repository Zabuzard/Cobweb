package de.tischner.cobweb.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to load and save configurations to a
 * {@link IConfigProvider}.<br>
 * <br>
 * Use {@link #loadConfig(IConfigProvider)} to load the configuration from a
 * file into the provider. Use {@link #saveConfig(IConfigProvider)} to save the
 * configuration currently provided by the given provider to a file.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ConfigLoader {
  /**
   * Comment for the configuration file.
   */
  private static final String CONFIG_COMMENT = "Configuration settings for Cobweb.";
  /**
   * Path to the configuration file.
   */
  private static final Path CONFIG_PATH = Paths.get("backend", "res", "config.ini");
  /**
   * Logger to use for logging.
   */
  private final static Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);
  /**
   * Map that holds the loaded configuration as key-value pairs.
   */
  private final Properties mProperties;

  /**
   * Creates a new configuration loader. Use {@link #loadConfig(IConfigProvider)}
   * to load a configuration from a file into the given provider and
   * {@link #saveConfig(IConfigProvider)} to save it back to the file.
   */
  public ConfigLoader() {
    mProperties = new Properties();
  }

  /**
   * Loads the configuration into the given provider. If the configuration does
   * not yet exist, an attempt to save using {@link #saveConfig(IConfigProvider)}
   * is made. Therefore, default values provided by the {@link IConfigProvider}
   * are used.
   *
   * @param provider Provider to load the configuration into.
   * @throws UncheckedIOException If an I/O-exception occurred while loading or
   *                              saving the configuration file.
   */
  public void loadConfig(final IConfigProvider provider) throws UncheckedIOException {
    LOGGER.info("Loading config");
    try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
      mProperties.load(input);
    } catch (final NoSuchFileException noSuchFileException) {
      // Try a second time after saving default settings
      saveConfig(provider);

      try (InputStream anotherInput = Files.newInputStream(CONFIG_PATH)) {
        mProperties.load(anotherInput);
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }

    // Fetch and set every saved setting
    for (final Entry<Object, Object> entry : mProperties.entrySet()) {
      provider.setSetting((String) entry.getKey(), (String) entry.getValue());
    }
  }

  /**
   * Saves the configurations provided by the given provider into a file.
   *
   * @param provider Provider whose configuration is to be saved
   * @throws UncheckedIOException If an I/O-Exception occurred while saving the
   *                              configuration file.
   */
  public void saveConfig(final IConfigProvider provider) throws UncheckedIOException {
    LOGGER.info("Saving config");
    // Fetch and put every setting
    for (final Entry<String, String> entry : provider.getAllSettings().entrySet()) {
      mProperties.put(entry.getKey(), entry.getValue());
    }

    try (OutputStream output = Files.newOutputStream(CONFIG_PATH)) {
      // Save the settings
      mProperties.store(output, CONFIG_COMMENT);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
