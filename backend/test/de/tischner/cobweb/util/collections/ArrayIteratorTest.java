package de.tischner.cobweb.util.collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link ArrayIterator}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ArrayIteratorTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayIterator#ArrayIterator(java.lang.Object[])}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testArrayIteratorObjectArray() {
    try {
      new ArrayIterator<>(new Integer[] { 1, 2, 3 });
      new ArrayIterator<>(new Integer[] {});
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayIterator#ArrayIterator(java.lang.Object[], int, int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testArrayIteratorObjectArrayIntInt() {
    try {
      new ArrayIterator<>(new Integer[] { 1, 2, 3 }, 0, 3);
      new ArrayIterator<>(new Integer[] { 1, 2, 3 }, 0, 5);
      new ArrayIterator<>(new Integer[] { 1, 2, 3 }, 2, 2);
      new ArrayIterator<>(new Integer[] { 1, 2, 3 }, 1, 2);
      new ArrayIterator<>(new Integer[] {}, 0, 0);
      new ArrayIterator<>(new Integer[] {}, 1, 2);
      new ArrayIterator<>(new Integer[] {}, -1, 5);
      new ArrayIterator<>(new Integer[] {}, 3, 1);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayIterator#hasNext()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testHasNext() {
    Assert.assertFalse(new ArrayIterator<>(new Integer[] {}).hasNext());
    Assert.assertFalse(new ArrayIterator<>(new Integer[] { 1, 2, 3 }, 2, 2).hasNext());
    Assert.assertFalse(new ArrayIterator<>(new Integer[] { 1, 2, 3 }, 3, 2).hasNext());

    final ArrayIterator<Integer> iter = new ArrayIterator<>(new Integer[] { 1, 2, 3 }, 1, 3);
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertFalse(iter.hasNext());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayIterator#next()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNext() {
    Assert.assertEquals(3, new ArrayIterator<>(new Integer[] { 1, 2, 3 }, 2, 3).next());

    final ArrayIterator<Integer> iter = new ArrayIterator<>(new Integer[] { 1, 2, 3 }, 1, 3);
    Assert.assertEquals(2, iter.next().intValue());
    Assert.assertEquals(3, iter.next().intValue());
  }

}
