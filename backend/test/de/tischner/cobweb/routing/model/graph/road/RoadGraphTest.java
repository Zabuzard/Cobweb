package de.tischner.cobweb.routing.model.graph.road;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.routing.model.graph.BasicGraph;
import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IHasId;

/**
 * Test for the class {@link RoadGraph}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RoadGraphTest {
  /**
   * Counter used for generating unique edge IDs.
   */
  private int mEdgeIdCounter;
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
    final RoadNode firstNode = new RoadNode(1, 1.0F, 1.0F);
    final RoadNode secondNode = new RoadNode(2, 2.0F, 2.0F);
    final RoadNode thirdNode = new RoadNode(3, 3.0F, 3.0F);
    final RoadNode fourthNode = new RoadNode(4, 4.0F, 4.0F);
    final RoadNode fifthNode = new RoadNode(5, 5.0F, 5.0F);
    final RoadNode sixthNode = new RoadNode(6, 6.0F, 6.0F);

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
    final RoadEdge<RoadNode> edge = new RoadEdge<>(40, new RoadNode(1, 1.0F, 1.0F), new RoadNode(2, 2.0F, 2.0F),
        EHighwayType.MOTORWAY, 100, EnumSet.of(ETransportationMode.CAR));
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
    mGraph.addNode(new RoadNode(10, 10.0F, 10.0F));
    Assert.assertEquals(7, mGraph.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#containsNodeWithId(int)}.
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
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#generateUniqueNodeId()}.
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
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#generateUniqueWayId()}.
   */
  @Test
  public void testGenerateUniqueWayId() {
    Assert.assertEquals(0, mGraph.generateUniqueWayId());
    Assert.assertEquals(1, mGraph.generateUniqueWayId());
    Assert.assertEquals(2, mGraph.generateUniqueWayId());
    // Testing the whole range takes too long (1-2 seconds)
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#getNodeById(int)}.
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
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#getNodes()}.
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
    final RoadNode node = new RoadNode(10, 10.0F, 10.0F);
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
   * {@link de.tischner.cobweb.routing.model.graph.road.RoadGraph#reverse()}.
   */
  @Test
  public void testReverse() {
    final RoadNode first = new RoadNode(1, 1.0F, 1.0F);
    final RoadNode second = new RoadNode(2, 2.0F, 2.0F);
    final RoadNode third = new RoadNode(3, 3.0F, 3.0F);
    final RoadEdge<RoadNode> firstEdge =
        new RoadEdge<>(1, first, second, EHighwayType.MOTORWAY, 100, EnumSet.of(ETransportationMode.CAR));
    final RoadEdge<RoadNode> secondEdge =
        new RoadEdge<>(2, second, third, EHighwayType.MOTORWAY, 100, EnumSet.of(ETransportationMode.CAR));
    mGraph = new RoadGraph<>();
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
    graph.addEdge(
        new RoadEdge<>(mEdgeIdCounter, first, second, EHighwayType.MOTORWAY, 100, EnumSet.of(ETransportationMode.CAR)));
    mEdgeIdCounter++;
  }
}
