package de.tischner.cobweb.config;

import java.nio.file.Path;

public interface IRoutingConfigProvider {
  Path getGraphCache();

  Path getOsmRoadFilter();

  int getRoutingServerPort();

  boolean useGraphCache();
}
