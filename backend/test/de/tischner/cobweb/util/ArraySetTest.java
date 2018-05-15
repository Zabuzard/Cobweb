package de.tischner.cobweb.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link ArraySet}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ArraySetTest {
  /**
   * The set in array-mode used for testing.
   */
  private ArraySet<Integer> mArrayMode;
  /**
   * The set in set-mode used for testing.
   */
  private ArraySet<Integer> mSetMode;

  /**
   * Setups array set instances for testing.
   */
  @Before
  public void setUp() {
    mArrayMode = new ArraySet<>(1, 2, 3, null);
    mSetMode = new ArraySet<>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, null);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.ArraySet#add(java.lang.Object)}.
   */
  @Test
  public void testAdd() {
    Assert.assertFalse(mArrayMode.contains(5));
    Assert.assertTrue(mArrayMode.add(5));
    Assert.assertTrue(mArrayMode.contains(5));
    Assert.assertEquals(5, mArrayMode.size());

    Assert.assertFalse(mArrayMode.add(5));
    Assert.assertTrue(mArrayMode.contains(5));
    Assert.assertEquals(5, mArrayMode.size());

    Assert.assertFalse(mArrayMode.add(null));
    Assert.assertTrue(mArrayMode.contains(null));
    Assert.assertEquals(5, mArrayMode.size());

    Assert.assertTrue(mArrayMode.add(6));
    Assert.assertTrue(mArrayMode.add(7));
    Assert.assertTrue(mArrayMode.add(8));
    Assert.assertTrue(mArrayMode.add(9));

    Assert.assertFalse(mSetMode.contains(12));
    Assert.assertTrue(mSetMode.add(12));
    Assert.assertTrue(mSetMode.contains(12));
    Assert.assertEquals(12, mSetMode.size());

    Assert.assertFalse(mSetMode.add(12));
    Assert.assertTrue(mSetMode.contains(12));
    Assert.assertEquals(12, mSetMode.size());

    Assert.assertFalse(mSetMode.add(null));
    Assert.assertTrue(mSetMode.contains(null));
    Assert.assertEquals(12, mSetMode.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.ArraySet#addAll(java.util.Collection)}.
   */
  @Test
  public void testAddAll() {
    Assert.assertFalse(mArrayMode.addAll(Arrays.asList(1, 2, 3, null)));
    Assert.assertEquals(4, mArrayMode.size());

    Assert.assertFalse(mArrayMode.addAll(Collections.emptyList()));
    Assert.assertEquals(4, mArrayMode.size());

    Assert.assertTrue(mArrayMode.addAll(Arrays.asList(1, 2, 2, 5, 5, 6)));
    Assert.assertEquals(6, mArrayMode.size());

    Assert.assertTrue(mArrayMode.addAll(Arrays.asList(5, 6, 7, 8, 9)));
    Assert.assertEquals(9, mArrayMode.size());

    Assert.assertFalse(mSetMode.addAll(Arrays.asList(1, 2, 3, null)));
    Assert.assertEquals(11, mSetMode.size());

    Assert.assertFalse(mSetMode.addAll(Collections.emptyList()));
    Assert.assertEquals(11, mSetMode.size());

    Assert.assertTrue(mSetMode.addAll(Arrays.asList(1, 2, 2, 12, 12, 13)));
    Assert.assertEquals(13, mSetMode.size());

    Assert.assertTrue(mSetMode.addAll(Arrays.asList(12, 13, 14, 14, 15)));
    Assert.assertEquals(15, mSetMode.size());
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.ArraySet#ArraySet()}.
   */
  @SuppressWarnings({ "static-method" })
  @Test
  public void testArraySet() {
    try {
      Assert.assertTrue(new ArraySet<>().isEmpty());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.ArraySet#ArraySet(java.util.Collection)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testArraySetCollectionOfE() {
    try {
      Assert.assertEquals(3, new ArraySet<>(Arrays.asList(1, 2, 3)).size());
      Assert.assertEquals(1, new ArraySet<>(Collections.singletonList(1)).size());
      Assert.assertTrue(new ArraySet<>(Collections.emptyList()).isEmpty());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.ArraySet#ArraySet(java.lang.Object)}.
   */
  @SuppressWarnings({ "static-method" })
  @Test
  public void testArraySetE() {
    try {
      Assert.assertEquals(1, new ArraySet<>(1).size());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.ArraySet#ArraySet(Object...)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testArraySetEArray() {
    try {
      Assert.assertEquals(3, new ArraySet<>(1, 2, 3).size());
      Assert.assertTrue(new ArraySet<>(new Integer[0]).isEmpty());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.ArraySet#clear()}.
   */
  @Test
  public void testClear() {
    Assert.assertFalse(mArrayMode.isEmpty());
    mArrayMode.clear();
    Assert.assertTrue(mArrayMode.isEmpty());

    Assert.assertFalse(mSetMode.isEmpty());
    mSetMode.clear();
    Assert.assertTrue(mSetMode.isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.ArraySet#contains(java.lang.Object)}.
   */
  @Test
  public void testContains() {
    Assert.assertTrue(mArrayMode.contains(1));
    Assert.assertTrue(mArrayMode.contains(null));
    Assert.assertFalse(mArrayMode.contains(4));

    Assert.assertTrue(mSetMode.contains(1));
    Assert.assertTrue(mSetMode.contains(null));
    Assert.assertFalse(mSetMode.contains(11));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.ArraySet#containsAll(java.util.Collection)}.
   */
  @Test
  public void testContainsAll() {
    Assert.assertTrue(mArrayMode.containsAll(Arrays.asList(1, 2, 3, null, 3, 3, 2)));
    Assert.assertTrue(mArrayMode.containsAll(Arrays.asList(2, null)));
    Assert.assertTrue(mArrayMode.containsAll(Arrays.asList(1)));
    Assert.assertTrue(mArrayMode.containsAll(Collections.emptyList()));
    Assert.assertFalse(mArrayMode.containsAll(Arrays.asList(1, 2, 3, 5, null)));

    Assert.assertTrue(mSetMode.containsAll(Arrays.asList(1, 2, 3, null, 3, 3, 2)));
    Assert.assertTrue(mSetMode.containsAll(Arrays.asList(2, null)));
    Assert.assertTrue(mSetMode.containsAll(Arrays.asList(1)));
    Assert.assertTrue(mSetMode.containsAll(Collections.emptyList()));
    Assert.assertFalse(mSetMode.containsAll(Arrays.asList(1, 2, 3, 12, null)));
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.ArraySet#isEmpty()}.
   */
  @Test
  public void testIsEmpty() {
    Assert.assertFalse(mArrayMode.isEmpty());
    Assert.assertFalse(mSetMode.isEmpty());
    Assert.assertTrue(new ArraySet<>().isEmpty());
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.ArraySet#iterator()}.
   */
  @Test
  public void testIterator() {
    Set<Integer> values = new HashSet<>();
    mArrayMode.iterator().forEachRemaining(values::add);
    Assert.assertTrue(values.containsAll(Arrays.asList(1, 2, 3, null)));
    Assert.assertEquals(4, values.size());

    values = new HashSet<>();
    mSetMode.iterator().forEachRemaining(values::add);
    Assert.assertTrue(values.containsAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, null)));
    Assert.assertEquals(11, values.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.ArraySet#remove(java.lang.Object)}.
   */
  @Test
  public void testRemove() {
    Assert.assertTrue(mArrayMode.contains(3));
    Assert.assertTrue(mArrayMode.remove(3));
    Assert.assertFalse(mArrayMode.contains(3));
    Assert.assertEquals(3, mArrayMode.size());

    Assert.assertFalse(mArrayMode.remove(3));
    Assert.assertFalse(mArrayMode.contains(3));
    Assert.assertEquals(3, mArrayMode.size());

    Assert.assertTrue(mArrayMode.remove(null));
    Assert.assertFalse(mArrayMode.contains(null));
    Assert.assertEquals(2, mArrayMode.size());

    Assert.assertTrue(mSetMode.contains(10));
    Assert.assertTrue(mSetMode.remove(10));
    Assert.assertFalse(mSetMode.contains(10));
    Assert.assertEquals(10, mSetMode.size());

    Assert.assertFalse(mSetMode.remove(10));
    Assert.assertFalse(mSetMode.contains(10));
    Assert.assertEquals(10, mSetMode.size());

    Assert.assertTrue(mSetMode.remove(null));
    Assert.assertFalse(mSetMode.contains(null));
    Assert.assertEquals(9, mSetMode.size());

    Assert.assertTrue(mSetMode.remove(1));
    Assert.assertTrue(mSetMode.remove(2));
    Assert.assertTrue(mSetMode.remove(3));
    Assert.assertTrue(mSetMode.remove(4));
    Assert.assertTrue(mSetMode.remove(5));
    Assert.assertTrue(mSetMode.remove(6));
    Assert.assertTrue(mSetMode.remove(7));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.ArraySet#removeAll(java.util.Collection)}.
   */
  @Test
  public void testRemoveAll() {
    Assert.assertFalse(mArrayMode.removeAll(Collections.emptyList()));
    Assert.assertEquals(4, mArrayMode.size());

    Assert.assertFalse(mArrayMode.removeAll(Arrays.asList(5, 5, 6)));
    Assert.assertEquals(4, mArrayMode.size());

    Assert.assertTrue(mArrayMode.removeAll(Arrays.asList(1, null, null, 5, 6)));
    Assert.assertEquals(2, mArrayMode.size());

    Assert.assertTrue(mArrayMode.removeAll(Arrays.asList(1, null, null, 5, 6, 2, 3, 3)));
    Assert.assertTrue(mArrayMode.isEmpty());

    Assert.assertFalse(mSetMode.removeAll(Collections.emptyList()));
    Assert.assertEquals(11, mSetMode.size());

    Assert.assertFalse(mSetMode.removeAll(Arrays.asList(12, 12, 13)));
    Assert.assertEquals(11, mSetMode.size());

    Assert.assertTrue(mSetMode.removeAll(Arrays.asList(1, null, null, 12, 12, 13)));
    Assert.assertEquals(9, mSetMode.size());

    Assert.assertTrue(mSetMode.removeAll(Arrays.asList(1, null, null, 12, 13, 2, 3, 3)));
    Assert.assertEquals(7, mSetMode.size());

    Assert.assertTrue(mSetMode.removeAll(Arrays.asList(4, 5, 6, 7)));
    Assert.assertEquals(3, mSetMode.size());

    Assert.assertTrue(mSetMode.removeAll(Arrays.asList(8, 9, 10, 10, 9, 8)));
    Assert.assertTrue(mSetMode.isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.ArraySet#retainAll(java.util.Collection)}.
   */
  @Test
  public void testRetainAll() {
    mArrayMode.retainAll(Arrays.asList(1, 2, 3, null));
    Assert.assertTrue(mArrayMode.containsAll(Arrays.asList(1, 2, 3, null)));

    mArrayMode.retainAll(Arrays.asList(1, 2, 6, 7));
    Assert.assertTrue(mArrayMode.containsAll(Arrays.asList(1, 2)));
    Assert.assertEquals(2, mArrayMode.size());

    mArrayMode.retainAll(Collections.emptyList());
    Assert.assertTrue(mArrayMode.isEmpty());

    mSetMode.retainAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, null));
    Assert.assertTrue(mSetMode.containsAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, null)));

    mSetMode.retainAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 15, 16));
    Assert.assertTrue(mSetMode.containsAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
    Assert.assertEquals(7, mSetMode.size());

    mSetMode.retainAll(Arrays.asList(4, 5, 6));
    Assert.assertTrue(mSetMode.containsAll(Arrays.asList(4, 5, 6)));
    Assert.assertEquals(3, mSetMode.size());

    mSetMode.retainAll(Collections.emptyList());
    Assert.assertTrue(mSetMode.isEmpty());
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.ArraySet#size()}.
   */
  @Test
  public void testSize() {
    Assert.assertEquals(4, mArrayMode.size());
    Assert.assertEquals(11, mSetMode.size());
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.ArraySet#toArray()}.
   */
  @Test
  public void testToArray() {
    Assert.assertEquals(new HashSet<>(Arrays.asList(1, 2, 3, null)),
        new HashSet<>(Arrays.asList(mArrayMode.toArray())));
    Assert.assertEquals(new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, null)),
        new HashSet<>(Arrays.asList(mSetMode.toArray())));
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.ArraySet#toArray(Object[])}.
   */
  @Test
  public void testToArrayTArray() {
    Assert.assertEquals(new HashSet<>(Arrays.asList(1, 2, 3, null)),
        new HashSet<>(Arrays.asList(mArrayMode.toArray(new Integer[0]))));
    Assert.assertEquals(new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, null)),
        new HashSet<>(Arrays.asList(mSetMode.toArray(new Integer[0]))));
  }

}
