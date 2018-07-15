package de.unifreiburg.informatik.cobweb.db;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link NodeNameData}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NodeNameDataTest {
  /**
   * The node name data used for testing.
   */
  private NodeNameData mNodeNameData;

  /**
   * Setups a node name data instance for testing.
   */
  @Before
  public void setUp() {
    mNodeNameData = new NodeNameData(1L, "Main street 5");
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.db.NodeNameData#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(1L, mNodeNameData.getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.db.NodeNameData#getName()}.
   */
  @Test
  public void testGetName() {
    Assert.assertEquals("Main street 5", mNodeNameData.getName());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.db.NodeNameData#NodeNameData(long, java.lang.String)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNodeNameData() {
    try {
      new NodeNameData(1L, "Main street 5");
      new NodeNameData(-1L, "hello");
      new NodeNameData(0L, "");
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
