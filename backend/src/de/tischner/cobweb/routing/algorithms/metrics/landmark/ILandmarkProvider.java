package de.tischner.cobweb.routing.algorithms.metrics.landmark;

import java.util.Collection;

public interface ILandmarkProvider<E> {
  Collection<E> getLandmarks(int amount);
}
