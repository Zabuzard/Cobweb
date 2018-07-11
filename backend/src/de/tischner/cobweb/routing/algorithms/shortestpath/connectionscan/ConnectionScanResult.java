package de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan;

public final class ConnectionScanResult {
  private final int[] mStopToArrTime;
  private final JourneyPointer[] mStopToJourney;

  public ConnectionScanResult(final int[] stopToArrTime, final JourneyPointer[] stopToJourney) {
    mStopToArrTime = stopToArrTime;
    mStopToJourney = stopToJourney;
  }

  public int[] getStopToArrTime() {
    return mStopToArrTime;
  }

  public JourneyPointer[] getStopToJourney() {
    return mStopToJourney;
  }
}
