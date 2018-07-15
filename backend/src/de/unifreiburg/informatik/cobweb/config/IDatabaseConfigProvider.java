package de.unifreiburg.informatik.cobweb.config;

import java.nio.file.Path;

/**
 * Interface for classes that provide database related configuration settings.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IDatabaseConfigProvider {
  /**
   * Gets the path to the SQL script that is executed to clean an external
   * database.
   *
   * @return The path to the SQL script
   */
  Path getCleanDbScript();

  /**
   * Gets the path to the database info object that stores information about the
   * content of the database. Can be used to determine which data files need to
   * be considered when parsing in order to avoid pushing the same data again.
   *
   * @return The path to the database info object
   */
  Path getDbInfo();

  /**
   * Gets the path to the SQL script that is executed to initialize an external
   * database.
   *
   * @return The path to the SQL script
   */
  Path getInitDbScript();

  /**
   * Gets the JDBC URL used to connect to an external database.
   *
   * @return The JDBC URL used to connect
   */
  String getJdbcUrl();

  /**
   * Whether an external or an internal in-memory database should be used.
   *
   * @return <tt>True</tt> if an external database should be used,
   *         <tt>false</tt> otherwise
   */
  boolean useExternalDb();
}
