package de.tischner.cobweb.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link NestedMap}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
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
   * {@link de.tischner.cobweb.util.NestedMap#addAll(de.tischner.cobweb.util.NestedMap)}.
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
   * Test method for {@link de.tischner.cobweb.util.NestedMap#clear()}.
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
   * Test method for {@link de.tischner.cobweb.util.NestedMap#entrySet()}.
   */
  @Test
  public void testEntrySet() {
    final Iterator<Triple<Integer, String, Boolean>> entryIter = mMap.entrySet().iterator();
    Assert.assertTrue(entryIter.hasNext());
    Assert.assertEquals(new Triple<>(1, "a", true), entryIter.next());
    Assert.assertTrue(entryIter.hasNext());
    Assert.assertEquals(new Triple<>(1, "b", false), entryIter.next());
    Assert.assertFalse(entryIter.hasNext());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.NestedMap#equals(java.lang.Object)}.
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
   * {@link de.tischner.cobweb.util.NestedMap#get(java.lang.Object)}.
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
   * {@link de.tischner.cobweb.util.NestedMap#get(java.lang.Object, java.lang.Object)}.
   */
  @Test
  public void testGetK1K2() {
    Assert.assertEquals(true, mMap.get(1, "a"));
    Assert.assertEquals(false, mMap.get(1, "b"));
    Assert.assertNull(mMap.get(1, "c"));
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.NestedMap#hashCode()}.
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
   * Test method for {@link de.tischner.cobweb.util.NestedMap#keySet()}.
   */
  @Test
  public void testKeySet() {
    final Set<Integer> keys = mMap.keySet();
    Assert.assertEquals(1, keys.size());
    Assert.assertEquals(1, keys.iterator().next().intValue());
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.NestedMap#NestedMap()}.
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
   * {@link de.tischner.cobweb.util.NestedMap#put(java.lang.Object, java.lang.Object, java.lang.Object)}.
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
   * {@link de.tischner.cobweb.util.NestedMap#put(java.lang.Object, java.util.Map)}.
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
   * {@link de.tischner.cobweb.util.NestedMap#remove(java.lang.Object)}.
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
   * {@link de.tischner.cobweb.util.NestedMap#remove(java.lang.Object, java.lang.Object)}.
   */
  @Test
  public void testRemoveK1K2() {
    Assert.assertEquals(true, mMap.remove(1, "a"));
    Assert.assertNull(mMap.remove(1, "a"));
    mMap.put(1, "a", false);
    Assert.assertEquals(false, mMap.remove(1, "a"));
  }

}
