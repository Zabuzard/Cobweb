package de.tischner.cobweb.searching.nearest.server.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link NearestSearchRequest}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NearestSearchRequestTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.nearest.server.model.NearestSearchRequest#getLatitude()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetLatitude() {
    Assert.assertEquals(1.0F, new NearestSearchRequest(1.0F, 1.0F).getLatitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.nearest.server.model.NearestSearchRequest#getLongitude()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetLongitude() {
    Assert.assertEquals(1.0F, new NearestSearchRequest(1.0F, 1.0F).getLongitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.nearest.server.model.NearestSearchRequest#NearestSearchRequest(float, float)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNearestSearchRequest() {
    try {
      new NearestSearchRequest(1.0F, 1.0F);
      new NearestSearchRequest(-5.0F, 5.0F);
      new NearestSearchRequest(0.0F, 0.0F);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
