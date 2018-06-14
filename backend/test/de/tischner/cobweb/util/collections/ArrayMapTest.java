package de.tischner.cobweb.util.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.BasicNode;

/**
 * Test for the class {@link ArrayMap}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public class ArrayMapTest {
  /**
   * The array map used for testing.
   */
  private ArrayMap<BasicNode, Integer> mMap;

  /**
   * Setups an array map instance for testing.
   */
  @Before
  public void setUp() {
    mMap = new ArrayMap<>();
    mMap.put(new BasicNode(0), 1);
    mMap.put(new BasicNode(9), 10);
    mMap.put(new BasicNode(5), 6);

    mMap.put(new BasicNode(1), 2);
    mMap.put(new BasicNode(2), 3);

    mMap.put(new BasicNode(6), 7);
    mMap.put(new BasicNode(7), 8);
    mMap.put(new BasicNode(8), 9);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#ArrayMap()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testArrayMap() {
    try {
      new ArrayMap<>();
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#ArrayMap(int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testArrayMapInt() {
    try {
      new ArrayMap<>(10);
      new ArrayMap<>(0);
      new ArrayMap<>(100);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#clear()}.
   */
  @Test
  public void testClear() {
    Assert.assertFalse(mMap.isEmpty());
    mMap.clear();
    Assert.assertTrue(mMap.isEmpty());

    final ArrayMap<BasicNode, Integer> other = new ArrayMap<>();
    other.clear();
    Assert.assertTrue(other.isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#containsKey(java.lang.Object)}.
   */
  @Test
  public void testContainsKey() {
    Assert.assertTrue(mMap.containsKey(new BasicNode(0)));
    Assert.assertTrue(mMap.containsKey(new BasicNode(1)));
    Assert.assertTrue(mMap.containsKey(new BasicNode(2)));
    Assert.assertTrue(mMap.containsKey(new BasicNode(5)));
    Assert.assertTrue(mMap.containsKey(new BasicNode(6)));
    Assert.assertTrue(mMap.containsKey(new BasicNode(7)));
    Assert.assertTrue(mMap.containsKey(new BasicNode(8)));
    Assert.assertTrue(mMap.containsKey(new BasicNode(9)));

    Assert.assertFalse(mMap.containsKey(new BasicNode(3)));
    Assert.assertFalse(mMap.containsKey(new BasicNode(4)));

    Assert.assertFalse(mMap.containsKey(new BasicNode(10_000)));

    mMap.remove(new BasicNode(1));
    Assert.assertFalse(mMap.containsKey(new BasicNode(1)));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#containsValue(java.lang.Object)}.
   */
  @Test
  public void testContainsValue() {
    Assert.assertTrue(mMap.containsValue(1));
    Assert.assertTrue(mMap.containsValue(2));
    Assert.assertTrue(mMap.containsValue(3));
    Assert.assertTrue(mMap.containsValue(6));
    Assert.assertTrue(mMap.containsValue(7));
    Assert.assertTrue(mMap.containsValue(8));
    Assert.assertTrue(mMap.containsValue(9));
    Assert.assertTrue(mMap.containsValue(10));

    Assert.assertFalse(mMap.containsValue(4));
    Assert.assertFalse(mMap.containsValue(5));

    Assert.assertFalse(mMap.containsValue(10_000));
    Assert.assertFalse(mMap.containsValue(0));
    Assert.assertFalse(mMap.containsValue(-10_000));

    mMap.remove(new BasicNode(1));
    Assert.assertFalse(mMap.containsValue(2));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#entrySet()}.
   */
  @Test
  public void testEntrySet() {
    boolean wasExceptionThrown = false;
    try {
      mMap.entrySet();
    } catch (final UnsupportedOperationException e) {
      wasExceptionThrown = true;
    }
    Assert.assertTrue(wasExceptionThrown);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#forEach(java.util.function.BiConsumer)}.
   */
  @Test
  public void testForEach() {
    boolean wasExceptionThrown = false;
    try {
      mMap.forEach((key, value) -> {
        // Do nothing, just trigger the exception
      });
    } catch (final UnsupportedOperationException e) {
      wasExceptionThrown = true;
    }
    Assert.assertTrue(wasExceptionThrown);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#forEachIdValue(org.eclipse.collections.api.block.procedure.primitive.IntObjectProcedure)}.
   */
  @Test
  public void testForEachIdValue() {
    final HashMap<Integer, Integer> entries = new HashMap<>();
    mMap.forEachIdValue(entries::put);
    Assert.assertEquals(8, entries.size());
    Assert.assertEquals(1, entries.get(0).intValue());
    Assert.assertEquals(2, entries.get(1).intValue());
    Assert.assertFalse(entries.containsKey(3));
    Assert.assertFalse(entries.containsKey(4));
    Assert.assertEquals(6, entries.get(5).intValue());
    Assert.assertEquals(7, entries.get(6).intValue());

    final ArrayMap<BasicNode, Integer> other = new ArrayMap<>();
    entries.clear();
    other.forEachIdValue(entries::put);
    Assert.assertTrue(entries.isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#get(java.lang.Object)}.
   */
  @Test
  public void testGet() {
    Assert.assertEquals(1, mMap.get(new BasicNode(0)).intValue());
    Assert.assertEquals(10, mMap.get(new BasicNode(9)).intValue());

    Assert.assertNull(mMap.get(new BasicNode(15)));

    mMap.remove(new BasicNode(0));
    Assert.assertNull(mMap.get(new BasicNode(0)));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#isEmpty()}.
   */
  @Test
  public void testIsEmpty() {
    Assert.assertFalse(mMap.isEmpty());
    Assert.assertTrue(new ArrayMap<>().isEmpty());

    final ArrayMap<BasicNode, Integer> other = new ArrayMap<>();
    other.put(new BasicNode(10_000), 1);
    Assert.assertFalse(other.isEmpty());
    other.remove(new BasicNode(10_000));
    Assert.assertTrue(other.isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#keySet()}.
   */
  @Test
  public void testKeySet() {
    boolean wasExceptionThrown = false;
    try {
      mMap.keySet();
    } catch (final UnsupportedOperationException e) {
      wasExceptionThrown = true;
    }
    Assert.assertTrue(wasExceptionThrown);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#put(de.tischner.cobweb.routing.model.graph.IHasId, Object)}.
   */
  @Test
  public void testPut() {
    Assert.assertNull(mMap.put(new BasicNode(15), 16));
    Assert.assertEquals(16, mMap.put(new BasicNode(15), 100).intValue());

    mMap.remove(new BasicNode(15));
    Assert.assertNull(mMap.put(new BasicNode(15), 16));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#putAll(java.util.Map)}.
   */
  @Test
  public void testPutAll() {
    final HashMap<BasicNode, Integer> entries = new HashMap<>();
    entries.put(new BasicNode(0), -1);
    entries.put(new BasicNode(9), -10);
    entries.put(new BasicNode(4), 5);
    entries.put(new BasicNode(100), 100);

    Assert.assertEquals(8, mMap.size());
    mMap.putAll(Collections.emptyMap());
    Assert.assertEquals(8, mMap.size());

    mMap.putAll(entries);
    Assert.assertEquals(10, mMap.size());
    Assert.assertEquals(-1, mMap.get(new BasicNode(0)).intValue());
    Assert.assertEquals(-10, mMap.get(new BasicNode(9)).intValue());
    Assert.assertEquals(5, mMap.get(new BasicNode(4)).intValue());
    Assert.assertEquals(100, mMap.get(new BasicNode(100)).intValue());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#remove(java.lang.Object)}.
   */
  @Test
  public void testRemove() {
    Assert.assertEquals(8, mMap.size());
    Assert.assertEquals(1, mMap.remove(new BasicNode(0)).intValue());
    Assert.assertEquals(7, mMap.size());

    Assert.assertNull(mMap.remove(new BasicNode(0)));
    Assert.assertEquals(7, mMap.size());

    Assert.assertNull(mMap.remove(new BasicNode(-100)));

    Assert.assertEquals(2, mMap.remove(new BasicNode(1)).intValue());
    Assert.assertEquals(3, mMap.remove(new BasicNode(2)).intValue());
    Assert.assertEquals(6, mMap.remove(new BasicNode(5)).intValue());
    Assert.assertEquals(7, mMap.remove(new BasicNode(6)).intValue());
    Assert.assertEquals(8, mMap.remove(new BasicNode(7)).intValue());
    Assert.assertEquals(9, mMap.remove(new BasicNode(8)).intValue());
    Assert.assertEquals(10, mMap.remove(new BasicNode(9)).intValue());

    Assert.assertTrue(mMap.isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#size()}.
   */
  @Test
  public void testSize() {
    Assert.assertEquals(8, mMap.size());
    Assert.assertEquals(0, new ArrayMap<>().size());

    final ArrayMap<BasicNode, Integer> other = new ArrayMap<>();
    other.put(new BasicNode(10_000), 1);
    Assert.assertEquals(1, other.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#streamValues()}.
   */
  @Test
  public void testStreamValues() {
    Set<Integer> values = mMap.streamValues().collect(Collectors.toSet());
    Assert.assertEquals(8, values.size());
    Assert.assertTrue(values.contains(1));
    Assert.assertTrue(values.contains(2));
    Assert.assertFalse(values.contains(4));
    Assert.assertFalse(values.contains(5));
    Assert.assertTrue(values.contains(6));
    Assert.assertTrue(values.contains(7));

    final ArrayMap<BasicNode, Integer> other = new ArrayMap<>();
    values = other.streamValues().collect(Collectors.toSet());
    Assert.assertTrue(values.isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.ArrayMap#values()}.
   */
  @Test
  public void testValues() {
    boolean wasExceptionThrown = false;
    try {
      mMap.values();
    } catch (final UnsupportedOperationException e) {
      wasExceptionThrown = true;
    }
    Assert.assertTrue(wasExceptionThrown);
  }

}
