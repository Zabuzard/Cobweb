package de.tischner.cobweb.routing.model.graph.link;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.tischner.cobweb.routing.model.graph.transit.NodeTime;
import de.tischner.cobweb.routing.model.graph.transit.TransitEdge;
import de.tischner.cobweb.routing.model.graph.transit.TransitGraph;
import de.tischner.cobweb.routing.model.graph.transit.TransitNode;
import de.tischner.cobweb.routing.model.graph.transit.TransitStop;

/**
 * Test for the class {@link LinkGraph}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class LinkGraphTest {
  /**
   * Counter used for generating unique edge IDs.
   */
  private int mEdgeIdCounter;
  /**
   * The graph used for testing.
   */
  private LinkGraph mGraph;

  /**
   * Setups a graph instance for testing.
   */
  @Before
  public void setUp() {
    final RoadGraph<ICoreNode, ICoreEdge<ICoreNode>> roadGraph = new RoadGraph<>();
    final RoadNode firstNode = new RoadNode(1, 1.0F, 1.0F);
    final RoadNode secondNode = new RoadNode(2, 2.0F, 2.0F);
    final RoadNode thirdNode = new RoadNode(3, 3.0F, 3.0F);
    final RoadNode fourthNode = new RoadNode(4, 4.0F, 4.0F);
    final RoadNode fifthNode = new RoadNode(5, 5.0F, 5.0F);
    final RoadNode sixthNode = new RoadNode(6, 6.0F, 6.0F);

    roadGraph.addNode(firstNode);
    roadGraph.addNode(secondNode);
    roadGraph.addNode(thirdNode);
    roadGraph.addNode(fourthNode);
    roadGraph.addNode(fifthNode);
    roadGraph.addNode(sixthNode);

    addRoadEdgeInBothDirections(roadGraph, firstNode, secondNode);
    addRoadEdgeInBothDirections(roadGraph, secondNode, thirdNode);
    addRoadEdgeInBothDirections(roadGraph, firstNode, thirdNode);
    addRoadEdgeInBothDirections(roadGraph, thirdNode, fourthNode);
    addRoadEdgeInBothDirections(roadGraph, firstNode, fourthNode);
    addRoadEdgeInBothDirections(roadGraph, firstNode, fifthNode);
    addRoadEdgeInBothDirections(roadGraph, fifthNode, secondNode);
    addRoadEdgeInBothDirections(roadGraph, fifthNode, sixthNode);
    addRoadEdgeInBothDirections(roadGraph, sixthNode, fourthNode);

    final TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> transitGraph = new TransitGraph<>();
    final TransitNode seventhNode = new TransitNode(1, 1.0F, 1.0F, 1);
    final TransitNode eightNode = new TransitNode(2, 2.0F, 2.0F, 2);
    final TransitNode ninthNode = new TransitNode(3, 3.0F, 3.0F, 3);

    transitGraph.addNode(seventhNode);
    transitGraph.addNode(eightNode);
    transitGraph.addNode(ninthNode);

    addTransitEdgeInBothDirections(transitGraph, seventhNode, eightNode);
    addTransitEdgeInBothDirections(transitGraph, eightNode, ninthNode);

    mGraph = new LinkGraph(roadGraph, transitGraph);

    final Map<ICoreNode, TransitStop<ICoreNode>> hubConnections = new HashMap<>();
    final List<NodeTime<ICoreNode>> nodes = new ArrayList<>();
    nodes.add(new NodeTime<>(seventhNode, 1));
    nodes.add(new NodeTime<>(eightNode, 2));
    hubConnections.put(fifthNode, new TransitStop<>(nodes, 1.0F, 1.0F));
    mGraph.initializeHubConnections(hubConnections);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkGraph#addEdge(ICoreEdge)}.
   */
  @Test
  public void testAddEdge() {
    final ICoreEdge<ICoreNode> edge = new RoadEdge<>(40, new RoadNode(1, 1.0F, 1.0F), new RoadNode(2, 2.0F, 2.0F),
        EHighwayType.MOTORWAY, 100, EnumSet.of(ETransportationMode.CAR));
    Assert.assertFalse(mGraph.containsEdge(edge));
    Assert.assertTrue(mGraph.addEdge(edge));
    Assert.assertTrue(mGraph.containsEdge(edge));
    Assert.assertFalse(mGraph.addEdge(edge));
    Assert.assertTrue(mGraph.containsEdge(edge));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkGraph#addNode(ICoreNode)}.
   */
  @Test
  public void testAddNode() {
    Assert.assertEquals(9, mGraph.size());
    mGraph.addNode(new RoadNode(10, 10.0F, 10.0F));
    Assert.assertEquals(10, mGraph.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkGraph#containsNodeWithId(int)}.
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
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkGraph#getNodeById(int)}.
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
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkGraph#getNodes()}.
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
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkGraph#isReversed()}.
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
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkGraph#LinkGraph(RoadGraph, TransitGraph)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testLinkGraph() {
    try {
      new LinkGraph(new RoadGraph<>(), new TransitGraph<>());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkGraph#removeNode(ICoreNode)}.
   */
  @Test
  public void testRemoveNode() {
    final RoadNode node = new RoadNode(10, 10.0F, 10.0F);
    Assert.assertEquals(9, mGraph.size());
    Assert.assertFalse(mGraph.removeNode(node));
    Assert.assertEquals(9, mGraph.size());
    mGraph.addNode(node);
    Assert.assertEquals(10, mGraph.size());
    Assert.assertTrue(mGraph.containsNodeWithId(10));
    Assert.assertTrue(mGraph.removeNode(node));
    Assert.assertFalse(mGraph.containsNodeWithId(10));
    Assert.assertEquals(9, mGraph.size());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.link.LinkGraph#reverse()}.
   */
  @Test
  public void testReverse() {
    final RoadNode first = new RoadNode(1, 1.0F, 1.0F);
    final RoadNode second = new RoadNode(2, 2.0F, 2.0F);
    final RoadNode third = new RoadNode(3, 3.0F, 3.0F);
    final ICoreEdge<ICoreNode> firstEdge =
        new RoadEdge<>(1, first, second, EHighwayType.MOTORWAY, 100, EnumSet.of(ETransportationMode.CAR));
    final ICoreEdge<ICoreNode> secondEdge =
        new RoadEdge<>(2, second, third, EHighwayType.MOTORWAY, 100, EnumSet.of(ETransportationMode.CAR));
    mGraph = new LinkGraph(new RoadGraph<>(), new TransitGraph<>());
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
   * Adds the two road edges to the given graph. One that goes from the first to
   * the second node and one in the opposite direction.
   *
   * @param graph  The graph to add the edges to
   * @param first  The first node
   * @param second The second node
   */
  private void addRoadEdgeInBothDirections(final RoadGraph<ICoreNode, ICoreEdge<ICoreNode>> graph, final RoadNode first,
      final RoadNode second) {
    addRoadEdgeInOneDirection(graph, first, second);
    addRoadEdgeInOneDirection(graph, second, first);
  }

  /**
   * Adds the road edge to the given graph. It goes from the first to the second
   * node.
   *
   * @param graph  The graph to add the edge to
   * @param first  The first node
   * @param second The second node
   */
  private void addRoadEdgeInOneDirection(final RoadGraph<ICoreNode, ICoreEdge<ICoreNode>> graph, final RoadNode first,
      final RoadNode second) {
    graph.addEdge(
        new RoadEdge<>(mEdgeIdCounter, first, second, EHighwayType.MOTORWAY, 100, EnumSet.of(ETransportationMode.CAR)));
    mEdgeIdCounter++;
  }

  /**
   * Adds the two transit edges to the given graph. One that goes from the first
   * to the second node and one in the opposite direction.
   *
   * @param graph  The graph to add the edges to
   * @param first  The first node
   * @param second The second node
   */
  private void addTransitEdgeInBothDirections(final TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> graph,
      final TransitNode first, final TransitNode second) {
    addTransitEdgeInOneDirection(graph, first, second);
    addTransitEdgeInOneDirection(graph, second, first);
  }

  /**
   * Adds the transit edge to the given graph. It goes from the first to the
   * second node.
   *
   * @param graph  The graph to add the edge to
   * @param first  The first node
   * @param second The second node
   */
  private void addTransitEdgeInOneDirection(final TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> graph,
      final TransitNode first, final TransitNode second) {
    graph.addEdge(new TransitEdge<ICoreNode>(mEdgeIdCounter, first, second, 1));
    mEdgeIdCounter++;
  }
}
