package de.tischner.cobweb.searching.name.server.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link Match}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class MatchTest {
  /**
   * The match used for testing.
   */
  private Match mMatch;

  /**
   * Setups a match instance for testing.
   */
  @Before
  public void setUp() {
    mMatch = new Match(1L, "Wall street 5");
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.server.model.Match#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(1L, mMatch.getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.server.model.Match#getName()}.
   */
  @Test
  public void testGetName() {
    Assert.assertEquals("Wall street 5", mMatch.getName());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.server.model.Match#Match(long, java.lang.String)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testMatch() {
    try {
      new Match(1L, "Wall street 5");
      new Match(-1L, "W");
      new Match(0L, "");
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
