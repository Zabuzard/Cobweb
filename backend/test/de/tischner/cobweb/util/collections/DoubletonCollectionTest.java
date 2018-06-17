package de.tischner.cobweb.util.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link DoubletonCollection}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class DoubletonCollectionTest {
  /**
   * The collection used for testing.
   */
  private DoubletonCollection<Integer> mCollection;

  /**
   * Setups a collection instance for testing.
   */
  @Before
  public void setUp() {
    final ArrayList<Integer> first = new ArrayList<>();
    first.add(1);
    first.add(2);
    first.add(3);

    final ArrayList<Integer> second = new ArrayList<>();
    second.add(-10);
    second.add(5);
    second.add(2);
    mCollection = new DoubletonCollection<>(first, second);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#add(java.lang.Object)}.
   */
  @Test
  public void testAdd() {
    Assert.assertTrue(mCollection.add(500));
    Assert.assertTrue(mCollection.contains(500));
    Assert.assertEquals(7, mCollection.size());

    Assert.assertTrue(mCollection.add(500));
    Assert.assertTrue(mCollection.contains(500));
    Assert.assertEquals(8, mCollection.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#addAll(java.util.Collection)}.
   */
  @Test
  public void testAddAll() {
    Assert.assertTrue(mCollection.addAll(Arrays.asList(500, 501, 502)));
    Assert.assertTrue(mCollection.contains(500));
    Assert.assertTrue(mCollection.contains(501));
    Assert.assertTrue(mCollection.contains(502));
    Assert.assertEquals(9, mCollection.size());

    Assert.assertTrue(mCollection.addAll(Arrays.asList(600)));
    Assert.assertTrue(mCollection.contains(600));
    Assert.assertEquals(10, mCollection.size());

    Assert.assertFalse(mCollection.addAll(Arrays.asList()));
    Assert.assertEquals(10, mCollection.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#clear()}.
   */
  @Test
  public void testClear() {
    mCollection.clear();
    Assert.assertTrue(mCollection.isEmpty());
    Assert.assertFalse(mCollection.contains(1));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#contains(java.lang.Object)}.
   */
  @Test
  public void testContains() {
    Assert.assertTrue(mCollection.contains(1));
    Assert.assertTrue(mCollection.contains(2));

    Assert.assertFalse(mCollection.contains(500));
    mCollection.add(500);
    Assert.assertTrue(mCollection.contains(500));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#containsAll(java.util.Collection)}.
   */
  @Test
  public void testContainsAll() {
    boolean isUnsupported = false;
    try {
      mCollection.containsAll(Arrays.asList(1, 2, 3));
    } catch (final UnsupportedOperationException e) {
      isUnsupported = true;
    }
    Assert.assertTrue(isUnsupported);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#DoubletonCollection(java.util.Collection, java.util.Collection)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testDoubletonCollection() {
    try {
      new DoubletonCollection<>(Arrays.asList(1, 2, 3), Arrays.asList(-10, 5, 2));
      new DoubletonCollection<>(Arrays.asList(), Arrays.asList());
      new DoubletonCollection<>(Arrays.asList(1, 2, 3), Arrays.asList());
      new DoubletonCollection<>(Arrays.asList(), Arrays.asList(-10, 5, 2));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#isEmpty()}.
   */
  @Test
  public void testIsEmpty() {
    Assert.assertFalse(mCollection.isEmpty());
    mCollection.clear();
    Assert.assertTrue(mCollection.isEmpty());

    mCollection.add(1);
    Assert.assertFalse(mCollection.isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#iterator()}.
   */
  @Test
  public void testIterator() {
    Iterator<Integer> iter = mCollection.iterator();
    Assert.assertTrue(iter.hasNext());
    Assert.assertEquals(1, iter.next().intValue());
    Assert.assertTrue(iter.hasNext());
    Assert.assertEquals(2, iter.next().intValue());
    Assert.assertTrue(iter.hasNext());
    Assert.assertEquals(3, iter.next().intValue());
    Assert.assertTrue(iter.hasNext());
    Assert.assertEquals(-10, iter.next().intValue());
    Assert.assertTrue(iter.hasNext());
    Assert.assertEquals(5, iter.next().intValue());
    Assert.assertTrue(iter.hasNext());
    Assert.assertEquals(2, iter.next().intValue());
    Assert.assertFalse(iter.hasNext());

    mCollection.clear();
    iter = mCollection.iterator();
    Assert.assertFalse(iter.hasNext());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#remove(java.lang.Object)}.
   */
  @Test
  public void testRemove() {
    Assert.assertTrue(mCollection.remove(2));
    Assert.assertTrue(mCollection.contains(2));
    Assert.assertEquals(5, mCollection.size());

    Assert.assertTrue(mCollection.remove(2));
    Assert.assertFalse(mCollection.contains(2));
    Assert.assertEquals(4, mCollection.size());

    Assert.assertFalse(mCollection.remove(2));
    Assert.assertFalse(mCollection.contains(2));
    Assert.assertEquals(4, mCollection.size());

    Assert.assertFalse(mCollection.remove(500));
    mCollection.add(500);
    Assert.assertTrue(mCollection.remove(500));
    Assert.assertEquals(4, mCollection.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#removeAll(java.util.Collection)}.
   */
  @Test
  public void testRemoveAll() {
    Assert.assertTrue(mCollection.removeAll(Arrays.asList(2, 500)));
    Assert.assertFalse(mCollection.contains(2));
    Assert.assertEquals(4, mCollection.size());

    Assert.assertFalse(mCollection.removeAll(Arrays.asList(2, 500)));
    Assert.assertFalse(mCollection.removeAll(Arrays.asList()));

    Assert.assertTrue(mCollection.removeAll(Arrays.asList(1, 2, 3)));
    Assert.assertFalse(mCollection.contains(1));
    Assert.assertFalse(mCollection.contains(2));
    Assert.assertFalse(mCollection.contains(3));
    Assert.assertEquals(2, mCollection.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#retainAll(java.util.Collection)}.
   */
  @Test
  public void testRetainAll() {
    Assert.assertFalse(mCollection.retainAll(Arrays.asList(1, 2, 3, -10, 5, 10, 500)));
    Assert.assertEquals(6, mCollection.size());

    Assert.assertTrue(mCollection.retainAll(Arrays.asList(1, 2, 3)));
    Assert.assertEquals(4, mCollection.size());

    Assert.assertTrue(mCollection.retainAll(Arrays.asList(500)));
    Assert.assertTrue(mCollection.isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#size()}.
   */
  @Test
  public void testSize() {
    Assert.assertEquals(6, mCollection.size());

    mCollection.add(500);
    Assert.assertEquals(7, mCollection.size());
    mCollection.remove(500);
    Assert.assertEquals(6, mCollection.size());

    mCollection.clear();
    Assert.assertEquals(0, mCollection.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#toArray()}.
   */
  @Test
  public void testToArray() {
    ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, -10, 5, 2));
    Assert.assertArrayEquals(expected.toArray(), mCollection.toArray());

    expected = new ArrayList<>(Arrays.asList());
    mCollection.clear();
    Assert.assertArrayEquals(expected.toArray(), mCollection.toArray());

    expected = new ArrayList<>(Arrays.asList(5));
    mCollection.add(5);
    Assert.assertArrayEquals(expected.toArray(), mCollection.toArray());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.DoubletonCollection#toArray(Object[])}.
   */
  @Test
  public void testToArrayTArray() {
    ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, -10, 5, 2));
    Assert.assertArrayEquals(expected.toArray(new Integer[0]), mCollection.toArray(new Integer[0]));

    expected = new ArrayList<>(Arrays.asList());
    mCollection.clear();
    Assert.assertArrayEquals(expected.toArray(new Integer[0]), mCollection.toArray(new Integer[0]));

    expected = new ArrayList<>(Arrays.asList(5));
    mCollection.add(5);
    Assert.assertArrayEquals(expected.toArray(new Integer[0]), mCollection.toArray(new Integer[0]));
  }

}
