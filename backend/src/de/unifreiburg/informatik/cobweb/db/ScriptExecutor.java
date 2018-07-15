package de.unifreiburg.informatik.cobweb.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that is able to execute SQL script files on a given database
 * connection.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ScriptExecutor {
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ScriptExecutor.class);

  /**
   * Separator used in a SQL query to separate single parts of a statement. Used
   * for converting a multi-line statement into an one-line statement.
   */
  private static final String QUERY_SEPARATOR = " ";
  /**
   * Prefix used in SQL to indicate a comment. Lines that start with this prefix
   * will be ignored.
   */
  private static final String SQL_COMMENT = "--";
  /**
   * Symbol that acts as a delimiter of statements.
   */
  private static final String STATEMENT_DELIMITER = ";";

  /**
   * Executes the given SQL script on the given database connection.
   *
   * @param script     Path to the SQL script to execute
   * @param connection Database connection to execute the script on
   * @throws SQLException If an SQL exception occurred while executing the
   *                      statements
   * @throws IOException  If an I/O exception occurred while reading the SQL
   *                      script
   */
  public static void executeScript(final Path script, final Connection connection) throws SQLException, IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Parsing script: {}", script);
    }
    // Set auto-commit to true
    final boolean autoCommitBefore = connection.getAutoCommit();
    try {
      connection.setAutoCommit(true);

      StringJoiner statementBuilder = new StringJoiner(QUERY_SEPARATOR);
      try (BufferedReader br = Files.newBufferedReader(script)) {
        while (true) {
          final String line = br.readLine();
          if (line == null) {
            break;
          }

          String trimmedLine = line.trim();
          // Ignore line if empty or comment
          if (trimmedLine.isEmpty() || trimmedLine.startsWith(SQL_COMMENT)) {
            continue;
          }

          final boolean endsStatement = trimmedLine.endsWith(STATEMENT_DELIMITER);
          // Remove the delimiter
          if (endsStatement) {
            trimmedLine = trimmedLine.substring(0, trimmedLine.length() - 1);
          }
          // Append to current statement
          statementBuilder.add(trimmedLine);

          // Continue if not end of statement
          if (!endsStatement) {
            continue;
          }

          // Execute statement
          try (Statement statement = connection.createStatement()) {
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("Executing statement: {}", statementBuilder.toString());
            }
            statement.execute(statementBuilder.toString());
          }

          // Prepare for next statement
          statementBuilder = new StringJoiner(QUERY_SEPARATOR);
        }
      }
    } finally {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Resetting auto-commit to: {}", autoCommitBefore);
      }
      connection.setAutoCommit(autoCommitBefore);
    }
  }

  /**
   * Utility class. No implementation.
   */
  private ScriptExecutor() {

  }

}
