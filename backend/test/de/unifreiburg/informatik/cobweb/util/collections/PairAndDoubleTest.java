package de.unifreiburg.informatik.cobweb.util.collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link PairAndDouble}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class PairAndDoubleTest {
  /**
   * The pair used for testing.
   */
  private PairAndDouble<Integer, String> mPair;

  /**
   * Setups a pair instance for testing.
   */
  @Before
  public void setUp() {
    mPair = new PairAndDouble<>(1, "a", 5.0);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.PairAndDouble#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mPair, mPair);

    PairAndDouble<Integer, String> other = new PairAndDouble<>(1, "a", 5.0);
    Assert.assertEquals(mPair, other);

    other = new PairAndDouble<>(1, "a", -5.0);
    Assert.assertNotEquals(mPair, other);

    other = new PairAndDouble<>(1, "aa", 5.0);
    Assert.assertNotEquals(mPair, other);

    other = new PairAndDouble<>(2, "a", 5.0);
    Assert.assertNotEquals(mPair, other);

    other = new PairAndDouble<>(null, null, 0.0);
    Assert.assertNotEquals(mPair, other);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.PairAndDouble#getFirst()}.
   */
  @Test
  public void testGetFirst() {
    Assert.assertEquals(1, mPair.getFirst().intValue());
    Assert.assertNull(new PairAndDouble<Integer, String>(null, null, 0.0).getFirst());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.PairAndDouble#getSecond()}.
   */
  @Test
  public void testGetSecond() {
    Assert.assertEquals("a", mPair.getSecond());
    Assert.assertNull(new PairAndDouble<Integer, String>(null, null, 0.0).getSecond());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.PairAndDouble#getValue()}.
   */
  @Test
  public void testGetValue() {
    Assert.assertEquals(5.0, mPair.getValue(), 0.0001);
    Assert.assertEquals(0.0, new PairAndDouble<Integer, String>(null, null, 0.0).getValue(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.PairAndDouble#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mPair.hashCode(), mPair.hashCode());

    PairAndDouble<Integer, String> other = new PairAndDouble<>(1, "a", 5.0);
    Assert.assertEquals(mPair.hashCode(), other.hashCode());

    other = new PairAndDouble<>(1, "a", -5.0);
    Assert.assertNotEquals(mPair.hashCode(), other.hashCode());

    other = new PairAndDouble<>(1, "aa", 5.0);
    Assert.assertNotEquals(mPair.hashCode(), other.hashCode());

    other = new PairAndDouble<>(2, "a", 5.0);
    Assert.assertNotEquals(mPair.hashCode(), other.hashCode());

    other = new PairAndDouble<>(null, null, 0.0);
    Assert.assertNotEquals(mPair.hashCode(), other.hashCode());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.PairAndDouble#PairAndDouble(Object, Object, double)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testPairAndDouble() {
    try {
      new PairAndDouble<>(1, "a", 5.0);
      new PairAndDouble<>(null, null, 0.0);
      new PairAndDouble<>(-5, "abc", -5);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
