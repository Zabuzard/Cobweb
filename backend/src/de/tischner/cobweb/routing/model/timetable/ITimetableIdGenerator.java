package de.tischner.cobweb.routing.model.timetable;

import java.util.NoSuchElementException;

/**
 * Interface for classes that can generate unique IDs for timetable elements
 * like stops and trips.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface ITimetableIdGenerator {
  /**
   * Generates and returns an unique ID for stops.
   *
   * @return The generated unique ID
   * @throws NoSuchElementException If the generator is out of unique IDs to
   *                                generate
   */
  int generateUniqueStopId() throws NoSuchElementException;

  /**
   * Generates and returns an unique ID for trips.
   *
   * @return The generated unique ID
   * @throws NoSuchElementException If the generator is out of unique IDs to
   *                                generate
   */
  int generateUniqueTripId() throws NoSuchElementException;
}
