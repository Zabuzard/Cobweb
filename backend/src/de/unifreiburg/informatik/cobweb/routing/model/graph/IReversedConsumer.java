package de.unifreiburg.informatik.cobweb.routing.model.graph;

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
