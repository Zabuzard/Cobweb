package de.tischner.cobweb.util.collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link NestedMap}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NestedMapTest {
  /**
   * The nested map used for testing.
   */
  private NestedMap<Integer, String, Boolean> mMap;

  /**
   * Setups a nested map instance for testing.
   */
  @Before
  public void setUp() {
    mMap = new NestedMap<>();
    mMap.put(1, "a", true);
    mMap.put(1, "b", false);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#addAll(de.tischner.cobweb.util.collections.NestedMap)}.
   */
  @Test
  public void testAddAll() {
    final NestedMap<Integer, String, Boolean> other = new NestedMap<>();
    other.put(2, "a", true);
    other.put(1, "a", false);

    mMap.addAll(other);
    Assert.assertEquals(false, mMap.get(1, "b"));
    Assert.assertEquals(false, mMap.get(1, "a"));
    Assert.assertEquals(true, mMap.get(2, "a"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#clear()}.
   */
  @Test
  public void testClear() {
    Assert.assertEquals(true, mMap.get(1, "a"));

    mMap.clear();
    Assert.assertNull(mMap.get(1, "a"));
    Assert.assertNull(mMap.get(1, "b"));

    mMap.put(1, "a", true);
    Assert.assertEquals(true, mMap.get(1, "a"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#contains(Object, Object)}.
   */
  @Test
  public void testContains() {
    Assert.assertFalse(mMap.contains(2, "a"));
    Assert.assertTrue(mMap.contains(1, "a"));
    Assert.assertTrue(mMap.contains(1, "b"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#entrySet()}.
   */
  @Test
  public void testEntrySet() {
    final HashSet<Triple<Integer, String, Boolean>> entries = new HashSet<>();
    mMap.entrySet().iterator().forEachRemaining(entries::add);
    Assert.assertEquals(2, entries.size());
    Assert.assertTrue(entries.contains(new Triple<>(1, "a", true)));
    Assert.assertTrue(entries.contains(new Triple<>(1, "b", false)));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mMap, mMap);

    NestedMap<Integer, String, Boolean> other = new NestedMap<>();
    other.put(1, "a", true);
    other.put(1, "b", false);
    Assert.assertEquals(mMap, other);

    other = new NestedMap<>();
    other.put(1, "a", true);
    other.put(1, "c", false);
    Assert.assertNotEquals(mMap, other);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#get(java.lang.Object)}.
   */
  @Test
  public void testGetK1() {
    final Map<String, Boolean> values = new HashMap<>();
    values.put("a", true);
    values.put("b", false);

    Assert.assertEquals(values, mMap.get(1));
    Assert.assertNull(mMap.get(2));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#get(java.lang.Object, java.lang.Object)}.
   */
  @Test
  public void testGetK1K2() {
    Assert.assertEquals(true, mMap.get(1, "a"));
    Assert.assertEquals(false, mMap.get(1, "b"));
    Assert.assertNull(mMap.get(1, "c"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mMap.hashCode(), mMap.hashCode());

    NestedMap<Integer, String, Boolean> other = new NestedMap<>();
    other.put(1, "a", true);
    other.put(1, "b", false);
    Assert.assertEquals(mMap.hashCode(), other.hashCode());

    other = new NestedMap<>();
    other.put(1, "a", true);
    other.put(1, "c", false);
    Assert.assertNotEquals(mMap.hashCode(), other.hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#keySet()}.
   */
  @Test
  public void testKeySet() {
    final Set<Integer> keys = mMap.keySet();
    Assert.assertEquals(1, keys.size());
    Assert.assertEquals(1, keys.iterator().next().intValue());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#NestedMap()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNestedMap() {
    try {
      new NestedMap<>();
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#NestedMap(int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNestedMapInt() {
    try {
      new NestedMap<>(10);
      new NestedMap<>(100);
      new NestedMap<>(0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#put(java.lang.Object, java.lang.Object, java.lang.Object)}.
   */
  @Test
  public void testPutK1K2V() {
    Assert.assertNull(mMap.put(2, "a", true));
    Assert.assertEquals(true, mMap.get(2, "a"));
    Assert.assertEquals(true, mMap.put(2, "a", false));
    Assert.assertEquals(false, mMap.get(2, "a"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#put(java.lang.Object, java.util.Map)}.
   */
  @Test
  public void testPutK1MapOfK2V() {
    final Map<String, Boolean> values = new HashMap<>();
    values.put("a", true);
    values.put("b", false);
    Assert.assertNull(mMap.put(2, values));
    Assert.assertEquals(true, mMap.get(2, "a"));
    Assert.assertEquals(values, mMap.put(2, values));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#remove(java.lang.Object)}.
   */
  @Test
  public void testRemoveK1() {
    final Map<String, Boolean> values = new HashMap<>();
    values.put("a", true);
    values.put("b", false);

    mMap.put(2, "a", true);
    Assert.assertEquals(values, mMap.remove(1));
    Assert.assertNull(mMap.get(1, "a"));
    Assert.assertEquals(true, mMap.get(2, "a"));
    Assert.assertNull(mMap.remove(1));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#remove(java.lang.Object, java.lang.Object)}.
   */
  @Test
  public void testRemoveK1K2() {
    Assert.assertEquals(true, mMap.remove(1, "a"));
    Assert.assertNull(mMap.remove(1, "a"));
    mMap.put(1, "a", false);
    Assert.assertEquals(false, mMap.remove(1, "a"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.NestedMap#setNestedInitialCapacity(int)}.
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
