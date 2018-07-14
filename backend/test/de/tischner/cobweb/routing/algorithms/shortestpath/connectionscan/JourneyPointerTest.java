package de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.timetable.Connection;
import de.tischner.cobweb.routing.model.timetable.Footpath;

/**
 * Test for the class {@link JourneyPointer}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class JourneyPointerTest {
  /**
   * The journey pointer used for testing.
   */
  private JourneyPointer mJourneyPointer;

  /**
   * Setups a journey pointer instance for testing.
   */
  @Before
  public void setUp() {
    mJourneyPointer = new JourneyPointer(new Connection(1, 0, 1, 2, 100, 120), new Connection(1, 4, 5, 6, 200, 220),
        new Footpath(6, 7, 50));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan.JourneyPointer#getEnterConnection()}.
   */
  @Test
  public void testGetEnterConnection() {
    Assert.assertEquals(0, mJourneyPointer.getEnterConnection().getSequenceIndex());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan.JourneyPointer#getExitConnection()}.
   */
  @Test
  public void testGetExitConnection() {
    Assert.assertEquals(4, mJourneyPointer.getExitConnection().getSequenceIndex());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan.JourneyPointer#getFootpath()}.
   */
  @Test
  public void testGetFootpath() {
    Assert.assertEquals(7, mJourneyPointer.getFootpath().getArrStopId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan.JourneyPointer#JourneyPointer(de.tischner.cobweb.routing.model.timetable.Connection, de.tischner.cobweb.routing.model.timetable.Connection, de.tischner.cobweb.routing.model.timetable.Footpath)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testJourneyPointer() {
    try {
      new JourneyPointer(new Connection(1, 0, 1, 2, 100, 120), new Connection(1, 4, 5, 6, 200, 220),
          new Footpath(6, 7, 50));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
