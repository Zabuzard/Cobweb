package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.algorithms.shortestpath.IHasPathCost;
import de.tischner.cobweb.routing.model.graph.BasicEdge;
import de.tischner.cobweb.routing.model.graph.BasicGraph;
import de.tischner.cobweb.routing.model.graph.BasicNode;
import de.tischner.cobweb.routing.model.graph.EdgeCost;
import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.IPath;

/**
 * Test for the class {@link Dijkstra}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class DijkstraTest {
  /**
   * The Dijkstra used for testing.
   */
  private Dijkstra<BasicNode, BasicEdge<BasicNode>> mDijkstra;
  /**
   * Counter used for generating unique edge IDs.
   */
  private int mEdgeIdCounter;
  /**
   * The graph used for testing.
   */
  private BasicGraph mGraph;

  /**
   * Setups a Dijkstra instance for testing.
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

    mDijkstra = new Dijkstra<>(mGraph);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra#computeSearchSpace(java.util.Collection, de.tischner.cobweb.routing.model.graph.INode)}.
   */
  @Test
  public void testComputeSearchSpaceCollectionOfNN() {
    final List<Integer> searchSpace =
        mDijkstra.computeSearchSpace(mGraph.getNodeById(1).get(), mGraph.getNodeById(4).get()).stream()
            .map(IHasId::getId).collect(Collectors.toList());
    Assert.assertEquals(6, searchSpace.size());
    Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), searchSpace);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra#computeShortestPath(java.util.Collection, de.tischner.cobweb.routing.model.graph.INode)}.
   */
  @Test
  public void testComputeShortestPathCollectionOfNN() {
    final Optional<IPath<BasicNode, BasicEdge<BasicNode>>> possiblePath =
        mDijkstra.computeShortestPath(mGraph.getNodeById(1).get(), mGraph.getNodeById(4).get());
    Assert.assertTrue(possiblePath.isPresent());
    final IPath<BasicNode, BasicEdge<BasicNode>> path = possiblePath.get();

    Assert.assertEquals(3.0, path.getTotalCost(), 0.0001);
    Assert.assertEquals(1, path.getSource().getId());
    Assert.assertEquals(4, path.getDestination().getId());
    Assert.assertEquals(3, path.length());

    final Iterator<EdgeCost<BasicNode, BasicEdge<BasicNode>>> nodeIter = path.iterator();
    Assert.assertEquals(2, nodeIter.next().getEdge().getDestination().getId());
    Assert.assertEquals(3, nodeIter.next().getEdge().getDestination().getId());
    Assert.assertEquals(4, nodeIter.next().getEdge().getDestination().getId());

    Assert.assertFalse(nodeIter.hasNext());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra#computeShortestPathCost(java.util.Collection, de.tischner.cobweb.routing.model.graph.INode)}.
   */
  @Test
  public void testComputeShortestPathCostCollectionOfNN() {
    final BasicNode first = mGraph.getNodeById(1).get();
    final BasicNode fourth = mGraph.getNodeById(4).get();
    Optional<Double> result = mDijkstra.computeShortestPathCost(Collections.singletonList(first), fourth);
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals(3.0, result.get(), 0.0001);

    mGraph = new BasicGraph();
    final BasicNode firstNode = new BasicNode(1);
    final BasicNode secondNode = new BasicNode(2);
    final BasicNode thirdNode = new BasicNode(3);
    final BasicNode fourthNode = new BasicNode(4);

    mGraph.addNode(firstNode);
    mGraph.addNode(secondNode);
    mGraph.addNode(thirdNode);
    mGraph.addNode(fourthNode);

    addEdgeInOneDirection(mGraph, secondNode, firstNode, 1.0);
    addEdgeInBothDirections(mGraph, secondNode, thirdNode, 10.0);
    addEdgeInOneDirection(mGraph, thirdNode, fourthNode, 2.0);

    mDijkstra = new Dijkstra<>(mGraph);

    final List<BasicNode> sources = new ArrayList<>();
    sources.add(secondNode);
    sources.add(thirdNode);

    result = mDijkstra.computeShortestPathCost(sources, firstNode);
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals(1.0, result.get(), 0.0001);

    result = mDijkstra.computeShortestPathCost(sources, fourthNode);
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals(2.0, result.get(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra#computeShortestPathCostsReachable(java.util.Collection)}.
   */
  @Test
  public void testComputeShortestPathCostsReachableCollectionOfN() {
    final Map<BasicNode, ? extends IHasPathCost> nodeToDistance =
        mDijkstra.computeShortestPathCostsReachable(Collections.singletonList(mGraph.getNodeById(1).get()));
    Assert.assertEquals(6, nodeToDistance.size());
    Assert.assertEquals(0.0, nodeToDistance.get(mGraph.getNodeById(1).get()).getPathCost(), 0.0001);
    Assert.assertEquals(1.0, nodeToDistance.get(mGraph.getNodeById(2).get()).getPathCost(), 0.0001);
    Assert.assertEquals(2.0, nodeToDistance.get(mGraph.getNodeById(3).get()).getPathCost(), 0.0001);
    Assert.assertEquals(3.0, nodeToDistance.get(mGraph.getNodeById(4).get()).getPathCost(), 0.0001);
    Assert.assertEquals(4.0, nodeToDistance.get(mGraph.getNodeById(5).get()).getPathCost(), 0.0001);
    Assert.assertEquals(4.0, nodeToDistance.get(mGraph.getNodeById(6).get()).getPathCost(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra#Dijkstra(de.tischner.cobweb.routing.model.graph.IGraph)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testDijkstra() {
    try {
      new Dijkstra<>(new BasicGraph());
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
