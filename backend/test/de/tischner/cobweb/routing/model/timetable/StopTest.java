package de.tischner.cobweb.routing.model.timetable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link Stop}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class StopTest {
  /**
   * The stop used for testing.
   */
  private Stop mStop;

  /**
   * Setups a stop instance for testing.
   */
  @Before
  public void setUp() {
    mStop = new Stop(5, 1.1f, 2.2f);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.Stop#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mStop, mStop);
    Assert.assertEquals(mStop, new Stop(5, 1.1f, 2.2f));

    Assert.assertEquals(mStop, new Stop(5, 2.2f, 1.1f));
    Assert.assertEquals(mStop, new Stop(5, 0.0f, 0.0f));

    Assert.assertNotEquals(mStop, new Stop(3, 1.1f, 2.2f));
    Assert.assertNotEquals(mStop, new Stop(0, 1.1f, 2.2f));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.Stop#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(5, mStop.getId());
    Assert.assertEquals(0, new Stop(0, 1.1f, 2.2f).getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.Stop#getLatitude()}.
   */
  @Test
  public void testGetLatitude() {
    Assert.assertEquals(1.1f, mStop.getLatitude(), 0.0001);
    Assert.assertEquals(0.0f, new Stop(0, 0.0f, 2.2f).getLatitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.Stop#getLongitude()}.
   */
  @Test
  public void testGetLongitude() {
    Assert.assertEquals(2.2f, mStop.getLongitude(), 0.0001);
    Assert.assertEquals(0.0f, new Stop(0, 0.0f, 0.0f).getLongitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.Stop#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mStop.hashCode(), mStop.hashCode());
    Assert.assertEquals(mStop.hashCode(), new Stop(5, 1.1f, 2.2f).hashCode());

    Assert.assertEquals(mStop.hashCode(), new Stop(5, 2.2f, 1.1f).hashCode());
    Assert.assertEquals(mStop.hashCode(), new Stop(5, 0.0f, 0.0f).hashCode());

    Assert.assertNotEquals(mStop.hashCode(), new Stop(3, 1.1f, 2.2f).hashCode());
    Assert.assertNotEquals(mStop.hashCode(), new Stop(0, 1.1f, 2.2f).hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.Stop#setLatitude(float)}.
   */
  @Test
  public void testSetLatitude() {
    mStop.setLatitude(10.0f);
    Assert.assertEquals(10.0f, mStop.getLatitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.Stop#setLongitude(float)}.
   */
  @Test
  public void testSetLongitude() {
    mStop.setLongitude(10.0f);
    Assert.assertEquals(10.0f, mStop.getLongitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.timetable.Stop#Stop(int, float, float)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testStop() {
    try {
      new Stop(5, 1.1f, 2.2f);
      new Stop(0, 0.0f, 0.0f);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
