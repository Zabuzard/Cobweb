package de.unifreiburg.informatik.cobweb.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unifreiburg.informatik.cobweb.config.IDatabaseConfigProvider;
import de.unifreiburg.informatik.cobweb.config.IRoutingConfigProvider;
import de.unifreiburg.informatik.cobweb.db.ScriptExecutor;

/**
 * Utility class which provides methods to clean databases, caches and
 * serialized data.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class CleanUtil {
  /**
   * The logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(CleanUtil.class);

  /**
   * Cleans the database, caches and serialized data provided by the given
   * configuration files.<br>
   * <br>
   * Will only clean if the corresponding flags are set, namely
   * {@link IRoutingConfigProvider#useGraphCache()} and
   * {@link IDatabaseConfigProvider#useExternalDb()}.
   *
   * @param routingConfig  The routing configuration providing paths to the
   *                       graph cache
   * @param databaseConfig The database configuration providing paths to the
   *                       external database
   */
  public static void clean(final IRoutingConfigProvider routingConfig, final IDatabaseConfigProvider databaseConfig) {
    LOGGER.info("Starting to clean");
    CleanUtil.cleanGraphCache(routingConfig);
    CleanUtil.cleanDatabase(databaseConfig);
  }

  /**
   * Cleans the external database provided by the given configuration.<br>
   * <br>
   * This includes the routing tables in the given database and its info file,
   * if the flag {@link IDatabaseConfigProvider#useExternalDb()} is set.
   *
   * @param databaseConfig The database configuration providing paths to the
   *                       external database
   */
  private static void cleanDatabase(final IDatabaseConfigProvider databaseConfig) {
    if (!databaseConfig.useExternalDb()) {
      return;
    }

    CleanUtil.deleteIfPossible(databaseConfig.getDbInfo());

    final Path cleanDbScript = databaseConfig.getCleanDbScript();
    LOGGER.info("Executing clean database script: {}", cleanDbScript);
    try {
      ScriptExecutor.executeScript(cleanDbScript, DriverManager.getConnection(databaseConfig.getJdbcUrl()));
    } catch (SQLException | IOException e) {
      // Ignore the problem
    }
  }

  /**
   * Cleans the graph cache provided by the given configuration.<br>
   * <br>
   * This includes the graph cache and its info file, if the flag
   * {@link IRoutingConfigProvider#useGraphCache()} is set.
   *
   * @param routingConfig The routing configuration providing paths to the graph
   *                      cache
   */
  private static void cleanGraphCache(final IRoutingConfigProvider routingConfig) {
    if (!routingConfig.useGraphCache()) {
      return;
    }

    CleanUtil.deleteIfPossible(routingConfig.getGraphCache());
    CleanUtil.deleteIfPossible(routingConfig.getGraphCacheInfo());
  }

  /**
   * Attempts to delete the file or directory represented by the given path.<br>
   * <br>
   * For the method to succeed the file must exist, not be used by other
   * applications and delete access must be granted. If it is a directory it
   * must be empty.
   *
   * @param path The file or directory to delete
   * @return <code>True</code> if the file or directory could be deleted,
   *         <code>false</code> if not.
   */
  private static boolean deleteIfPossible(final Path path) {
    try {
      final boolean wasDeleted = Files.deleteIfExists(path);
      if (wasDeleted) {
        LOGGER.info("Deleted: {}", path);
      }
      return wasDeleted;
    } catch (IOException | SecurityException e) {
      return false;
    }
  }

  /**
   * Utility class. No implementation.
   */
  private CleanUtil() {

  }
}
