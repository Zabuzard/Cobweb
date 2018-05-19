package de.tischner.cobweb.util.collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link Pair}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class PairTest {
  /**
   * The pair used for testing.
   */
  private Pair<Integer, String> mPair;

  /**
   * Setups a pair instance for testing.
   */
  @Before
  public void setUp() {
    mPair = new Pair<>(1, "a");
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.Pair#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mPair, mPair);

    Pair<Integer, String> other = new Pair<>(1, "a");
    Assert.assertEquals(mPair, other);

    other = new Pair<>(1, "aa");
    Assert.assertNotEquals(mPair, other);

    other = new Pair<>(2, "a");
    Assert.assertNotEquals(mPair, other);

    other = new Pair<>(null, null);
    Assert.assertNotEquals(mPair, other);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.Pair#getFirst()}.
   */
  @Test
  public void testGetFirst() {
    Assert.assertEquals(1, mPair.getFirst().intValue());
    Assert.assertNull(new Pair<Integer, String>(null, null).getFirst());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.Pair#getSecond()}.
   */
  @Test
  public void testGetSecond() {
    Assert.assertEquals("a", mPair.getSecond());
    Assert.assertNull(new Pair<Integer, String>(null, null).getSecond());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.Pair#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mPair.hashCode(), mPair.hashCode());

    Pair<Integer, String> other = new Pair<>(1, "a");
    Assert.assertEquals(mPair.hashCode(), other.hashCode());

    other = new Pair<>(1, "aa");
    Assert.assertNotEquals(mPair.hashCode(), other.hashCode());

    other = new Pair<>(2, "a");
    Assert.assertNotEquals(mPair.hashCode(), other.hashCode());

    other = new Pair<>(null, null);
    Assert.assertNotEquals(mPair.hashCode(), other.hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.Pair#Pair(java.lang.Object, java.lang.Object)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testPair() {
    try {
      new Pair<>(1, "a");
      new Pair<>(null, null);
      new Pair<>(5, "abc");
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
