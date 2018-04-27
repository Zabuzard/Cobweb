package de.tischner.cobweb.config;

import java.nio.file.Path;

public interface IParseConfigProvider {
  Path getGtfsDirectory();

  Path getOsmDirectory();
}
