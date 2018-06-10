package de.tischner.cobweb.routing.algorithms.nearestneighbor;

import java.util.Optional;

import de.tischner.cobweb.routing.model.graph.ISpatial;

public interface INearestNeighborComputation<E extends ISpatial> {
  public Optional<E> getNearestNeighbor(E point);
}
