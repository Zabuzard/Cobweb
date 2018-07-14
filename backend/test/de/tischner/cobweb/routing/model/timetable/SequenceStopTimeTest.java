package de.tischner.cobweb.routing.model.timetable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;

/**
 * Test for the class {@link SequenceStopTime}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class SequenceStopTimeTest {
  /**
   * The sequence stop time used for testing.
   */
  private SequenceStopTime mSequenceStopTime;

  /**
   * Setups a sequence stop time instance for testing.
   */
  @Before
  public void setUp() {
    mSequenceStopTime = new SequenceStopTime(100, 120, new AgencyAndId("foo", "bar"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.SequenceStopTime#getArrTime()}.
   */
  @Test
  public void testGetArrTime() {
    Assert.assertEquals(100, mSequenceStopTime.getArrTime());
    Assert.assertEquals(0, new SequenceStopTime(0, 120, new AgencyAndId("foo", "bar")).getArrTime());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.SequenceStopTime#getDepTime()}.
   */
  @Test
  public void testGetDepTime() {
    Assert.assertEquals(120, mSequenceStopTime.getDepTime());
    Assert.assertEquals(0, new SequenceStopTime(0, 0, new AgencyAndId("foo", "bar")).getDepTime());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.SequenceStopTime#getStopId()}.
   */
  @Test
  public void testGetStopId() {
    final AgencyAndId stopId = mSequenceStopTime.getStopId();
    Assert.assertEquals("foo", stopId.getAgencyId());
    Assert.assertEquals("bar", stopId.getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.SequenceStopTime#SequenceStopTime(int, int, org.onebusaway.gtfs.model.AgencyAndId)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testSequenceStopTime() {
    try {
      new SequenceStopTime(100, 120, new AgencyAndId("foo", "bar"));
      new SequenceStopTime(0, 0, new AgencyAndId("", ""));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
