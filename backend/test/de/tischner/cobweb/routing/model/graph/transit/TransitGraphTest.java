package de.tischner.cobweb.routing.model.graph.transit;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.BasicGraph;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IHasId;

/**
 * Test for the class {@link TransitGraph}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TransitGraphTest {
  /**
   * Counter used for generating unique edge IDs.
   */
  private int mEdgeIdCounter;
  /**
   * The graph used for testing.
   */
  private TransitGraph<TransitNode, TransitEdge<TransitNode>> mGraph;

  /**
   * Setups a graph instance for testing.
   */
  @Before
  public void setUp() {
    mGraph = new TransitGraph<>();
    final TransitNode firstNode = new TransitNode(1, 1.0F, 1.0F, 1);
    final TransitNode secondNode = new TransitNode(2, 2.0F, 2.0F, 2);
    final TransitNode thirdNode = new TransitNode(3, 3.0F, 3.0F, 3);
    final TransitNode fourthNode = new TransitNode(4, 4.0F, 4.0F, 4);
    final TransitNode fifthNode = new TransitNode(5, 5.0F, 5.0F, 5);
    final TransitNode sixthNode = new TransitNode(6, 6.0F, 6.0F, 6);

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
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#addEdge(de.tischner.cobweb.routing.model.graph.IEdge)}.
   */
  @Test
  public void testAddEdge() {
    final TransitEdge<TransitNode> edge =
        new TransitEdge<>(40, new TransitNode(1, 1.0F, 1.0F, 1), new TransitNode(2, 2.0F, 2.0F, 2), 1);
    Assert.assertFalse(mGraph.containsEdge(edge));
    Assert.assertTrue(mGraph.addEdge(edge));
    Assert.assertTrue(mGraph.containsEdge(edge));
    Assert.assertFalse(mGraph.addEdge(edge));
    Assert.assertTrue(mGraph.containsEdge(edge));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#addNode(de.tischner.cobweb.routing.model.graph.INode)}.
   */
  @Test
  public void testAddNode() {
    Assert.assertEquals(6, mGraph.size());
    mGraph.addNode(new TransitNode(10, 10.0F, 10.0F, 1));
    Assert.assertEquals(7, mGraph.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#addStop(TransitStop)}.
   */
  @Test
  public void testAddStop() {
    Collection<TransitStop<TransitNode>> stops = mGraph.getStops();
    Assert.assertTrue(stops.isEmpty());

    final TransitStop<TransitNode> firstStop = new TransitStop<>(Collections.emptyList(), 1.0F, 1.0F);
    mGraph.addStop(firstStop);
    stops = mGraph.getStops();
    Assert.assertEquals(1, stops.size());
    Assert.assertTrue(stops.contains(firstStop));

    final TransitStop<TransitNode> secondStop = new TransitStop<>(Collections.emptyList(), 1.0F, 1.0F);
    mGraph.addStop(secondStop);
    stops = mGraph.getStops();
    Assert.assertEquals(2, stops.size());
    Assert.assertTrue(stops.contains(secondStop));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#containsNodeWithId(int)}.
   */
  @Test
  public void testContainsNodeWithId() {
    Assert.assertTrue(mGraph.containsNodeWithId(1));
    Assert.assertTrue(mGraph.containsNodeWithId(5));
    Assert.assertFalse(mGraph.containsNodeWithId(8));
    Assert.assertFalse(mGraph.containsNodeWithId(-2));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#generateUniqueEdgeId()}.
   */
  @Test
  public void testGenerateUniqueEdgeId() {
    Assert.assertEquals(0, mGraph.generateUniqueEdgeId());
    Assert.assertEquals(1, mGraph.generateUniqueEdgeId());
    Assert.assertEquals(2, mGraph.generateUniqueEdgeId());
    // Testing the whole range takes too long (1-2 seconds)
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#generateUniqueNodeId()}.
   */
  @Test
  public void testGenerateUniqueNodeId() {
    Assert.assertEquals(0, mGraph.generateUniqueNodeId());
    Assert.assertEquals(1, mGraph.generateUniqueNodeId());
    Assert.assertEquals(2, mGraph.generateUniqueNodeId());
    // Testing the whole range takes too long (1-2 seconds)
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#getNodeById(int)}.
   */
  @Test
  public void testGetNodeById() {
    Assert.assertTrue(mGraph.getNodeById(1).isPresent());
    Assert.assertEquals(1, mGraph.getNodeById(1).get().getId());
    Assert.assertTrue(mGraph.getNodeById(5).isPresent());
    Assert.assertEquals(5, mGraph.getNodeById(5).get().getId());

    Assert.assertFalse(mGraph.getNodeById(8).isPresent());
    Assert.assertFalse(mGraph.getNodeById(-2).isPresent());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#getNodes()}.
   */
  @Test
  public void testGetNodes() {
    final Set<Integer> nodeIds = mGraph.getNodes().stream().map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertEquals(6, nodeIds.size());
    Assert.assertTrue(nodeIds.contains(1));
    Assert.assertTrue(nodeIds.contains(2));
    Assert.assertTrue(nodeIds.contains(3));
    Assert.assertTrue(nodeIds.contains(4));
    Assert.assertTrue(nodeIds.contains(5));
    Assert.assertTrue(nodeIds.contains(6));

    Assert.assertTrue(new BasicGraph().getNodes().isEmpty());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#getStops()}.
   */
  @Test
  public void testGetStops() {
    Collection<TransitStop<TransitNode>> stops = mGraph.getStops();
    Assert.assertTrue(stops.isEmpty());

    final TransitStop<TransitNode> firstStop = new TransitStop<>(Collections.emptyList(), 1.0F, 1.0F);
    mGraph.addStop(firstStop);
    stops = mGraph.getStops();
    Assert.assertEquals(1, stops.size());
    Assert.assertTrue(stops.contains(firstStop));

    final TransitStop<TransitNode> secondStop = new TransitStop<>(Collections.emptyList(), 1.0F, 1.0F);
    mGraph.addStop(secondStop);
    stops = mGraph.getStops();
    Assert.assertEquals(2, stops.size());
    Assert.assertTrue(stops.contains(secondStop));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#isReversed()}.
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
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#removeNode(de.tischner.cobweb.routing.model.graph.INode)}.
   */
  @Test
  public void testRemoveNode() {
    final TransitNode node = new TransitNode(10, 10.0F, 10.0F, 1);
    Assert.assertEquals(6, mGraph.size());
    Assert.assertFalse(mGraph.removeNode(node));
    Assert.assertEquals(6, mGraph.size());
    mGraph.addNode(node);
    Assert.assertEquals(7, mGraph.size());
    Assert.assertTrue(mGraph.containsNodeWithId(10));
    Assert.assertTrue(mGraph.removeNode(node));
    Assert.assertFalse(mGraph.containsNodeWithId(10));
    Assert.assertEquals(6, mGraph.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#reverse()}.
   */
  @Test
  public void testReverse() {
    final TransitNode first = new TransitNode(1, 1.0F, 1.0F, 1);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 2);
    final TransitNode third = new TransitNode(3, 3.0F, 3.0F, 3);
    final TransitEdge<TransitNode> firstEdge = new TransitEdge<>(1, first, second, 1);
    final TransitEdge<TransitNode> secondEdge = new TransitEdge<>(2, second, third, 2);
    mGraph = new TransitGraph<>();
    mGraph.addNode(first);
    mGraph.addNode(second);
    mGraph.addNode(third);
    mGraph.addEdge(firstEdge);
    mGraph.addEdge(secondEdge);

    mGraph.reverse();

    Assert.assertTrue(mGraph.containsEdge(firstEdge));
    Assert.assertTrue(mGraph.containsEdge(secondEdge));

    final Set<Integer> destinationIds =
        mGraph.getEdges().map(IEdge::getDestination).map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertTrue(destinationIds.contains(1));
    Assert.assertTrue(destinationIds.contains(2));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.transit.TransitGraph#TransitGraph()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTransitGraph() {
    try {
      new TransitGraph<>();
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
  private void addEdgeInBothDirections(final TransitGraph<TransitNode, TransitEdge<TransitNode>> graph,
      final TransitNode first, final TransitNode second) {
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
  private void addEdgeInOneDirection(final TransitGraph<TransitNode, TransitEdge<TransitNode>> graph,
      final TransitNode first, final TransitNode second) {
    graph.addEdge(new TransitEdge<>(mEdgeIdCounter, first, second, 1));
    mEdgeIdCounter++;
  }
}
