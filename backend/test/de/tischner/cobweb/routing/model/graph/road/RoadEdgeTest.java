package de.tischner.cobweb.routing.model.graph.road;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.parsing.osm.EHighwayType;

/**
 * Test for the class {@link RoadEdge}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
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
    final RoadNode first = new RoadNode(1L, 1.0, 1.0);
    final RoadNode second = new RoadNode(2L, 2.0, 2.0);
    mEdge = new RoadEdge<>(1L, first, second, EHighwayType.MOTORWAY, 100);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    final RoadNode first = new RoadNode(1L, 1.0, 1.0);
    final RoadNode second = new RoadNode(2L, 2.0, 2.0);
    RoadEdge<RoadNode> other = new RoadEdge<>(1L, first, second, EHighwayType.MOTORWAY, 100);
    Assert.assertEquals(mEdge, other);

    other = new RoadEdge<>(1L, second, first, EHighwayType.MOTORWAY, 100);
    Assert.assertNotEquals(mEdge, other);

    other = new RoadEdge<>(1L, first, second, EHighwayType.LIVING_STREET, 100);
    Assert.assertNotEquals(mEdge, other);

    other = new RoadEdge<>(2L, first, second, EHighwayType.MOTORWAY, 100);
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
    Assert.assertEquals(2L, mEdge.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(1L, mEdge.getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#getSource()}.
   */
  @Test
  public void testGetSource() {
    Assert.assertEquals(1L, mEdge.getSource().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#hashCode()}.
   */
  @Test
  public void testHashCode() {
    final RoadNode first = new RoadNode(1L, 1.0, 1.0);
    final RoadNode second = new RoadNode(2L, 2.0, 2.0);
    RoadEdge<RoadNode> other = new RoadEdge<>(1L, first, second, EHighwayType.MOTORWAY, 100);
    Assert.assertEquals(mEdge.hashCode(), other.hashCode());

    other = new RoadEdge<>(1L, second, first, EHighwayType.MOTORWAY, 100);
    Assert.assertNotEquals(mEdge.hashCode(), other.hashCode());

    other = new RoadEdge<>(1L, first, second, EHighwayType.LIVING_STREET, 100);
    Assert.assertNotEquals(mEdge.hashCode(), other.hashCode());

    other = new RoadEdge<>(2L, first, second, EHighwayType.MOTORWAY, 100);
    Assert.assertNotEquals(mEdge.hashCode(), other.hashCode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#RoadEdge(long, de.tischner.cobweb.routing.model.graph.INode, de.tischner.cobweb.routing.model.graph.INode, de.tischner.cobweb.parsing.osm.EHighwayType, int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testRoadEdge() {
    final RoadNode first = new RoadNode(1L, 1.0, 1.0);
    final RoadNode second = new RoadNode(2L, 2.0, 2.0);
    try {
      new RoadEdge<>(1L, first, second, EHighwayType.MOTORWAY, 100);
      new RoadEdge<>(-1L, first, second, EHighwayType.ROAD, 100);
      new RoadEdge<>(0L, first, second, EHighwayType.MOTORWAY, -20);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#setReversedProvider(de.tischner.cobweb.routing.model.graph.road.IReversedProvider)}.
   */
  @Test
  public void testSetReversedProvider() {
    mEdge.setReversedProvider(() -> true);
    Assert.assertEquals(2L, mEdge.getSource().getId());
    Assert.assertEquals(1L, mEdge.getDestination().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadEdge#updateCost()}.
   */
  @Test
  public void testUpdateCost() {
    final RoadNode first = new RoadNode(1L, 0.0, 0.0);
    final RoadNode second = new RoadNode(2L, 0.0, 0.0);
    mEdge = new RoadEdge<>(1L, first, second, EHighwayType.MOTORWAY, 100);
    Assert.assertEquals(0.0, mEdge.getCost(), 0.0001);

    first.setLatitude(1.0);
    first.setLongitude(1.0);
    second.setLatitude(2.0);
    second.setLongitude(2.0);
    mEdge.updateCost();
    Assert.assertTrue(mEdge.getCost() > 0);
  }

}
