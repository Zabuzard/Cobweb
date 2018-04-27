package de.tischner.cobweb.config;

import java.nio.file.Path;

public interface IRoutingConfigProvider {
  Path getGraphCache();

  int getRoutingServerPort();

  boolean useGraphCache();
}
