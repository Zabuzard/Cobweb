package de.tischner.cobweb.db;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link SpatialNodeData}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class SpatialNodeDataTest {

  /**
   * The spatial node data used for testing.
   */
  private SpatialNodeData mSpatialNodeData;

  /**
   * Setups a highway data instance for testing.
   */
  @Before
  public void setUp() {
    mSpatialNodeData = new SpatialNodeData(1L, 10.0, 5.0);
  }

  /**
   * Test method for {@link de.tischner.cobweb.db.SpatialNodeData#getId()}.
   */
  @Test
  public final void testGetId() {
    Assert.assertEquals(1L, mSpatialNodeData.getId());
  }

  /**
   * Test method for {@link de.tischner.cobweb.db.SpatialNodeData#getLatitude()}.
   */
  @Test
  public final void testGetLatitude() {
    Assert.assertEquals(10.0, mSpatialNodeData.getLatitude(), 0.0001);
  }

  /**
   * Test method for {@link de.tischner.cobweb.db.SpatialNodeData#getLongitude()}.
   */
  @Test
  public final void testGetLongitude() {
    Assert.assertEquals(5.0, mSpatialNodeData.getLongitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.db.SpatialNodeData#SpatialNodeData(long, double, double)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public final void testSpatialNodeData() {
    try {
      new SpatialNodeData(1L, 10.0, 5.0);
      new SpatialNodeData(-10L, -10.0, -5.0);
      new SpatialNodeData(0L, 0.0, 0.0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
