package de.tischner.cobweb.config;

import java.nio.file.Path;

public interface IDatabaseConfigProvider {
  Path getDbInfo();

  Path getInitDbScript();

  String getJDBCUrl();

  boolean useExternalDb();
}
