package de.unifreiburg.informatik.cobweb.util.collections;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link TripletonIterator}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TripletonIteratorTest {

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.TripletonIterator#hasNext()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testHasNext() {
    Assert.assertFalse(
        new TripletonIterator<>(Stream.empty().iterator(), Stream.empty().iterator(), Stream.empty().iterator())
            .hasNext());
    Assert.assertTrue(new TripletonIterator<>(Stream.<Integer>empty().iterator(), Stream.of(-10, 5, 2).iterator(),
        Stream.of(6, 7, 4).iterator()).hasNext());
    Assert.assertTrue(new TripletonIterator<>(Stream.of(1, 2, 3).iterator(), Stream.<Integer>empty().iterator(),
        Stream.<Integer>empty().iterator()).hasNext());
    Assert.assertTrue(new TripletonIterator<>(Stream.of(1, 2, 3).iterator(), Stream.of(-10, 5, 2).iterator(),
        Stream.of(6, 7, 4).iterator()).hasNext());

    final TripletonIterator<Integer> iter = new TripletonIterator<>(Stream.of(1, 2, 3).iterator(),
        Stream.of(-10, 5, 2).iterator(), Stream.of(6, 7, 4).iterator());
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
   * {@link de.unifreiburg.informatik.cobweb.util.collections.TripletonIterator#next()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNext() {
    final TripletonIterator<Integer> first = new TripletonIterator<>(Stream.<Integer>empty().iterator(),
        Stream.of(-10, 5, 2).iterator(), Stream.of(6, 7, 4).iterator());
    Assert.assertEquals(-10, first.next().intValue());
    Assert.assertEquals(5, first.next().intValue());
    Assert.assertEquals(2, first.next().intValue());
    Assert.assertEquals(6, first.next().intValue());
    Assert.assertEquals(7, first.next().intValue());
    Assert.assertEquals(4, first.next().intValue());

    final TripletonIterator<Integer> second = new TripletonIterator<>(Stream.of(1, 2, 3).iterator(),
        Stream.<Integer>empty().iterator(), Stream.<Integer>empty().iterator());
    Assert.assertEquals(1, second.next().intValue());
    Assert.assertEquals(2, second.next().intValue());
    Assert.assertEquals(3, second.next().intValue());

    final TripletonIterator<Integer> third = new TripletonIterator<>(Stream.of(1, 2, 3).iterator(),
        Stream.of(-10, 5, 2).iterator(), Stream.of(6, 7, 4).iterator());
    Assert.assertEquals(1, third.next().intValue());
    Assert.assertEquals(2, third.next().intValue());
    Assert.assertEquals(3, third.next().intValue());
    Assert.assertEquals(-10, third.next().intValue());
    Assert.assertEquals(5, third.next().intValue());
    Assert.assertEquals(2, third.next().intValue());
    Assert.assertEquals(6, third.next().intValue());
    Assert.assertEquals(7, third.next().intValue());
    Assert.assertEquals(4, third.next().intValue());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.collections.TripletonIterator#TripletonIterator(java.util.Iterator, java.util.Iterator, java.util.Iterator)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTripletonIterator() {
    try {
      new TripletonIterator<>(Stream.of(1, 2, 3).iterator(), Stream.of(-10, 5, 2).iterator(),
          Stream.of(6, 7, 4).iterator());
      new TripletonIterator<>(Stream.empty().iterator(), Stream.empty().iterator(), Stream.empty().iterator());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
