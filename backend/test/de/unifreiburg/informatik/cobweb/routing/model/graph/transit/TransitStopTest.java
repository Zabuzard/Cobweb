package de.unifreiburg.informatik.cobweb.routing.model.graph.transit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unifreiburg.informatik.cobweb.routing.model.graph.BasicNode;

/**
 * Test for the class {@link TransitStop}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TransitStopTest {
  /**
   * The transit stop used for testing.
   */
  private TransitStop<BasicNode> mTransitStop;

  /**
   * Setups a highway data instance for testing.
   */
  @Before
  public void setUp() {
    final NodeTime<BasicNode> first = new NodeTime<>(new BasicNode(1), 1);
    final NodeTime<BasicNode> second = new NodeTime<>(new BasicNode(2), 2);
    final List<NodeTime<BasicNode>> nodes = new ArrayList<>();
    nodes.add(first);
    nodes.add(second);
    mTransitStop = new TransitStop<>(nodes, 1.0F, 1.0F);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitStop#getArrivalNodes()}.
   */
  @Test
  public void testGetArrivalNodes() {
    final Collection<NodeTime<BasicNode>> nodes = mTransitStop.getArrivalNodes();
    Assert.assertEquals(2, nodes.size());
    final Iterator<NodeTime<BasicNode>> nodeIter = nodes.iterator();
    Assert.assertEquals(1, nodeIter.next().getNode().getId());
    Assert.assertEquals(2, nodeIter.next().getNode().getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitStop#getLatitude()}.
   */
  @Test
  public void testGetLatitude() {
    Assert.assertEquals(1.0F, mTransitStop.getLatitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitStop#getLongitude()}.
   */
  @Test
  public void testGetLongitude() {
    Assert.assertEquals(1.0F, mTransitStop.getLongitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitStop#setLatitude(float)}.
   */
  @Test
  public void testSetLatitude() {
    mTransitStop.setLatitude(2.0F);
    Assert.assertEquals(2.0F, mTransitStop.getLatitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitStop#setLongitude(float)}.
   */
  @Test
  public void testSetLongitude() {
    mTransitStop.setLongitude(2.0F);
    Assert.assertEquals(2.0F, mTransitStop.getLongitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitStop#TransitStop(java.util.List, float, float)}.
   */
  @SuppressWarnings({ "static-method", "unused" })
  @Test
  public void testTransitStop() {
    final NodeTime<BasicNode> first = new NodeTime<>(new BasicNode(1), 1);
    final NodeTime<BasicNode> second = new NodeTime<>(new BasicNode(2), 2);
    final List<NodeTime<BasicNode>> nodes = new ArrayList<>();
    nodes.add(first);
    nodes.add(second);
    try {
      new TransitStop<>(nodes, 1.0F, 1.0F);
      new TransitStop<>(Collections.singletonList(first), 0.0F, 0.0F);
      new TransitStop<>(Collections.emptyList(), -1.0F, -1.0F);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
