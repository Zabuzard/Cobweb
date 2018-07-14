package de.tischner.cobweb.util.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class CollectionUtilTest {
  /**
   * Test method for
   * {@link de.tischner.cobweb.util.collections.CollectionUtil#increaseCapacity(java.util.Collection, int)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testIncreaseCapacity() {
    final List<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3));
    final List<Integer> elements = new ArrayList<>(Arrays.asList(1, 2, 3));
    CollectionUtil.increaseCapacity(elements, 2);
    Assert.assertEquals(expected, elements);

    CollectionUtil.increaseCapacity(elements, 5);
    expected.add(null);
    expected.add(null);
    Assert.assertEquals(expected, elements);

    CollectionUtil.increaseCapacity(elements, 5);
    Assert.assertEquals(expected, elements);

    CollectionUtil.increaseCapacity(elements, 10);
    expected.add(null);
    expected.add(null);
    expected.add(null);
    expected.add(null);
    expected.add(null);
    Assert.assertEquals(expected, elements);
  }

}
