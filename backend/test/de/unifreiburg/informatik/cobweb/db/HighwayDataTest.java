package de.unifreiburg.informatik.cobweb.db;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unifreiburg.informatik.cobweb.parsing.osm.EHighwayType;

/**
 * Test for the class {@link HighwayData}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class HighwayDataTest {
  /**
   * The highway data used for testing.
   */
  private HighwayData mHighwayData;

  /**
   * Setups a highway data instance for testing.
   */
  @Before
  public void setUp() {
    mHighwayData = new HighwayData(12L, EHighwayType.MOTORWAY, 100);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.db.HighwayData#getMaxSpeed()}.
   */
  @Test
  public final void testGetMaxSpeed() {
    Assert.assertEquals(100, mHighwayData.getMaxSpeed());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.db.HighwayData#getType()}.
   */
  @Test
  public final void testGetType() {
    Assert.assertEquals(EHighwayType.MOTORWAY, mHighwayData.getType());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.db.HighwayData#getWayId()}.
   */
  @Test
  public final void testGetWayId() {
    Assert.assertEquals(12L, mHighwayData.getWayId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.db.HighwayData#HighwayData(long, de.unifreiburg.informatik.cobweb.parsing.osm.EHighwayType, int)}.
   */
  @SuppressWarnings({ "static-method", "unused" })
  @Test
  public final void testHighwayData() {
    try {
      new HighwayData(12L, EHighwayType.MOTORWAY, 100);
      new HighwayData(-20L, EHighwayType.ROAD, -100);
      new HighwayData(0L, null, 0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
