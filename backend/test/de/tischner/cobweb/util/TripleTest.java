package de.tischner.cobweb.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link Triple}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TripleTest {
  /**
   * The triple used for testing.
   */
  private Triple<Integer, String, Boolean> mTriple;

  /**
   * Setups a triple instance for testing.
   */
  @Before
  public void setUp() {
    mTriple = new Triple<>(1, "a", true);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.Triple#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mTriple, mTriple);

    Triple<Integer, String, Boolean> other = new Triple<>(1, "a", true);
    Assert.assertEquals(mTriple, other);

    other = new Triple<>(1, "a", false);
    Assert.assertNotEquals(mTriple, other);

    other = new Triple<>(1, "aa", true);
    Assert.assertNotEquals(mTriple, other);

    other = new Triple<>(2, "a", true);
    Assert.assertNotEquals(mTriple, other);

    other = new Triple<>(null, null, null);
    Assert.assertNotEquals(mTriple, other);
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.Triple#getFirst()}.
   */
  @Test
  public void testGetFirst() {
    Assert.assertEquals(1, mTriple.getFirst().intValue());
    Assert.assertNull(new Triple<Integer, String, Boolean>(null, null, null).getFirst());
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.Triple#getSecond()}.
   */
  @Test
  public void testGetSecond() {
    Assert.assertEquals("a", mTriple.getSecond());
    Assert.assertNull(new Triple<Integer, String, Boolean>(null, null, null).getSecond());
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.Triple#getThird()}.
   */
  @Test
  public void testGetThird() {
    Assert.assertEquals(true, mTriple.getThird());
    Assert.assertNull(new Triple<Integer, String, Boolean>(null, null, null).getThird());
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.Triple#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mTriple.hashCode(), mTriple.hashCode());

    Triple<Integer, String, Boolean> other = new Triple<>(1, "a", true);
    Assert.assertEquals(mTriple.hashCode(), other.hashCode());

    other = new Triple<>(1, "a", false);
    Assert.assertNotEquals(mTriple.hashCode(), other.hashCode());

    other = new Triple<>(1, "aa", true);
    Assert.assertNotEquals(mTriple.hashCode(), other.hashCode());

    other = new Triple<>(2, "a", true);
    Assert.assertNotEquals(mTriple.hashCode(), other.hashCode());

    other = new Triple<>(null, null, null);
    Assert.assertNotEquals(mTriple.hashCode(), other.hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.Triple#Triple(java.lang.Object, java.lang.Object, java.lang.Object)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTriple() {
    try {
      new Triple<>(1, "a", true);
      new Triple<>(null, null, null);
      new Triple<>(5, "abc", null);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
