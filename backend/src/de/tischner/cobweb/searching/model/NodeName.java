package de.tischner.cobweb.searching.model;

import de.zabuza.lexisearch.indexing.IKeyProvider;
import de.zabuza.lexisearch.indexing.IKeyRecord;
import de.zabuza.lexisearch.ranking.IRecordScoreProvider;

/**
 * Implementation of an {@link IKeyRecord} which represents an OSM node and its
 * name.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NodeName implements IKeyRecord<String>, IRecordScoreProvider {
  /**
   * The default relevance score to use when no score was given.
   */
  private static final int DEFAULT_RELEVANCE_SCORE = 1;
  /**
   * The id of the node name.
   */
  private final int mId;
  /**
   * The key provider of the node name.
   */
  private final IKeyProvider<String, String> mKeyProvider;
  /**
   * The name of the node.
   */
  private final String mName;
  /**
   * The unique OSM node ID of this node name.
   */
  private final long mNodeId;
  /**
   * The relevance score of this node name. The higher the more relevant.
   */
  private final int mRelevanceScore;

  /**
   * Creates a new node name with the given parameters. The relevance score will
   * be set to a default value.
   *
   * @param id          The id of the node name
   * @param nodeId      the unique OSM node ID of this node name
   * @param name        The name of the node name
   * @param keyProvider The key provider of the node name
   */
  public NodeName(final int id, final long nodeId, final String name, final IKeyProvider<String, String> keyProvider) {
    this(id, nodeId, name, keyProvider, DEFAULT_RELEVANCE_SCORE);
  }

  /**
   * Creates a new node name with the given parameters.
   *
   * @param id             The id of the node name
   * @param nodeId         the unique OSM node ID of this node name
   * @param name           The name of the node name
   * @param keyProvider    The key provider of the node name
   * @param relevanceScore The relevance score of the node name
   */
  public NodeName(final int id, final long nodeId, final String name, final IKeyProvider<String, String> keyProvider,
      final int relevanceScore) {
    mId = id;
    mNodeId = nodeId;
    mName = name;
    mRelevanceScore = relevanceScore;
    mKeyProvider = keyProvider;
  }

  /*
   * (non-Javadoc)
   * @see de.zabuza.lexisearch.indexing.IKeyRecord#getKeys()
   */
  @Override
  public String[] getKeys() {
    return mKeyProvider.getKeys(mName);
  }

  /*
   * (non-Javadoc)
   * @see de.zabuza.lexisearch.indexing.IKeyRecord#getName()
   */
  @Override
  public String getName() {
    return mName;
  }

  /**
   * Gets the unique OSM node ID of this node name.
   *
   * @return The unique OSM node ID
   */
  public long getNodeId() {
    return mNodeId;
  }

  /*
   * (non-Javadoc)
   * @see de.zabuza.lexisearch.indexing.IKeyRecord#getRecordId()
   */
  @Override
  public int getRecordId() {
    return mId;
  }

  /*
   * (non-Javadoc)
   * @see de.zabuza.lexisearch.ranking.IRecordScoreProvider#getScore()
   */
  @Override
  public int getScore() {
    return mRelevanceScore;
  }

  /*
   * (non-Javadoc)
   * @see de.zabuza.lexisearch.indexing.IKeyRecord#getSize()
   */
  @Override
  public int getSize() {
    return mKeyProvider.getSize(mName);
  }

}
