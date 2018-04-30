package de.tischner.cobweb.routing.algorithms.metrics;

public interface IMetric<E> {
  double distance(E first, E second);
}
