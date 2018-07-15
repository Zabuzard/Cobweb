package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.connectionscan;

import de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection;
import de.unifreiburg.informatik.cobweb.routing.model.timetable.Footpath;

/**
 * POJO for a journey pointer that represents a part of a journey. Can be used
 * for constructing shortest paths by backtracking.<br>
 * <br>
 * A pointer represents a section of a trip together with a final footpath.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class JourneyPointer {
  /**
   * The connection used to enter the trip.
   */
  private final Connection mEnterConnection;
  /**
   * The connection used to exit the trip.
   */
  private final Connection mExitConnection;
  /**
   * The footpath used after exiting the trip.
   */
  private final Footpath mFootpath;

  /**
   * Creates a new journey pointer that represents the given path.<br>
   * <br>
   * The connections must belong to the same trip. The enter connection must
   * appear in the trips sequence before the exit connection and the footpath
   * must departure where the exit connection arrives.
   *
   * @param enterConnection The connection used to enter the trip
   * @param exitConnection  The connection used to exit the trip
   * @param footpath        The footpath used after exiting the trip
   */
  public JourneyPointer(final Connection enterConnection, final Connection exitConnection, final Footpath footpath) {
    mEnterConnection = enterConnection;
    mExitConnection = exitConnection;
    mFootpath = footpath;
  }

  /**
   * Gets the connection used to enter the trip.
   *
   * @return The connection used to enter the trip
   */
  public Connection getEnterConnection() {
    return mEnterConnection;
  }

  /**
   * Gets the connection used to exit the trip.
   *
   * @return The connection used to exit the trip
   */
  public Connection getExitConnection() {
    return mExitConnection;
  }

  /**
   * Gets the footpath used after exiting the trip
   *
   * @return The footpath used after exiting the trip
   */
  public Footpath getFootpath() {
    return mFootpath;
  }
}
