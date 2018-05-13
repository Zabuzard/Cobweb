package de.tischner.cobweb.searching.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.db.NodeNameData;
import de.zabuza.lexisearch.indexing.IInvertedIndex;
import de.zabuza.lexisearch.indexing.IInvertedList;
import de.zabuza.lexisearch.indexing.IKeyProvider;
import de.zabuza.lexisearch.indexing.IKeyRecord;
import de.zabuza.lexisearch.indexing.Posting;
import de.zabuza.lexisearch.indexing.qgram.QGramProvider;

/**
 * Test for the class {@link NodeNameSet}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NodeNameSetTest {
  /**
   * The key provider used for testing.
   */
  private IKeyProvider<String, String> mKeyProvider;
  /**
   * The node name set used for testing.
   */
  private NodeNameSet mSet;

  /**
   * Setups a node name set instance for testing.
   */
  @Before
  public void setUp() {
    mKeyProvider = new QGramProvider(3);
    mSet = new NodeNameSet();
    mSet.add(new NodeName(0, 1L, "a", mKeyProvider));
    mSet.add(new NodeName(1, 2L, "b", mKeyProvider));
    mSet.add(new NodeName(2, 3L, "c", mKeyProvider));
    mSet.add(new NodeName(3, 4L, "d", mKeyProvider));
    mSet.add(new NodeName(4, 5L, "e", mKeyProvider));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#add(de.zabuza.lexisearch.indexing.IKeyRecord)}.
   */
  @Test
  public void testAdd() {
    Assert.assertEquals(5, mSet.size());
    final NodeName nodeName = new NodeName(10, 10L, "z", mKeyProvider);
    Assert.assertTrue(mSet.add(nodeName));
    Assert.assertEquals(6, mSet.size());
    Assert.assertFalse(mSet.add(nodeName));
    Assert.assertEquals(6, mSet.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#addAll(java.util.Collection)}.
   */
  @Test
  public void testAddAll() {
    Assert.assertEquals(5, mSet.size());
    final NodeName first = new NodeName(10, 10L, "x", mKeyProvider);
    final NodeName second = new NodeName(11, 11L, "y", mKeyProvider);

    Assert.assertTrue(mSet.addAll(Collections.singletonList(first)));
    Assert.assertEquals(6, mSet.size());

    Assert.assertFalse(mSet.addAll(Collections.singletonList(first)));
    Assert.assertEquals(6, mSet.size());

    Assert.assertTrue(mSet.addAll(Arrays.asList(first, second)));
    Assert.assertEquals(7, mSet.size());

    Assert.assertFalse(mSet.addAll(Arrays.asList(first, second)));
    Assert.assertEquals(7, mSet.size());

    Assert.assertFalse(mSet.addAll(Arrays.asList(first, first)));
    Assert.assertEquals(7, mSet.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#buildFromNodeNameData(java.lang.Iterable, de.zabuza.lexisearch.indexing.IKeyProvider)}.
   */
  @Test
  public void testBuildFromNodeNameData() {
    final List<NodeNameData> data = new ArrayList<>();
    data.add(new NodeNameData(1L, "a"));
    data.add(new NodeNameData(2L, "b"));
    data.add(new NodeNameData(3L, "c"));
    data.add(new NodeNameData(4L, "d"));
    data.add(new NodeNameData(5L, "e"));

    final NodeNameSet set = NodeNameSet.buildFromNodeNameData(data, mKeyProvider);
    Assert.assertEquals(5, set.size());

    Assert.assertEquals(5, set.stream().map(IKeyRecord::getRecordId).collect(Collectors.toSet()).size());

    final Set<String> names = set.stream().map(IKeyRecord::getName).collect(Collectors.toSet());
    Assert.assertTrue(names.contains("a"));
    Assert.assertTrue(names.contains("b"));
    Assert.assertTrue(names.contains("c"));
    Assert.assertTrue(names.contains("d"));
    Assert.assertTrue(names.contains("e"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#clear()}.
   */
  @Test
  public void testClear() {
    Assert.assertEquals(5, mSet.size());
    mSet.clear();
    Assert.assertTrue(mSet.isEmpty());
    Assert.assertFalse(mSet.iterator().hasNext());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#contains(java.lang.Object)}.
   */
  @Test
  public void testContains() {
    final NodeName nodeName = new NodeName(10, 10L, "z", mKeyProvider);

    Assert.assertFalse(mSet.contains(nodeName));
    mSet.add(nodeName);
    Assert.assertTrue(mSet.contains(nodeName));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#containsAll(java.util.Collection)}.
   */
  @Test
  public void testContainsAll() {
    final NodeName first = new NodeName(10, 10L, "x", mKeyProvider);
    final NodeName second = new NodeName(11, 11L, "y", mKeyProvider);

    Assert.assertFalse(mSet.containsAll(Collections.singletonList(first)));
    Assert.assertFalse(mSet.containsAll(Arrays.asList(first, second)));

    mSet.add(first);
    Assert.assertTrue(mSet.containsAll(Collections.singletonList(first)));
    Assert.assertFalse(mSet.containsAll(Arrays.asList(first, second)));

    mSet.add(second);
    Assert.assertTrue(mSet.containsAll(Arrays.asList(first, second)));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#createdInvertedIndex()}.
   */
  @Test
  public void testCreatedInvertedIndex() {
    final IInvertedIndex<String> invertedIndex = mSet.createdInvertedIndex();
    final Set<Integer> recordIds =
        StreamSupport.stream(invertedIndex.getKeys().spliterator(), false).map(invertedIndex::getRecords)
            .map(IInvertedList::getPostings).flatMap(postings -> StreamSupport.stream(postings.spliterator(), false))
            .map(Posting::getId).collect(Collectors.toSet());
    Assert.assertEquals(5, recordIds.size());
    Assert.assertTrue(recordIds.contains(0));
    Assert.assertTrue(recordIds.contains(1));
    Assert.assertTrue(recordIds.contains(2));
    Assert.assertTrue(recordIds.contains(3));
    Assert.assertTrue(recordIds.contains(4));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#getKeyRecordById(int)}.
   */
  @Test
  public void testGetKeyRecordById() {
    final IKeyRecord<String> record = mSet.getKeyRecordById(3);
    Assert.assertEquals("d", record.getName());

    Assert.assertNull(mSet.getKeyRecordById(10));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#isEmpty()}.
   */
  @Test
  public void testIsEmpty() {
    Assert.assertFalse(mSet.isEmpty());
    Assert.assertTrue(new NodeNameSet().isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#iterator()}.
   */
  @Test
  public void testIterator() {
    final Iterator<IKeyRecord<String>> recordIter = mSet.iterator();
    Assert.assertTrue(recordIter.hasNext());
    Assert.assertEquals("a", recordIter.next().getName());
    Assert.assertTrue(recordIter.hasNext());
    Assert.assertEquals("b", recordIter.next().getName());
    Assert.assertTrue(recordIter.hasNext());
    Assert.assertEquals("c", recordIter.next().getName());
    Assert.assertTrue(recordIter.hasNext());
    Assert.assertEquals("d", recordIter.next().getName());
    Assert.assertTrue(recordIter.hasNext());
    Assert.assertEquals("e", recordIter.next().getName());
    Assert.assertFalse(recordIter.hasNext());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#NodeNameSet()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNodeNameSet() {
    try {
      new NodeNameSet();
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#remove(java.lang.Object)}.
   */
  @Test
  public void testRemove() {
    Assert.assertEquals(5, mSet.size());
    final NodeName nodeName = new NodeName(10, 10L, "z", mKeyProvider);
    Assert.assertFalse(mSet.remove(nodeName));
    Assert.assertEquals(5, mSet.size());

    mSet.add(nodeName);
    Assert.assertEquals(6, mSet.size());
    Assert.assertTrue(mSet.remove(nodeName));
    Assert.assertEquals(5, mSet.size());
    Assert.assertFalse(mSet.contains(nodeName));

    Assert.assertFalse(mSet.remove(nodeName));
    Assert.assertEquals(5, mSet.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#removeAll(java.util.Collection)}.
   */
  @Test
  public void testRemoveAll() {
    Assert.assertEquals(5, mSet.size());
    final NodeName first = new NodeName(10, 10L, "x", mKeyProvider);
    final NodeName second = new NodeName(11, 11L, "y", mKeyProvider);
    Assert.assertFalse(mSet.removeAll(Arrays.asList(first, second)));
    Assert.assertEquals(5, mSet.size());

    mSet.add(first);
    Assert.assertEquals(6, mSet.size());
    Assert.assertTrue(mSet.removeAll(Arrays.asList(first, second)));
    Assert.assertEquals(5, mSet.size());
    Assert.assertFalse(mSet.contains(first));

    Assert.assertFalse(mSet.removeAll(Arrays.asList(first, second)));
    Assert.assertEquals(5, mSet.size());

    mSet.add(first);
    mSet.add(second);
    Assert.assertEquals(7, mSet.size());
    Assert.assertTrue(mSet.removeAll(Arrays.asList(first, second)));
    Assert.assertEquals(5, mSet.size());
    Assert.assertFalse(mSet.contains(first));
    Assert.assertFalse(mSet.contains(second));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#retainAll(java.util.Collection)}.
   */
  @Test
  public void testRetainAll() {
    final NodeName first = new NodeName(10, 10L, "x", mKeyProvider);
    final NodeName second = new NodeName(11, 11L, "y", mKeyProvider);

    Assert.assertFalse(mSet.retainAll(mSet));
    Assert.assertEquals(5, mSet.size());

    Assert.assertTrue(mSet.retainAll(Arrays.asList(first, second)));
    Assert.assertTrue(mSet.isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#size()}.
   */
  @Test
  public void testSize() {
    Assert.assertEquals(5, mSet.size());
    Assert.assertEquals(0, new NodeNameSet().size());

    final NodeName nodeName = new NodeName(10, 10L, "z", mKeyProvider);
    mSet.add(nodeName);
    Assert.assertEquals(6, mSet.size());
    mSet.remove(nodeName);
    Assert.assertEquals(5, mSet.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#toArray()}.
   */
  @Test
  public void testToArray() {
    final Object[] elements = mSet.toArray();
    Assert.assertEquals(5, elements.length);
    final Set<String> names = Arrays.stream(elements).filter(e -> e instanceof NodeName)
        .map(e -> ((NodeName) e).getName()).collect(Collectors.toSet());
    Assert.assertTrue(names.contains("a"));
    Assert.assertTrue(names.contains("b"));
    Assert.assertTrue(names.contains("c"));
    Assert.assertTrue(names.contains("d"));
    Assert.assertTrue(names.contains("e"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.model.NodeNameSet#toArray(Object[])}.
   */
  @Test
  public void testToArrayTArray() {
    final NodeName[] elements = mSet.toArray(new NodeName[0]);
    Assert.assertEquals(5, elements.length);
    final Set<String> names = Arrays.stream(elements).map(NodeName::getName).collect(Collectors.toSet());
    Assert.assertTrue(names.contains("a"));
    Assert.assertTrue(names.contains("b"));
    Assert.assertTrue(names.contains("c"));
    Assert.assertTrue(names.contains("d"));
    Assert.assertTrue(names.contains("e"));
  }

}
