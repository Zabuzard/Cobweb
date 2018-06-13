package de.tischner.cobweb.routing.model.graph;

import java.util.Set;

public interface IHasTransportationMode {
  Set<ETransportationMode> getTransportationModes();

  boolean hasTransportationMode(ETransportationMode mode);
}
