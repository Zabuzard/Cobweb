package de.tischner.cobweb.config;

import java.nio.file.Path;

public interface IDatabaseConfigProvider {
  Path getInitDbScript();

  String getJDBCUrl();
}
