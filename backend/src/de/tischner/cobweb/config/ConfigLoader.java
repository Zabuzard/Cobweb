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

public final class ConfigLoader {
  private static final String CONFIG_COMMENT = "Configuration settings for Cobweb.";
  private static final Path CONFIG_PATH = Paths.get("backend", "res", "config.ini");
  private final static Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);
  private final Properties mProperties;

  public ConfigLoader() {
    mProperties = new Properties();
  }

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
