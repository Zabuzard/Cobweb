package de.unifreiburg.informatik.cobweb.routing.parsing.osm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link IdMapping}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public class IdMappingTest {
  /**
   * The mapping used for testing.
   */
  private IdMapping mMapping;

  /**
   * Setups a mapping instance for testing.
   */
  @Before
  public void setUp() {
    mMapping = new IdMapping(5L, 4, true);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.parsing.osm.IdMapping#getInternalId()}.
   */
  @Test
  public void testGetInternalId() {
    Assert.assertEquals(4, mMapping.getInternalId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.parsing.osm.IdMapping#getOsmId()}.
   */
  @Test
  public void testGetOsmId() {
    Assert.assertEquals(5L, mMapping.getOsmId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.parsing.osm.IdMapping#IdMapping(long, int, boolean)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testIdMapping() {
    try {
      new IdMapping(5L, 4, true);
      new IdMapping(5L, 4, false);
      new IdMapping(0L, 0, true);
      new IdMapping(-10L, -10, false);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.parsing.osm.IdMapping#isNode()}.
   */
  @Test
  public void testIsNode() {
    Assert.assertTrue(mMapping.isNode());
  }

}
