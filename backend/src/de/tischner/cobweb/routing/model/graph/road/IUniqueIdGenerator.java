package de.tischner.cobweb.routing.model.graph.road;

import java.util.NoSuchElementException;

/**
 * Interface for classes that can generate unique IDs.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IUniqueIdGenerator {
  /**
   * Generates and returns an unique ID for nodes.
   *
   * @return The generated unique ID
   * @throws NoSuchElementException If the generator is out of unique IDs to
   *                                generate
   */
  int generateUniqueNodeId() throws NoSuchElementException;

  /**
   * Generates and returns an unique ID for ways.
   *
   * @return The generated unique ID
   * @throws NoSuchElementException If the generator is out of unique IDs to
   *                                generate
   */
  int generateUniqueWayId() throws NoSuchElementException;
}
