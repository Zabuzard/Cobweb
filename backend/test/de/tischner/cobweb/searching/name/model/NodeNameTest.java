package de.tischner.cobweb.searching.name.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.zabuza.lexisearch.indexing.IKeyProvider;
import de.zabuza.lexisearch.indexing.qgram.QGramProvider;

/**
 * Test for the class {@link NodeName}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NodeNameTest {
  /**
   * The key provider used for testing.
   */
  private IKeyProvider<String, String> mKeyProvider;
  /**
   * The node name used for testing.
   */
  private NodeName mNodeName;

  /**
   * Setups a node name instance for testing.
   */
  @Before
  public void setUp() {
    mKeyProvider = new QGramProvider(3);
    mNodeName = new NodeName(2, 1L, "Wall street 5", mKeyProvider, 5);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.model.NodeName#getKeys()}.
   */
  @Test
  public void testGetKeys() {
    Assert.assertArrayEquals(mKeyProvider.getKeys(mNodeName.getName()), mNodeName.getKeys());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.model.NodeName#getName()}.
   */
  @Test
  public void testGetName() {
    Assert.assertEquals("Wall street 5", mNodeName.getName());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.model.NodeName#getNodeId()}.
   */
  @Test
  public void testGetNodeId() {
    Assert.assertEquals(1L, mNodeName.getNodeId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.model.NodeName#getRecordId()}.
   */
  @Test
  public void testGetRecordId() {
    Assert.assertEquals(2, mNodeName.getRecordId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.model.NodeName#getScore()}.
   */
  @Test
  public void testGetScore() {
    Assert.assertEquals(5, mNodeName.getScore());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.model.NodeName#getSize()}.
   */
  @Test
  public void testGetSize() {
    Assert.assertEquals(mKeyProvider.getSize(mNodeName.getName()), mNodeName.getSize());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.model.NodeName#NodeName(int, long, java.lang.String, de.zabuza.lexisearch.indexing.IKeyProvider)}.
   */
  @SuppressWarnings("unused")
  @Test
  public void testNodeNameIntLongStringIKeyProviderOfStringString() {
    try {
      new NodeName(2, 1L, "Wall street 5", mKeyProvider);
      new NodeName(-2, -1L, "hello", mKeyProvider);
      new NodeName(0, 0L, "", mKeyProvider);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.model.NodeName#NodeName(int, long, java.lang.String, de.zabuza.lexisearch.indexing.IKeyProvider, int)}.
   */
  @SuppressWarnings("unused")
  @Test
  public void testNodeNameIntLongStringIKeyProviderOfStringStringInt() {
    try {
      new NodeName(2, 1L, "Wall street 5", mKeyProvider, 5);
      new NodeName(-2, -1L, "hello", mKeyProvider, -5);
      new NodeName(0, 0L, "", mKeyProvider, 0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
