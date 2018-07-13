package de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan;

/**
 * POJO that contains the results of a connection scan algorithm computation.
 * That is, it contains shortest path information.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ConnectionScanResult {
  /**
   * An array mapping stops by their IDs to the earliest arrival time in seconds
   * since midnight.
   */
  private final int[] mStopToArrTime;
  /**
   * An array mapping stops by their IDs to their journey pointers which are
   * used to construct shortest paths by backtracking.
   */
  private final JourneyPointer[] mStopToJourney;

  /**
   * Creates a new connection scan results container.
   *
   * @param stopToArrTime An array mapping stops by their IDs to the earliest
   *                      arrival time in seconds since midnight
   * @param stopToJourney An array mapping stops by their IDs to their journey
   *                      pointers which are used to construct shortest paths by
   *                      backtracking
   */
  public ConnectionScanResult(final int[] stopToArrTime, final JourneyPointer[] stopToJourney) {
    mStopToArrTime = stopToArrTime;
    mStopToJourney = stopToJourney;
  }

  /**
   * Gets an array mapping stops by their IDs to the earliest arrival time in
   * seconds since midnight.
   *
   * @return The array mapping stops to earliest arrival time
   */
  public int[] getStopToArrTime() {
    return mStopToArrTime;
  }

  /**
   * Gets an array mapping stops by their IDs to their journey pointers which
   * are used to construct shortest paths by backtracking.
   *
   * @return The array mapping stops to their journey pointers
   */
  public JourneyPointer[] getStopToJourney() {
    return mStopToJourney;
  }
}
