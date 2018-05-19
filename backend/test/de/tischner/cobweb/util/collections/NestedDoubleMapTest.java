package de.tischner.cobweb.util.collections;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.impl.factory.primitive.ObjectDoubleMaps;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link NestedDoubleMap}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NestedDoubleMapTest {
  /**
   * The nested map used for testing.
   */
  private NestedDoubleMap<Integer, String> mMap;

  /**
   * Setups a nested map instance for testing.
   */
  @Before
  public void setUp() {
    mMap = new NestedDoubleMap<>();
    mMap.put(1, "a", 5.0);
    mMap.put(1, "b", 1.0);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#addAll(de.tischner.cobweb.util.collections.NestedDoubleMap)}.
   */
  @Test
  public void testAddAll() {
    final NestedDoubleMap<Integer, String> other = new NestedDoubleMap<>();
    other.put(2, "a", 5.0);
    other.put(1, "a", 1.0);

    mMap.addAll(other);
    Assert.assertEquals(1.0, mMap.get(1, "b"), 0.0001);
    Assert.assertEquals(1.0, mMap.get(1, "a"), 0.0001);
    Assert.assertEquals(5.0, mMap.get(2, "a"), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#clear()}.
   */
  @Test
  public void testClear() {
    Assert.assertEquals(5.0, mMap.get(1, "a"), 0.0001);

    mMap.clear();
    Assert.assertEquals(0.0, mMap.get(1, "a"), 0.0001);
    Assert.assertEquals(0.0, mMap.get(1, "b"), 0.0001);

    mMap.put(1, "a", 5.0);
    Assert.assertEquals(5.0, mMap.get(1, "a"), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#contains(Object, Object)}.
   */
  @Test
  public void testContains() {
    Assert.assertFalse(mMap.contains(2, "a"));
    Assert.assertTrue(mMap.contains(1, "a"));
    Assert.assertTrue(mMap.contains(1, "b"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#entrySet()}.
   */
  @Test
  public void testEntrySet() {
    final HashSet<PairAndDouble<Integer, String>> entries = new HashSet<>();
    mMap.entrySet().iterator().forEachRemaining(entries::add);
    Assert.assertEquals(2, entries.size());
    Assert.assertTrue(entries.contains(new PairAndDouble<>(1, "a", 5.0)));
    Assert.assertTrue(entries.contains(new PairAndDouble<>(1, "b", 1.0)));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mMap, mMap);

    NestedDoubleMap<Integer, String> other = new NestedDoubleMap<>();
    other.put(1, "a", 5.0);
    other.put(1, "b", 1.0);
    Assert.assertEquals(mMap, other);

    other = new NestedDoubleMap<>();
    other.put(1, "a", 5.0);
    other.put(1, "c", 1.0);
    Assert.assertNotEquals(mMap, other);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#get(java.lang.Object)}.
   */
  @Test
  public void testGetK1() {
    final MutableObjectDoubleMap<String> values = ObjectDoubleMaps.mutable.empty();
    values.put("a", 5.0);
    values.put("b", 1.0);

    Assert.assertEquals(values, mMap.get(1));
    Assert.assertNull(mMap.get(2));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#get(java.lang.Object, java.lang.Object)}.
   */
  @Test
  public void testGetK1K2() {
    Assert.assertEquals(5.0, mMap.get(1, "a"), 0.0001);
    Assert.assertEquals(1.0, mMap.get(1, "b"), 0.0001);
    Assert.assertEquals(0.0, mMap.get(1, "c"), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mMap.hashCode(), mMap.hashCode());

    NestedDoubleMap<Integer, String> other = new NestedDoubleMap<>();
    other.put(1, "a", 5.0);
    other.put(1, "b", 1.0);
    Assert.assertEquals(mMap.hashCode(), other.hashCode());

    other = new NestedDoubleMap<>();
    other.put(1, "a", 5.0);
    other.put(1, "c", 1.0);
    Assert.assertNotEquals(mMap.hashCode(), other.hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#keySet()}.
   */
  @Test
  public void testKeySet() {
    final Set<Integer> keys = mMap.keySet();
    Assert.assertEquals(1, keys.size());
    Assert.assertEquals(1, keys.iterator().next().intValue());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#NestedDoubleMap()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNestedDoubleMap() {
    try {
      new NestedDoubleMap<>();
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#NestedDoubleMap(int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNestedDoubleMapInt() {
    try {
      new NestedDoubleMap<>(10);
      new NestedDoubleMap<>(100);
      new NestedDoubleMap<>(0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#put(Object, Object, double)}.
   */
  @Test
  public void testPutK1K2V() {
    mMap.put(2, "a", 5.0);
    Assert.assertEquals(5.0, mMap.get(2, "a"), 0.0001);
    mMap.put(2, "a", 1.0);
    Assert.assertEquals(1.0, mMap.get(2, "a"), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#put(Object, MutableObjectDoubleMap)}.
   */
  @Test
  public void testPutK1MapOfK2V() {
    final MutableObjectDoubleMap<String> values = ObjectDoubleMaps.mutable.empty();
    values.put("a", 5.0);
    values.put("b", 1.0);
    Assert.assertNull(mMap.put(2, values));
    Assert.assertEquals(5.0, mMap.get(2, "a"), 0.0001);
    Assert.assertEquals(values, mMap.put(2, values));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#remove(java.lang.Object)}.
   */
  @Test
  public void testRemoveK1() {
    final MutableObjectDoubleMap<String> values = ObjectDoubleMaps.mutable.empty();
    values.put("a", 5.0);
    values.put("b", 1.0);

    mMap.put(2, "a", 5.0);
    Assert.assertEquals(values, mMap.remove(1));
    Assert.assertEquals(0.0, mMap.get(1, "a"), 0.0001);
    Assert.assertEquals(5.0, mMap.get(2, "a"), 0.0001);
    Assert.assertNull(mMap.remove(1));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#remove(java.lang.Object, java.lang.Object)}.
   */
  @Test
  public void testRemoveK1K2() {
    Assert.assertEquals(5.0, mMap.get(1, "a"), 0.0001);
    mMap.remove(1, "a");
    Assert.assertEquals(0.0, mMap.get(1, "a"), 0.0001);
    mMap.remove(1, "a");
    mMap.put(1, "a", 1.0);
    Assert.assertEquals(1.0, mMap.get(1, "a"), 0.0001);
    mMap.remove(1, "a");
    Assert.assertEquals(0.0, mMap.get(1, "a"), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedDoubleMap#setNestedInitialCapacity(int)}.
   */
  @Test
  public void testSetNestedInitialCapacity() {
    try {
      mMap.setNestedInitialCapacity(10);
      mMap.setNestedInitialCapacity(100);
      mMap.setNestedInitialCapacity(0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
