package de.unifreiburg.informatik.cobweb.routing.model.timetable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link Footpath}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class FootpathTest {
  /**
   * The footpath used for testing.
   */
  private Footpath mFootpath;

  /**
   * Setups a footpath instance for testing.
   */
  @Before
  public void setUp() {
    mFootpath = new Footpath(10, 12, 100);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Footpath#Footpath(int, int, int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testFootpath() {
    try {
      new Footpath(10, 12, 100);
      new Footpath(0, 0, 0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Footpath#getArrStopId()}.
   */
  @Test
  public void testGetArrStopId() {
    Assert.assertEquals(12, mFootpath.getArrStopId());
    Assert.assertEquals(0, new Footpath(10, 0, 100).getArrStopId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Footpath#getDepStopId()}.
   */
  @Test
  public void testGetDepStopId() {
    Assert.assertEquals(10, mFootpath.getDepStopId());
    Assert.assertEquals(0, new Footpath(0, 0, 100).getDepStopId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Footpath#getDuration()}.
   */
  @Test
  public void testGetDuration() {
    Assert.assertEquals(100, mFootpath.getDuration());
    Assert.assertEquals(0, new Footpath(0, 0, 0).getDuration());
  }

}
