package de.tischner.cobweb.routing.model.graph.road;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.parsing.osm.EHighwayType;

/**
 * Test for the class {@link RoadEdge}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RoadEdgeTest {

  /**
   * The edge used for testing.
   */
  private RoadEdge<RoadNode> mEdge;

  /**
   * Setups an edge instance for testing.
   */
  @Before
  public void setUp() {
    final RoadNode first = new RoadNode(1, 1.0F, 1.0F);
    final RoadNode second = new RoadNode(2, 2.0F, 2.0F);
    mEdge = new RoadEdge<>(1, first, second, EHighwayType.MOTORWAY, 100);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    final RoadNode first = new RoadNode(1, 1.0F, 1.0F);
    final RoadNode second = new RoadNode(2, 2.0F, 2.0F);
    RoadEdge<RoadNode> other = new RoadEdge<>(1, first, second, EHighwayType.MOTORWAY, 100);
    Assert.assertEquals(mEdge, other);

    other = new RoadEdge<>(1, second, first, EHighwayType.MOTORWAY, 100);
    Assert.assertNotEquals(mEdge, other);

    other = new RoadEdge<>(1, first, second, EHighwayType.LIVING_STREET, 100);
    Assert.assertNotEquals(mEdge, other);

    other = new RoadEdge<>(2, first, second, EHighwayType.MOTORWAY, 100);
    Assert.assertNotEquals(mEdge, other);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#getCost()}.
   */
  @Test
  public void testGetCost() {
    Assert.assertTrue(mEdge.getCost() > 0.0);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#getDestination()}.
   */
  @Test
  public void testGetDestination() {
    Assert.assertEquals(2, mEdge.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(1, mEdge.getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#getSource()}.
   */
  @Test
  public void testGetSource() {
    Assert.assertEquals(1, mEdge.getSource().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#hashCode()}.
   */
  @Test
  public void testHashCode() {
    final RoadNode first = new RoadNode(1, 1.0F, 1.0F);
    final RoadNode second = new RoadNode(2, 2.0F, 2.0F);
    RoadEdge<RoadNode> other = new RoadEdge<>(1, first, second, EHighwayType.MOTORWAY, 100);
    Assert.assertEquals(mEdge.hashCode(), other.hashCode());

    other = new RoadEdge<>(1, second, first, EHighwayType.MOTORWAY, 100);
    Assert.assertNotEquals(mEdge.hashCode(), other.hashCode());

    other = new RoadEdge<>(1, first, second, EHighwayType.LIVING_STREET, 100);
    Assert.assertNotEquals(mEdge.hashCode(), other.hashCode());

    other = new RoadEdge<>(2, first, second, EHighwayType.MOTORWAY, 100);
    Assert.assertNotEquals(mEdge.hashCode(), other.hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#RoadEdge(int, de.tischner.cobweb.routing.model.graph.ICoreNode, de.tischner.cobweb.routing.model.graph.ICoreNode, EHighwayType, int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testRoadEdge() {
    final RoadNode first = new RoadNode(1, 1.0F, 1.0F);
    final RoadNode second = new RoadNode(2, 2.0F, 2.0F);
    try {
      new RoadEdge<>(1, first, second, EHighwayType.MOTORWAY, 100);
      new RoadEdge<>(-1, first, second, EHighwayType.ROAD, 100);
      new RoadEdge<>(0, first, second, EHighwayType.MOTORWAY, -20);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#setReversedProvider(de.tischner.cobweb.routing.model.graph.IReversedProvider)}.
   */
  @Test
  public void testSetReversedProvider() {
    mEdge.setReversedProvider(() -> true);
    Assert.assertEquals(2, mEdge.getSource().getId());
    Assert.assertEquals(1, mEdge.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#updateCost()}.
   */
  @Test
  public void testUpdateCost() {
    final RoadNode first = new RoadNode(1, 0.0F, 0.0F);
    final RoadNode second = new RoadNode(2, 0.0F, 0.0F);
    mEdge = new RoadEdge<>(1, first, second, EHighwayType.MOTORWAY, 100);
    Assert.assertEquals(0.0, mEdge.getCost(), 0.0001);

    first.setLatitude(1.0F);
    first.setLongitude(1.0F);
    second.setLatitude(2.0F);
    second.setLongitude(2.0F);
    mEdge.updateCost();
    Assert.assertTrue(mEdge.getCost() > 0);
  }

}
