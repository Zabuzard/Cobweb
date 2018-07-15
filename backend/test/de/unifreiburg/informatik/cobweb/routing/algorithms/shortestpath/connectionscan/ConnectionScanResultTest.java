package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.connectionscan;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection;
import de.unifreiburg.informatik.cobweb.routing.model.timetable.Footpath;

/**
 * Test for the class {@link ConnectionScanResult}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ConnectionScanResultTest {
  /**
   * The result used for testing.
   */
  private ConnectionScanResult mResult;

  /**
   * Setups a result instance for testing.
   */
  @Before
  public void setUp() {
    final ArrayList<JourneyPointer> journeys = new ArrayList<>();
    journeys.add(new JourneyPointer(new Connection(1, 0, 1, 2, 100, 120), new Connection(1, 4, 5, 6, 200, 220),
        new Footpath(6, 7, 50)));
    journeys.add(new JourneyPointer(new Connection(1, 0, 1, 2, 100, 120), new Connection(1, 4, 5, 6, 200, 220),
        new Footpath(6, 7, 50)));
    journeys.add(new JourneyPointer(new Connection(1, 0, 1, 2, 100, 120), new Connection(1, 4, 5, 6, 200, 220),
        new Footpath(6, 7, 50)));

    mResult = new ConnectionScanResult(new int[] { 100, 120, 140 }, journeys.toArray(new JourneyPointer[0]));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.connectionscan.ConnectionScanResult#ConnectionScanResult(int[], de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.connectionscan.JourneyPointer[])}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testConnectionScanResult() {
    try {
      new ConnectionScanResult(new int[] { 100, 120, 140 }, new JourneyPointer[] { null, null, null });
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.connectionscan.ConnectionScanResult#getStopToArrTime()}.
   */
  @Test
  public void testGetStopToArrTime() {
    final int[] stopToArrTime = mResult.getStopToArrTime();
    Assert.assertArrayEquals(new int[] { 100, 120, 140 }, stopToArrTime);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.connectionscan.ConnectionScanResult#getStopToJourney()}.
   */
  @Test
  public void testGetStopToJourney() {
    final JourneyPointer[] stopToJourney = mResult.getStopToJourney();
    Assert.assertEquals(3, stopToJourney.length);
    Assert.assertEquals(1, stopToJourney[0].getEnterConnection().getTripId());
  }

}
