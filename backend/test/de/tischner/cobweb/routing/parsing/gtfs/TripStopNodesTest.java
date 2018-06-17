package de.tischner.cobweb.routing.parsing.gtfs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.transit.TransitNode;

/**
 * Test for the class {@link TripStopNodes}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TripStopNodesTest {
  /**
   * The trip stop nodes used for testing.
   */
  private TripStopNodes<TransitNode> mTripStopNodes;

  /**
   * Setups a highway data instance for testing.
   */
  @Before
  public void setUp() {
    mTripStopNodes = new TripStopNodes<>(new TransitNode(1, 1.0F, 1.0F, 1), new TransitNode(2, 2.0F, 2.0F, 2), 1, 2);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.gtfs.TripStopNodes#getArrNode()}.
   */
  @Test
  public void testGetArrNode() {
    Assert.assertEquals(1, mTripStopNodes.getArrNode().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.gtfs.TripStopNodes#getArrTime()}.
   */
  @Test
  public void testGetArrTime() {
    Assert.assertEquals(1, mTripStopNodes.getArrTime());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.gtfs.TripStopNodes#getDepNode()}.
   */
  @Test
  public void testGetDepNode() {
    Assert.assertEquals(2, mTripStopNodes.getDepNode().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.gtfs.TripStopNodes#getDepTime()}.
   */
  @Test
  public void testGetDepTime() {
    Assert.assertEquals(2, mTripStopNodes.getDepTime());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.gtfs.TripStopNodes#TripStopNodes(de.tischner.cobweb.routing.model.graph.INode, de.tischner.cobweb.routing.model.graph.INode, int, int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTripStopNodes() {
    try {
      new TripStopNodes<>(new TransitNode(1, 1.0F, 1.0F, 1), new TransitNode(2, 2.0F, 2.0F, 2), 1, 2);
      new TripStopNodes<>(new TransitNode(1, 0.0F, 0.0F, 0), new TransitNode(2, 2.0F, 2.0F, 2), 2, 1);
      new TripStopNodes<>(new TransitNode(1, 0.0F, 0.0F, 0), new TransitNode(2, 2.0F, 2.0F, 2), -2, -1);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
