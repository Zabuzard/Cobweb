package de.tischner.cobweb.searching.nearest.server.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link NearestSearchResponse}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NearestSearchResponseTest {
  /**
   * The response used for testing.
   */
  private NearestSearchResponse mResponse;

  /**
   * Setups a response instance for testing.
   */
  @Before
  public void setUp() {
    mResponse = new NearestSearchResponse(10L, 1L, 1.0F, 1.0F);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.nearest.server.model.NearestSearchResponse#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(1L, mResponse.getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.nearest.server.model.NearestSearchResponse#getLatitude()}.
   */
  @Test
  public void testGetLatitude() {
    Assert.assertEquals(1.0F, mResponse.getLatitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.nearest.server.model.NearestSearchResponse#getLongitude()}.
   */
  @Test
  public void testGetLongitude() {
    Assert.assertEquals(1.0F, mResponse.getLongitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.nearest.server.model.NearestSearchResponse#getTime()}.
   */
  @Test
  public void testGetTime() {
    Assert.assertEquals(10L, mResponse.getTime());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.nearest.server.model.NearestSearchResponse#NearestSearchResponse(long, long, float, float)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNearestSearchResponse() {
    try {
      new NearestSearchResponse(10L, 1L, 1.0F, 1.0F);
      new NearestSearchResponse(0L, 0L, 0.0F, 0.0F);
      new NearestSearchResponse(5L, 5L, 5.0F, -5.0F);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
