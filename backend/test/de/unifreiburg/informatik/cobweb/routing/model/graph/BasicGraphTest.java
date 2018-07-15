package de.unifreiburg.informatik.cobweb.routing.model.graph;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link BasicGraph}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class BasicGraphTest {
  /**
   * Counter used for generating unique edge IDs.
   */
  private int mEdgeIdCounter;
  /**
   * The graph used for testing.
   */
  private BasicGraph mGraph;

  /**
   * Setups a graph instance for testing.
   */
  @Before
  public void setUp() {
    mGraph = new BasicGraph();
    final BasicNode firstNode = new BasicNode(1);
    final BasicNode secondNode = new BasicNode(2);
    final BasicNode thirdNode = new BasicNode(3);
    final BasicNode fourthNode = new BasicNode(4);
    final BasicNode fifthNode = new BasicNode(5);
    final BasicNode sixthNode = new BasicNode(6);

    mGraph.addNode(firstNode);
    mGraph.addNode(secondNode);
    mGraph.addNode(thirdNode);
    mGraph.addNode(fourthNode);
    mGraph.addNode(fifthNode);
    mGraph.addNode(sixthNode);

    addEdgeInBothDirections(mGraph, firstNode, secondNode, 1);
    addEdgeInBothDirections(mGraph, secondNode, thirdNode, 1);
    addEdgeInBothDirections(mGraph, firstNode, thirdNode, 3);
    addEdgeInBothDirections(mGraph, thirdNode, fourthNode, 1);
    addEdgeInBothDirections(mGraph, firstNode, fourthNode, 10);
    addEdgeInBothDirections(mGraph, firstNode, fifthNode, 4);
    addEdgeInBothDirections(mGraph, fifthNode, secondNode, 5);
    addEdgeInBothDirections(mGraph, fifthNode, sixthNode, 3);
    addEdgeInBothDirections(mGraph, sixthNode, fourthNode, 1);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.AGraph#addEdge(de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge)}.
   */
  @Test
  public void testAddEdge() {
    final BasicEdge<BasicNode> edge = new BasicEdge<>(10, new BasicNode(10), new BasicNode(11), 1.0);
    Assert.assertFalse(mGraph.containsEdge(edge));
    Assert.assertTrue(mGraph.addEdge(edge));
    Assert.assertTrue(mGraph.containsEdge(edge));
    Assert.assertFalse(mGraph.addEdge(edge));
    Assert.assertTrue(mGraph.containsEdge(edge));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicGraph#addNode(de.unifreiburg.informatik.cobweb.routing.model.graph.BasicNode)}.
   */
  @Test
  public void testAddNode() {
    Assert.assertEquals(6, mGraph.size());
    mGraph.addNode(new BasicNode(10));
    Assert.assertEquals(7, mGraph.size());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicGraph#BasicGraph()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testBasicGraph() {
    try {
      new BasicGraph();
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.AGraph#containsEdge(de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge)}.
   */
  @Test
  public void testContainsEdge() {
    final BasicEdge<BasicNode> edge = new BasicEdge<>(10, new BasicNode(10), new BasicNode(11), 1.0);
    Assert.assertFalse(mGraph.containsEdge(edge));
    mGraph.addEdge(edge);
    Assert.assertTrue(mGraph.containsEdge(edge));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicGraph#containsNodeWithId(int)}.
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
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.AGraph#getAmountOfEdges()}.
   */
  @Test
  public void testGetAmountOfEdges() {
    final BasicEdge<BasicNode> edge = new BasicEdge<>(10, new BasicNode(10), new BasicNode(11), 1.0);
    Assert.assertEquals(18, mGraph.getAmountOfEdges());
    mGraph.addEdge(edge);
    Assert.assertEquals(19, mGraph.getAmountOfEdges());

    Assert.assertEquals(0, new BasicGraph().getAmountOfEdges());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.AGraph#getEdges()}.
   */
  @Test
  public void testGetEdges() {
    final BasicNode first = new BasicNode(1);
    final BasicNode second = new BasicNode(2);
    final BasicNode third = new BasicNode(3);
    final BasicEdge<BasicNode> firstEdge = new BasicEdge<>(1, first, second, 1.0);
    final BasicEdge<BasicNode> secondEdge = new BasicEdge<>(2, second, third, 1.0);
    mGraph = new BasicGraph();
    mGraph.addNode(first);
    mGraph.addNode(second);
    mGraph.addNode(third);
    mGraph.addEdge(firstEdge);
    mGraph.addEdge(secondEdge);

    Assert.assertTrue(mGraph.containsEdge(firstEdge));
    Assert.assertTrue(mGraph.containsEdge(secondEdge));

    final Set<Integer> destinationIds =
        mGraph.getEdges().map(IEdge::getDestination).map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertTrue(destinationIds.contains(2));
    Assert.assertTrue(destinationIds.contains(3));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.AGraph#getIncomingEdges(de.unifreiburg.informatik.cobweb.routing.model.graph.INode)}.
   */
  @Test
  public void testGetIncomingEdges() {
    final Set<Integer> sourceIds = mGraph.getIncomingEdges(mGraph.getNodeById(1).get()).map(IEdge::getSource)
        .map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertEquals(4, sourceIds.size());
    Assert.assertTrue(sourceIds.contains(2));
    Assert.assertTrue(sourceIds.contains(3));
    Assert.assertTrue(sourceIds.contains(4));
    Assert.assertTrue(sourceIds.contains(5));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicGraph#getNodeById(int)}.
   */
  @Test
  public void testGetNodeById() {
    Assert.assertTrue(mGraph.getNodeById(1).isPresent());
    Assert.assertEquals(1L, mGraph.getNodeById(1).get().getId());
    Assert.assertTrue(mGraph.getNodeById(5).isPresent());
    Assert.assertEquals(5L, mGraph.getNodeById(5).get().getId());

    Assert.assertFalse(mGraph.getNodeById(8).isPresent());
    Assert.assertFalse(mGraph.getNodeById(-2).isPresent());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicGraph#getNodes()}.
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
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.AGraph#getOutgoingEdges(de.unifreiburg.informatik.cobweb.routing.model.graph.INode)}.
   */
  @Test
  public void testGetOutgoingEdges() {
    final Set<Integer> destinationIds = mGraph.getOutgoingEdges(mGraph.getNodeById(1).get()).map(IEdge::getDestination)
        .map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertEquals(4, destinationIds.size());
    Assert.assertTrue(destinationIds.contains(2));
    Assert.assertTrue(destinationIds.contains(3));
    Assert.assertTrue(destinationIds.contains(4));
    Assert.assertTrue(destinationIds.contains(5));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.AGraph#removeEdge(de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge)}.
   */
  @Test
  public void testRemoveEdge() {
    final BasicEdge<BasicNode> edge = new BasicEdge<>(10, new BasicNode(10), new BasicNode(11), 1.0);
    mGraph.addEdge(edge);
    Assert.assertTrue(mGraph.containsEdge(edge));
    Assert.assertTrue(mGraph.removeEdge(edge));
    Assert.assertFalse(mGraph.containsEdge(edge));
    Assert.assertFalse(mGraph.removeEdge(edge));
    Assert.assertFalse(mGraph.containsEdge(edge));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicGraph#removeNode(de.unifreiburg.informatik.cobweb.routing.model.graph.BasicNode)}.
   */
  @Test
  public void testRemoveNode() {
    final BasicNode node = new BasicNode(10);
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
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.BasicGraph#reverse()}.
   */
  @Test
  public void testReverse() {
    final BasicNode first = new BasicNode(1);
    final BasicNode second = new BasicNode(2);
    final BasicNode third = new BasicNode(3);
    final BasicEdge<BasicNode> firstEdge = new BasicEdge<>(1, first, second, 1.0);
    final BasicEdge<BasicNode> secondEdge = new BasicEdge<>(2, second, third, 1.0);
    mGraph = new BasicGraph();
    mGraph.addNode(first);
    mGraph.addNode(second);
    mGraph.addNode(third);
    mGraph.addEdge(firstEdge);
    mGraph.addEdge(secondEdge);

    mGraph.reverse();

    Assert.assertTrue(mGraph.getEdges().anyMatch(edge -> firstEdge.equals(edge)));
    Assert.assertTrue(mGraph.getEdges().anyMatch(edge -> secondEdge.equals(edge)));

    final Set<Integer> destinationIds =
        mGraph.getEdges().map(IEdge::getDestination).map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertTrue(destinationIds.contains(1));
    Assert.assertTrue(destinationIds.contains(2));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.graph.AGraph#size()}.
   */
  @Test
  public void testSize() {
    Assert.assertEquals(6, mGraph.size());
    mGraph.addNode(new BasicNode(10));
    Assert.assertEquals(7, mGraph.size());

    Assert.assertEquals(0, new BasicGraph().size());
  }

  /**
   * Adds the two edges to the given graph. One that goes from the first to the
   * second node and one in the opposite direction.
   *
   * @param graph  The graph to add the edges to
   * @param first  The first node
   * @param second The second node
   * @param cost   The cost of the edge
   */
  private void addEdgeInBothDirections(final BasicGraph graph, final BasicNode first, final BasicNode second,
      final double cost) {
    addEdgeInOneDirection(graph, first, second, cost);
    addEdgeInOneDirection(graph, second, first, cost);
  }

  /**
   * Adds the edge to the given graph. It goes from the first to the second
   * node.
   *
   * @param graph  The graph to add the edge to
   * @param first  The first node
   * @param second The second node
   * @param cost   The cost of the edge
   */
  private void addEdgeInOneDirection(final BasicGraph graph, final BasicNode first, final BasicNode second,
      final double cost) {
    graph.addEdge(new BasicEdge<>(mEdgeIdCounter, first, second, cost));
    mEdgeIdCounter++;
  }

}
