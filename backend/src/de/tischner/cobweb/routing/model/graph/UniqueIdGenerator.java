package de.tischner.cobweb.routing.model.graph;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * Class that can generate unique IDs.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class UniqueIdGenerator implements Serializable {
  /**
   * The unique ID for the last possible entity. Is used to determine when the
   * generator is out of IDs.
   */
  private static final int LAST_ID = -1;
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The unique ID used last.
   */
  private int mLastUsedId;

  /**
   * Creates a new unique ID generator.
   */
  public UniqueIdGenerator() {
    mLastUsedId = LAST_ID;
  }

  /**
   * Generates and returns an unique ID.
   *
   * @return The generated unique ID
   * @throws NoSuchElementException If the generator is out of unique IDs to
   *                                generate
   */
  public int generateUniqueId() throws NoSuchElementException {
    mLastUsedId++;
    if (mLastUsedId == LAST_ID) {
      throw new NoSuchElementException();
    }
    return mLastUsedId;
  }
}
