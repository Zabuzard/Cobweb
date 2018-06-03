package de.tischner.cobweb.routing.model.graph.transit;

import java.util.NoSuchElementException;

/**
 * Interface for classes that can generate unique IDs for transit nodes.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface ITransitIdGenerator {
  /**
   * Generates and returns an unique ID for transit nodes.
   *
   * @return The generated unique ID
   * @throws NoSuchElementException If the generator is out of unique IDs to
   *                                generate
   */
  int generateUniqueNodeId() throws NoSuchElementException;
}
