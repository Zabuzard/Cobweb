package de.unifreiburg.informatik.cobweb.routing.model.graph;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link UniqueIdGenerator}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class UniqueIdGeneratorTest {

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.UniqueIdGenerator#generateUniqueId()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGenerateUniqueId() {
    final UniqueIdGenerator generator = new UniqueIdGenerator();
    Assert.assertEquals(0, generator.generateUniqueId());
    Assert.assertEquals(1, generator.generateUniqueId());
    Assert.assertEquals(2, generator.generateUniqueId());
    // Testing the whole range takes too long (1-2 seconds)
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.UniqueIdGenerator#UniqueIdGenerator()}.
   */
  @SuppressWarnings({ "static-method", "unused" })
  @Test
  public void testUniqueIdGenerator() {
    try {
      new UniqueIdGenerator();
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
