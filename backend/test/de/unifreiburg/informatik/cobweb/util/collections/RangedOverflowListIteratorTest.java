package de.unifreiburg.informatik.cobweb.util.collections;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link RangedOverflowListIteratorTest}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RangedOverflowListIteratorTest {
  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.RangedListIterator#hasNext()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testHasNext() {
    RangedOverflowListIterator<Integer> iter = new RangedOverflowListIterator<>(Arrays.asList(1), 0, 1);
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertFalse(iter.hasNext());

    iter = new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2, 5);
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertFalse(iter.hasNext());

    iter = new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2, 4);
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertFalse(iter.hasNext());

    iter = new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 3, 2);
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertFalse(iter.hasNext());

    iter = new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 3, 0);
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertFalse(iter.hasNext());

    iter = new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2);
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertFalse(iter.hasNext());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.RangedListIterator#next()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNext() {
    RangedOverflowListIterator<Integer> iter = new RangedOverflowListIterator<>(Arrays.asList(1), 0, 1);
    Assert.assertEquals(1, iter.next().intValue());

    iter = new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2, 5);
    Assert.assertEquals(3, iter.next().intValue());
    Assert.assertEquals(4, iter.next().intValue());
    Assert.assertEquals(5, iter.next().intValue());

    iter = new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2, 4);
    Assert.assertEquals(3, iter.next().intValue());
    Assert.assertEquals(4, iter.next().intValue());

    iter = new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 3, 2);
    Assert.assertEquals(4, iter.next().intValue());
    Assert.assertEquals(5, iter.next().intValue());
    Assert.assertEquals(1, iter.next().intValue());
    Assert.assertEquals(2, iter.next().intValue());

    iter = new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 3, 0);
    Assert.assertEquals(4, iter.next().intValue());
    Assert.assertEquals(5, iter.next().intValue());

    iter = new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2);
    Assert.assertEquals(3, iter.next().intValue());
    Assert.assertEquals(4, iter.next().intValue());
    Assert.assertEquals(5, iter.next().intValue());
    Assert.assertEquals(1, iter.next().intValue());
    Assert.assertEquals(2, iter.next().intValue());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.RangedOverflowListIterator#RangedOverflowListIterator(java.util.List, int)}.
   */
  @SuppressWarnings({ "unused" })
  @Test
  public void testRangedListIteratorListInt() {
    try {
      new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 0);
      new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 4);
      new RangedOverflowListIterator<>(Arrays.asList(1), 0);
      new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 3);
      new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2);
    } catch (final Exception e) {
      Assert.fail();
    }

    expectIllegalArgumentException(
        () -> new RangedOverflowListIterator<>(new LinkedList<>(Arrays.asList(1, 2, 3, 4, 5)), 0));
    expectIllegalArgumentException(() -> new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), -1));
    expectIllegalArgumentException(() -> new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 5));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.RangedOverflowListIterator#RangedOverflowListIterator(java.util.List, int, int)}.
   */
  @SuppressWarnings({ "unused" })
  @Test
  public void testRangedListIteratorListIntInt() {
    try {
      new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 0, 5);
      new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 4, 5);
      new RangedOverflowListIterator<>(Arrays.asList(1), 0, 1);
      new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 3, 1);
      new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2, 2);
    } catch (final Exception e) {
      Assert.fail();
    }

    expectIllegalArgumentException(
        () -> new RangedOverflowListIterator<>(new LinkedList<>(Arrays.asList(1, 2, 3, 4, 5)), 0, 5));
    expectIllegalArgumentException(() -> new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), -1, 5));
    expectIllegalArgumentException(() -> new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 0, -1));
    expectIllegalArgumentException(() -> new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 5, 5));
    expectIllegalArgumentException(() -> new RangedOverflowListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 0, 6));
  }

  /**
   * Executes the given runnable and expects that it throws an
   * {@link IllegalArgumentException}. If not it acts like
   * {@link Assert#fail()}.
   *
   * @param runnable The runnable to execute that is expected to throw
   */
  @SuppressWarnings("static-method")
  private void expectIllegalArgumentException(final Runnable runnable) {
    boolean errorOccured = false;
    try {
      runnable.run();
    } catch (final IllegalArgumentException e) {
      errorOccured = true;
    }
    Assert.assertTrue(errorOccured);
  }

}
