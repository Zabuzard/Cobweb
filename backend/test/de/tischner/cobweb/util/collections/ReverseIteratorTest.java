package de.tischner.cobweb.util.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link ReverseIterator}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ReverseIteratorTest {
  /**
   * The reverse iterator used for testing.
   */
  private ReverseIterator<Integer> mIter;

  /**
   * Setups a reverse iterator instance for testing.
   */
  @Before
  public void setUp() {
    mIter = new ReverseIterator<>(Arrays.asList(1, 2, 3));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ReverseIterator#hasNext()}.
   */
  @Test
  public void testHasNext() {
    Assert.assertTrue(mIter.hasNext());
    mIter.next();
    Assert.assertTrue(mIter.hasNext());
    mIter.next();
    Assert.assertTrue(mIter.hasNext());
    mIter.next();
    Assert.assertFalse(mIter.hasNext());

    mIter = new ReverseIterator<>(Collections.emptyList());
    Assert.assertFalse(mIter.hasNext());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ReverseIterator#next()}.
   */
  @Test
  public void testNext() {
    Assert.assertEquals(3, mIter.next().intValue());
    Assert.assertEquals(2, mIter.next().intValue());
    Assert.assertEquals(1, mIter.next().intValue());

    mIter = new ReverseIterator<>(Collections.singletonList(1));
    Assert.assertEquals(1, mIter.next().intValue());

    boolean wasExceptionThrown = false;
    try {
      new ReverseIterator<>(Collections.emptyList()).next();
    } catch (final NoSuchElementException e) {
      wasExceptionThrown = true;
    }
    Assert.assertTrue(wasExceptionThrown);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ReverseIterator#ReverseIterator(java.util.List)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testReverseIterator() {
    try {
      new ReverseIterator<>(Arrays.asList(1, 2, 3));
      new ReverseIterator<>(Collections.singletonList(1));
      new ReverseIterator<>(Collections.emptyList());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
