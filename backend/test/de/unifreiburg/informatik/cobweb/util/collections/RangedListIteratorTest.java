package de.unifreiburg.informatik.cobweb.util.collections;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link RangedListIterator}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RangedListIteratorTest {
  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.RangedListIterator#hasNext()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testHasNext() {
    RangedListIterator<Integer> iter = new RangedListIterator<>(Arrays.asList(1), 0, 1);
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertFalse(iter.hasNext());

    iter = new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2, 5);
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertFalse(iter.hasNext());

    iter = new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2, 4);
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
    RangedListIterator<Integer> iter = new RangedListIterator<>(Arrays.asList(1), 0, 1);
    Assert.assertEquals(1, iter.next().intValue());

    iter = new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2, 5);
    Assert.assertEquals(3, iter.next().intValue());
    Assert.assertEquals(4, iter.next().intValue());
    Assert.assertEquals(5, iter.next().intValue());

    iter = new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 2, 4);
    Assert.assertEquals(3, iter.next().intValue());
    Assert.assertEquals(4, iter.next().intValue());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.RangedListIterator#RangedListIterator(java.util.List, int, int)}.
   */
  @SuppressWarnings({ "unused" })
  @Test
  public void testRangedListIterator() {
    try {
      new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 0, 5);
      new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 4, 5);
      new RangedListIterator<>(Arrays.asList(1), 0, 1);
    } catch (final Exception e) {
      Assert.fail();
    }

    expectIllegalArgumentException(
        () -> new RangedListIterator<>(new LinkedList<>(Arrays.asList(1, 2, 3, 4, 5)), 0, 5));
    expectIllegalArgumentException(() -> new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), -1, 5));
    expectIllegalArgumentException(() -> new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 0, -1));
    expectIllegalArgumentException(() -> new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 5, 5));
    expectIllegalArgumentException(() -> new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 0, 6));
    expectIllegalArgumentException(() -> new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 3, 2));
    expectIllegalArgumentException(() -> new RangedListIterator<>(Arrays.asList(1, 2, 3, 4, 5), 3, 3));
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
