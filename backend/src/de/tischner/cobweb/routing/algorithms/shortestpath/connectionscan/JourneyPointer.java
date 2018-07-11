package de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan;

import de.tischner.cobweb.routing.model.timetable.Connection;
import de.tischner.cobweb.routing.model.timetable.Footpath;

public final class JourneyPointer {
  private final Connection mEnterConnection;
  private final Connection mExitConnection;
  private final Footpath mFootpath;

  public JourneyPointer(final Connection enterConnection, final Connection exitConnection, final Footpath footpath) {
    mEnterConnection = enterConnection;
    mExitConnection = exitConnection;
    mFootpath = footpath;
  }

  public Connection getEnterConnection() {
    return mEnterConnection;
  }

  public Connection getExitConnection() {
    return mExitConnection;
  }

  public Footpath getFootpath() {
    return mFootpath;
  }
}
