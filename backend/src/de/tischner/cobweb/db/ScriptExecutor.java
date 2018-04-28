package de.tischner.cobweb.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

public final class ScriptExecutor {

  private static final String QUERY_SEPARATOR = " ";
  private static final String SQL_COMMENT = "--";
  private static final String STATEMENT_DELIMITER = ";";

  public static void executeScript(final Path script, final Connection connection) throws SQLException, IOException {
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
            statement.execute(statementBuilder.toString());
          }

          // Prepare for next statement
          statementBuilder = new StringJoiner(QUERY_SEPARATOR);
        }
      }
    } finally {
      connection.setAutoCommit(autoCommitBefore);
    }
  }

}
