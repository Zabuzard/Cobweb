package de.tischner.cobweb.util.collections;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link DoubletonIterator}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class DoubletonIteratorTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonIterator#DoubletonIterator(java.util.Iterator, java.util.Iterator)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testDoubletonIterator() {
    try {
      new DoubletonIterator<>(Stream.of(1, 2, 3).iterator(), Stream.of(-10, 5, 2).iterator());
      new DoubletonIterator<>(Stream.empty().iterator(), Stream.empty().iterator());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonIterator#hasNext()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testHasNext() {
    Assert.assertFalse(new DoubletonIterator<>(Stream.empty().iterator(), Stream.empty().iterator()).hasNext());
    Assert.assertTrue(
        new DoubletonIterator<>(Stream.<Integer>empty().iterator(), Stream.of(-10, 5, 2).iterator()).hasNext());
    Assert.assertTrue(
        new DoubletonIterator<>(Stream.of(1, 2, 3).iterator(), Stream.<Integer>empty().iterator()).hasNext());
    Assert
        .assertTrue(new DoubletonIterator<>(Stream.of(1, 2, 3).iterator(), Stream.of(-10, 5, 2).iterator()).hasNext());

    final DoubletonIterator<Integer> iter =
        new DoubletonIterator<>(Stream.of(1, 2, 3).iterator(), Stream.of(-10, 5, 2).iterator());
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
    Assert.assertTrue(iter.hasNext());
    iter.next();
    Assert.assertFalse(iter.hasNext());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonIterator#next()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNext() {
    final DoubletonIterator<Integer> first =
        new DoubletonIterator<>(Stream.<Integer>empty().iterator(), Stream.of(-10, 5, 2).iterator());
    Assert.assertEquals(-10, first.next().intValue());
    Assert.assertEquals(5, first.next().intValue());
    Assert.assertEquals(2, first.next().intValue());

    final DoubletonIterator<Integer> second =
        new DoubletonIterator<>(Stream.of(1, 2, 3).iterator(), Stream.<Integer>empty().iterator());
    Assert.assertEquals(1, second.next().intValue());
    Assert.assertEquals(2, second.next().intValue());
    Assert.assertEquals(3, second.next().intValue());

    final DoubletonIterator<Integer> third =
        new DoubletonIterator<>(Stream.of(1, 2, 3).iterator(), Stream.of(-10, 5, 2).iterator());
    Assert.assertEquals(1, third.next().intValue());
    Assert.assertEquals(2, third.next().intValue());
    Assert.assertEquals(3, third.next().intValue());
    Assert.assertEquals(-10, third.next().intValue());
    Assert.assertEquals(5, third.next().intValue());
    Assert.assertEquals(2, third.next().intValue());
  }

}
