package de.tischner.cobweb.routing.model.graph.road;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.routing.model.graph.BasicGraph;
import de.tischner.cobweb.routing.model.graph.IEdge;

/**
 * Test for the class {@link RoadGraph}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RoadGraphTest {
  /**
   * Counter used for generating unique edge IDs.
   */
  private long mEdgeIdCounter;
  /**
   * The graph used for testing.
   */
  private RoadGraph<RoadNode, RoadEdge<RoadNode>> mGraph;

  /**
   * Setups a graph instance for testing.
   */
  @Before
  public void setUp() {
    mGraph = new RoadGraph<>();
    final RoadNode firstNode = new RoadNode(1L, 1.0, 1.0);
    final RoadNode secondNode = new RoadNode(2L, 2.0, 2.0);
    final RoadNode thirdNode = new RoadNode(3L, 3.0, 3.0);
    final RoadNode fourthNode = new RoadNode(4L, 4.0, 4.0);
    final RoadNode fifthNode = new RoadNode(5L, 5.0, 5.0);
    final RoadNode sixthNode = new RoadNode(6L, 6.0, 6.0);

    mGraph.addNode(firstNode);
    mGraph.addNode(secondNode);
    mGraph.addNode(thirdNode);
    mGraph.addNode(fourthNode);
    mGraph.addNode(fifthNode);
    mGraph.addNode(sixthNode);

    addEdgeInBothDirections(mGraph, firstNode, secondNode);
    addEdgeInBothDirections(mGraph, secondNode, thirdNode);
    addEdgeInBothDirections(mGraph, firstNode, thirdNode);
    addEdgeInBothDirections(mGraph, thirdNode, fourthNode);
    addEdgeInBothDirections(mGraph, firstNode, fourthNode);
    addEdgeInBothDirections(mGraph, firstNode, fifthNode);
    addEdgeInBothDirections(mGraph, fifthNode, secondNode);
    addEdgeInBothDirections(mGraph, fifthNode, sixthNode);
    addEdgeInBothDirections(mGraph, sixthNode, fourthNode);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#addEdge(de.tischner.cobweb.routing.model.graph.IEdge)}.
   */
  @Test
  public void testAddEdge() {
    final RoadEdge<RoadNode> edge =
        new RoadEdge<>(40L, new RoadNode(1L, 1.0, 1.0), new RoadNode(2L, 2.0, 2.0), EHighwayType.MOTORWAY, 100);
    Assert.assertFalse(mGraph.containsEdge(edge));
    Assert.assertTrue(mGraph.addEdge(edge));
    Assert.assertTrue(mGraph.containsEdge(edge));
    Assert.assertFalse(mGraph.addEdge(edge));
    Assert.assertTrue(mGraph.containsEdge(edge));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#addNode(de.tischner.cobweb.routing.model.graph.INode)}.
   */
  @Test
  public void testAddNode() {
    Assert.assertEquals(6, mGraph.size());
    mGraph.addNode(new RoadNode(10L, 10.0, 10.0));
    Assert.assertEquals(7, mGraph.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#containsNodeWithId(long)}.
   */
  @Test
  public void testContainsNodeWithId() {
    Assert.assertTrue(mGraph.containsNodeWithId(1L));
    Assert.assertTrue(mGraph.containsNodeWithId(5L));
    Assert.assertFalse(mGraph.containsNodeWithId(8L));
    Assert.assertFalse(mGraph.containsNodeWithId(-2L));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#getNodeById(long)}.
   */
  @Test
  public void testGetNodeById() {
    Assert.assertTrue(mGraph.getNodeById(1L).isPresent());
    Assert.assertEquals(1L, mGraph.getNodeById(1L).get().getId());
    Assert.assertTrue(mGraph.getNodeById(5L).isPresent());
    Assert.assertEquals(5L, mGraph.getNodeById(5L).get().getId());

    Assert.assertFalse(mGraph.getNodeById(8L).isPresent());
    Assert.assertFalse(mGraph.getNodeById(-2L).isPresent());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#getNodes()}.
   */
  @Test
  public void testGetNodes() {
    final Set<Long> nodeIds = mGraph.getNodes().stream().map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertEquals(6, nodeIds.size());
    Assert.assertTrue(nodeIds.contains(1L));
    Assert.assertTrue(nodeIds.contains(2L));
    Assert.assertTrue(nodeIds.contains(3L));
    Assert.assertTrue(nodeIds.contains(4L));
    Assert.assertTrue(nodeIds.contains(5L));
    Assert.assertTrue(nodeIds.contains(6L));

    Assert.assertTrue(new BasicGraph().getNodes().isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#isReversed()}.
   */
  @Test
  public void testIsReversed() {
    Assert.assertFalse(mGraph.isReversed());
    mGraph.reverse();
    Assert.assertTrue(mGraph.isReversed());
    mGraph.reverse();
    Assert.assertFalse(mGraph.isReversed());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#removeNode(de.tischner.cobweb.routing.model.graph.INode)}.
   */
  @Test
  public void testRemoveNode() {
    final RoadNode node = new RoadNode(10L, 10.0, 10.0);
    Assert.assertEquals(6, mGraph.size());
    Assert.assertFalse(mGraph.removeNode(node));
    Assert.assertEquals(6, mGraph.size());
    mGraph.addNode(node);
    Assert.assertEquals(7, mGraph.size());
    Assert.assertTrue(mGraph.containsNodeWithId(10L));
    Assert.assertTrue(mGraph.removeNode(node));
    Assert.assertFalse(mGraph.containsNodeWithId(10L));
    Assert.assertEquals(6, mGraph.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#reverse()}.
   */
  @Test
  public void testReverse() {
    final RoadNode first = new RoadNode(1L, 1.0, 1.0);
    final RoadNode second = new RoadNode(2L, 2.0, 2.0);
    final RoadNode third = new RoadNode(3L, 3.0, 3.0);
    final RoadEdge<RoadNode> firstEdge = new RoadEdge<>(1L, first, second, EHighwayType.MOTORWAY, 100);
    final RoadEdge<RoadNode> secondEdge = new RoadEdge<>(2L, second, third, EHighwayType.MOTORWAY, 100);
    mGraph = new RoadGraph<>();
    mGraph.addNode(first);
    mGraph.addNode(second);
    mGraph.addNode(third);
    mGraph.addEdge(firstEdge);
    mGraph.addEdge(secondEdge);

    mGraph.reverse();

    Assert.assertTrue(mGraph.containsEdge(firstEdge));
    Assert.assertTrue(mGraph.containsEdge(secondEdge));

    final Set<Long> destinationIds =
        mGraph.getEdges().map(IEdge::getDestination).map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertTrue(destinationIds.contains(1L));
    Assert.assertTrue(destinationIds.contains(2L));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#RoadGraph()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testRoadGraph() {
    try {
      new RoadGraph<>();
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Adds the two edges to the given graph. One that goes from the first to the
   * second node and one in the opposite direction.
   *
   * @param graph  The graph to add the edges to
   * @param first  The first node
   * @param second The second node
   */
  private void addEdgeInBothDirections(final RoadGraph<RoadNode, RoadEdge<RoadNode>> graph, final RoadNode first,
      final RoadNode second) {
    addEdgeInOneDirection(graph, first, second);
    addEdgeInOneDirection(graph, second, first);
  }

  /**
   * Adds the edge to the given graph. It goes from the first to the second
   * node.
   *
   * @param graph  The graph to add the edge to
   * @param first  The first node
   * @param second The second node
   */
  private void addEdgeInOneDirection(final RoadGraph<RoadNode, RoadEdge<RoadNode>> graph, final RoadNode first,
      final RoadNode second) {
    graph.addEdge(new RoadEdge<>(mEdgeIdCounter, first, second, EHighwayType.MOTORWAY, 100));
    mEdgeIdCounter++;
  }
}
