package de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.transit.TransitNode;

/**
 * Test for the class {@link FootpathTransitEdge}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class FootpathTransitEdgeTest {
  /**
   * The edge used for testing.
   */
  private FootpathTransitEdge<TransitNode> mEdge;

  /**
   * Setups an edge instance for testing.
   */
  @Before
  public void setUp() {
    final TransitNode first = new TransitNode(1, 1.0F, 1.0F, 1);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 2);
    mEdge = new FootpathTransitEdge<>(1, first, second, 1);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan.FootpathTransitEdge#FootpathTransitEdge(int, de.tischner.cobweb.routing.model.graph.ICoreNode, de.tischner.cobweb.routing.model.graph.ICoreNode, double)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testFootpathTransitEdge() {
    final TransitNode first = new TransitNode(1, 1.0F, 1.0F, 1);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 1);
    try {
      new FootpathTransitEdge<>(1, first, second, 1);
      new FootpathTransitEdge<>(-1, first, second, -1);
      new FootpathTransitEdge<>(0, first, second, 0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan.FootpathTransitEdge#getTransportationModes()}.
   */
  @Test
  public void testGetTransportationModes() {
    Assert.assertEquals(3, mEdge.getTransportationModes().size());
    Assert.assertTrue(mEdge.getTransportationModes().contains(ETransportationMode.CAR));
    Assert.assertTrue(mEdge.getTransportationModes().contains(ETransportationMode.BIKE));
    Assert.assertTrue(mEdge.getTransportationModes().contains(ETransportationMode.FOOT));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.connectionscan.FootpathTransitEdge#hasTransportationMode(de.tischner.cobweb.routing.model.graph.ETransportationMode)}.
   */
  @Test
  public void testHasTransportationMode() {
    Assert.assertTrue(mEdge.hasTransportationMode(ETransportationMode.CAR));
    Assert.assertTrue(mEdge.hasTransportationMode(ETransportationMode.BIKE));
    Assert.assertTrue(mEdge.hasTransportationMode(ETransportationMode.FOOT));

    Assert.assertFalse(mEdge.hasTransportationMode(ETransportationMode.TRAM));
  }

}
