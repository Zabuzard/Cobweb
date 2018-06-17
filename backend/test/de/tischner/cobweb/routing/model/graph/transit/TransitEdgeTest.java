package de.tischner.cobweb.routing.model.graph.transit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.ETransportationMode;

/**
 * Test for the class {@link TransitEdge}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TransitEdgeTest {

  /**
   * The edge used for testing.
   */
  private TransitEdge<TransitNode> mEdge;

  /**
   * Setups an edge instance for testing.
   */
  @Before
  public void setUp() {
    final TransitNode first = new TransitNode(1, 1.0F, 1.0F, 1);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 2);
    mEdge = new TransitEdge<>(1, first, second, 1);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitEdge#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    final TransitNode first = new TransitNode(1, 1.0F, 1.0F, 1);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 2);
    TransitEdge<TransitNode> other = new TransitEdge<>(1, first, second, 1);
    Assert.assertEquals(mEdge, other);

    other = new TransitEdge<>(1, second, first, 1);
    Assert.assertEquals(mEdge, other);

    other = new TransitEdge<>(1, first, second, 2);
    Assert.assertEquals(mEdge, other);

    other = new TransitEdge<>(2, first, second, 1);
    Assert.assertNotEquals(mEdge, other);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitEdge#getCost()}.
   */
  @Test
  public void testGetCost() {
    Assert.assertEquals(1.0, mEdge.getCost(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitEdge#getDestination()}.
   */
  @Test
  public void testGetDestination() {
    Assert.assertEquals(2, mEdge.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitEdge#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(1, mEdge.getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitEdge#getSource()}.
   */
  @Test
  public void testGetSource() {
    Assert.assertEquals(1, mEdge.getSource().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitEdge#getTransportationModes()}.
   */
  @Test
  public void testGetTransportationModes() {
    Assert.assertEquals(1, mEdge.getTransportationModes().size());
    Assert.assertTrue(mEdge.getTransportationModes().contains(ETransportationMode.TRAM));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitEdge#hashCode()}.
   */
  @Test
  public void testHashCode() {
    final TransitNode first = new TransitNode(1, 1.0F, 1.0F, 1);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 1);
    TransitEdge<TransitNode> other = new TransitEdge<>(1, first, second, 1);
    Assert.assertEquals(mEdge.hashCode(), other.hashCode());

    other = new TransitEdge<>(1, second, first, 1);
    Assert.assertEquals(mEdge.hashCode(), other.hashCode());

    other = new TransitEdge<>(1, first, second, 2);
    Assert.assertEquals(mEdge.hashCode(), other.hashCode());

    other = new TransitEdge<>(2, first, second, 1);
    Assert.assertNotEquals(mEdge.hashCode(), other.hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitEdge#hasTransportationMode(ETransportationMode)}.
   */
  @Test
  public void testHasTransportationMode() {
    Assert.assertTrue(mEdge.hasTransportationMode(ETransportationMode.TRAM));

    Assert.assertFalse(mEdge.hasTransportationMode(ETransportationMode.CAR));
    Assert.assertFalse(mEdge.hasTransportationMode(ETransportationMode.BIKE));
    Assert.assertFalse(mEdge.hasTransportationMode(ETransportationMode.FOOT));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitEdge#setReversedProvider(de.tischner.cobweb.routing.model.graph.IReversedProvider)}.
   */
  @Test
  public void testSetReversedProvider() {
    mEdge.setReversedProvider(() -> true);
    Assert.assertEquals(2, mEdge.getSource().getId());
    Assert.assertEquals(1, mEdge.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitEdge#TransitEdge(int, de.tischner.cobweb.routing.model.graph.ICoreNode, de.tischner.cobweb.routing.model.graph.ICoreNode, double)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTransitEdge() {
    final TransitNode first = new TransitNode(1, 1.0F, 1.0F, 1);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 1);
    try {
      new TransitEdge<>(1, first, second, 1);
      new TransitEdge<>(-1, first, second, -1);
      new TransitEdge<>(0, first, second, 0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
