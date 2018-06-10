package de.tischner.cobweb.searching.server.model;

import org.junit.Assert;
import org.junit.Test;

import de.tischner.cobweb.searching.name.server.model.NameSearchRequest;

/**
 * Test for the class {@link NameSearchRequest}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NameSearchRequestTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.server.model.NameSearchRequest#getAmount()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetAmount() {
    Assert.assertEquals(5, new NameSearchRequest("Wall street 5", 5).getAmount());
    Assert.assertEquals(3, new NameSearchRequest("Wall str", 3).getAmount());
    Assert.assertEquals(1, new NameSearchRequest("W", 1).getAmount());
    Assert.assertEquals(0, new NameSearchRequest("", 0).getAmount());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.server.model.NameSearchRequest#getName()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetName() {
    Assert.assertEquals("Wall street 5", new NameSearchRequest("Wall street 5", 1).getName());
    Assert.assertEquals("Wall str", new NameSearchRequest("Wall str", 1).getName());
    Assert.assertEquals("W", new NameSearchRequest("W", 1).getName());
    Assert.assertEquals("", new NameSearchRequest("", 1).getName());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.server.model.NameSearchRequest#NameSearchRequest(String, int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNameSearchRequest() {
    try {
      new NameSearchRequest("Wall street 5", 5);
      new NameSearchRequest("Wall str", 2);
      new NameSearchRequest("W", 1);
      new NameSearchRequest("", 0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
