package de.tischner.cobweb.routing.model.graph.road;

/**
 * Interface for consumer of {@link IReversedProvider}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
@FunctionalInterface
public interface IReversedConsumer {
  /**
   * Sets the {@link IReversedProvider} to be consumed.
   *
   * @param provider The provider to consume
   */
  void setReversedProvider(IReversedProvider provider);
}
