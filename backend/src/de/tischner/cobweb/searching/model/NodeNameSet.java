package de.tischner.cobweb.searching.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.tischner.cobweb.db.NodeNameData;
import de.zabuza.lexisearch.indexing.IInvertedIndex;
import de.zabuza.lexisearch.indexing.IKeyProvider;
import de.zabuza.lexisearch.indexing.IKeyRecord;
import de.zabuza.lexisearch.indexing.IKeyRecordSet;
import de.zabuza.lexisearch.indexing.InvertedIndexUtil;

/**
 * Implementation of a {@link IKeyRecordSet} which holds {@link NodeName}
 * objects.<br>
 * <br>
 * It provides fast access to its elements by their record ID. The method
 * {@link #buildFromNodeNameData(Iterable, IKeyProvider)} can be used to
 * construct it from given data.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NodeNameSet implements IKeyRecordSet<IKeyRecord<String>, String> {
  /**
   * Builds a node name set from the given node name data.
   *
   * @param nodeNameData The data to build the set from
   * @param keyProvider  The key provider to use
   * @return The set of node names build from the given data
   */
  public static NodeNameSet buildFromNodeNameData(final Iterable<NodeNameData> nodeNameData,
      final IKeyProvider<String, String> keyProvider) {
    final NodeNameSet nodeNames = new NodeNameSet();

    int nextRecordId = 0;
    for (final NodeNameData data : nodeNameData) {
      nodeNames.add(new NodeName(nextRecordId, data.getId(), data.getName(), keyProvider));
      nextRecordId++;
    }
    return nodeNames;
  }

  /**
   * Map which connects record IDs to their records.
   */
  private final Map<Integer, IKeyRecord<String>> mIdToRecord;

  /**
   * Creates a new empty node name set.
   */
  public NodeNameSet() {
    mIdToRecord = new HashMap<>();
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#add(java.lang.Object)
   */
  @Override
  public boolean add(final IKeyRecord<String> e) {
    final int id = e.getRecordId();
    final IKeyRecord<String> previous = mIdToRecord.put(id, e);
    return previous == null || !previous.equals(e);
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#addAll(java.util.Collection)
   */
  @Override
  public boolean addAll(final Collection<? extends IKeyRecord<String>> c) {
    boolean hasChanged = false;
    for (final IKeyRecord<String> record : c) {
      if (add(record)) {
        hasChanged = true;
      }
    }
    return hasChanged;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#clear()
   */
  @Override
  public void clear() {
    mIdToRecord.clear();
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#contains(java.lang.Object)
   */
  @Override
  public boolean contains(final Object o) {
    if (!(o instanceof IKeyRecord)) {
      return false;
    }
    final int id = ((IKeyRecord<?>) o).getRecordId();
    final IKeyRecord<String> current = mIdToRecord.get(id);
    return current != null && current.equals(o);
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#containsAll(java.util.Collection)
   */
  @Override
  public boolean containsAll(final Collection<?> c) {
    return c.stream().allMatch(this::contains);
  }

  /**
   * Creates an inverted index from this set of node names.
   *
   * @return The inverted index which works on this set of node names
   */
  public IInvertedIndex<String> createdInvertedIndex() {
    return InvertedIndexUtil.createFromWords(mIdToRecord.values());
  }

  /*
   * (non-Javadoc)
   * @see de.zabuza.lexisearch.indexing.IKeyRecordSet#getKeyRecordById(int)
   */
  @Override
  public IKeyRecord<String> getKeyRecordById(final int recordId) {
    return mIdToRecord.get(recordId);
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#isEmpty()
   */
  @Override
  public boolean isEmpty() {
    return mIdToRecord.isEmpty();
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#iterator()
   */
  @Override
  public Iterator<IKeyRecord<String>> iterator() {
    return mIdToRecord.values().iterator();
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#remove(java.lang.Object)
   */
  @Override
  public boolean remove(final Object o) {
    if (!contains(o) || !(o instanceof IKeyRecord)) {
      return false;
    }
    final int id = ((IKeyRecord<?>) o).getRecordId();
    mIdToRecord.remove(id);
    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#removeAll(java.util.Collection)
   */
  @Override
  public boolean removeAll(final Collection<?> c) {
    boolean hasChanged = false;
    for (final Object record : c) {
      if (remove(record)) {
        hasChanged = true;
      }
    }
    return hasChanged;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#retainAll(java.util.Collection)
   */
  @Override
  public boolean retainAll(final Collection<?> c) {
    return mIdToRecord.values().retainAll(c);
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#size()
   */
  @Override
  public int size() {
    return mIdToRecord.size();
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#toArray()
   */
  @Override
  public Object[] toArray() {
    return mIdToRecord.values().toArray();
  }

  /*
   * (non-Javadoc)
   * @see java.util.Set#toArray(java.lang.Object[])
   */
  @Override
  public <T> T[] toArray(final T[] a) {
    return mIdToRecord.values().toArray(a);
  }
}
