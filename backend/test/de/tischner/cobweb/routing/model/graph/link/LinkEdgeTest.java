package de.tischner.cobweb.routing.model.graph.link;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.tischner.cobweb.routing.model.graph.transit.TransitNode;

/**
 * Test for the class {@link LinkEdge}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class LinkEdgeTest {

  /**
   * The edge used for testing.
   */
  private LinkEdge<ICoreNode> mEdge;

  /**
   * Setups an edge instance for testing.
   */
  @Before
  public void setUp() {
    final RoadNode first = new RoadNode(1, 1.0F, 1.0F);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 2);
    mEdge = new LinkEdge<>(first, second);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkEdge#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mEdge, mEdge);

    final RoadNode first = new RoadNode(1, 1.0F, 1.0F);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 2);
    LinkEdge<ICoreNode> other = new LinkEdge<>(first, second);
    Assert.assertNotEquals(mEdge, other);

    other = new LinkEdge<>(second, first);
    Assert.assertNotEquals(mEdge, other);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkEdge#getCost()}.
   */
  @Test
  public void testGetCost() {
    Assert.assertEquals(0.0, mEdge.getCost(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkEdge#getDestination()}.
   */
  @Test
  public void testGetDestination() {
    Assert.assertEquals(2, mEdge.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkEdge#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(-1, mEdge.getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkEdge#getSource()}.
   */
  @Test
  public void testGetSource() {
    Assert.assertEquals(1, mEdge.getSource().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkEdge#getTransportationModes()}.
   */
  @Test
  public void testGetTransportationModes() {
    final Set<ETransportationMode> modes = mEdge.getTransportationModes();
    Assert.assertEquals(3, modes.size());
    Assert.assertTrue(modes.contains(ETransportationMode.CAR));
    Assert.assertTrue(modes.contains(ETransportationMode.BIKE));
    Assert.assertTrue(modes.contains(ETransportationMode.FOOT));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkEdge#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mEdge.hashCode(), mEdge.hashCode());

    final RoadNode first = new RoadNode(1, 1.0F, 1.0F);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 2);
    LinkEdge<ICoreNode> other = new LinkEdge<>(first, second);
    Assert.assertNotEquals(mEdge.hashCode(), other.hashCode());

    other = new LinkEdge<>(second, first);
    Assert.assertNotEquals(mEdge.hashCode(), other.hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkEdge#hasTransportationMode(ETransportationMode)}.
   */
  @Test
  public void testHasTransportationMode() {
    Assert.assertFalse(mEdge.hasTransportationMode(ETransportationMode.TRAM));

    Assert.assertTrue(mEdge.hasTransportationMode(ETransportationMode.CAR));
    Assert.assertTrue(mEdge.hasTransportationMode(ETransportationMode.BIKE));
    Assert.assertTrue(mEdge.hasTransportationMode(ETransportationMode.FOOT));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkEdge#setReversedProvider(de.tischner.cobweb.routing.model.graph.IReversedProvider)}.
   */
  @Test
  public void testSetReversedProvider() {
    mEdge.setReversedProvider(() -> true);
    Assert.assertEquals(2, mEdge.getSource().getId());
    Assert.assertEquals(1, mEdge.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkEdge#LinkEdge(ICoreNode, ICoreNode)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTransitEdge() {
    final RoadNode first = new RoadNode(1, 1.0F, 1.0F);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 2);
    try {
      new LinkEdge<>(first, second);
      new LinkEdge<>(null, null);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
