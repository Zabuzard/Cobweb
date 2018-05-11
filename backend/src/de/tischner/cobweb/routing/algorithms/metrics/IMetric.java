package de.tischner.cobweb.routing.algorithms.metrics;

/**
 * Interface for a metric defined on a given type of objects.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> The type of objects the metric operates on
 */
@FunctionalInterface
public interface IMetric<E> {
  /**
   * Computes the distance between the two given objects accordingly to the
   * implementing metric.
   *
   * @param first  The first object
   * @param second The second object
   * @return The distance between the two given objects
   */
  double distance(E first, E second);
}
