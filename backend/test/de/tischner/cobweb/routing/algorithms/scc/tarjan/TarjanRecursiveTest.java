package de.tischner.cobweb.routing.algorithms.scc.tarjan;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.algorithms.scc.StronglyConnectedComponent;
import de.tischner.cobweb.routing.model.graph.BasicEdge;
import de.tischner.cobweb.routing.model.graph.BasicGraph;
import de.tischner.cobweb.routing.model.graph.BasicNode;
import de.tischner.cobweb.routing.model.graph.road.IHasId;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;

/**
 * Test for the class {@link TarjanRecursive}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TarjanRecursiveTest {

  /**
   * Counter used for generating unique edge IDs.
   */
  private int mEdgeIdCounter;
  /**
   * The Tarjan instance used for testing.
   */
  private TarjanRecursive<BasicNode, BasicEdge<BasicNode>, BasicGraph> mTarjan;

  /**
   * Setups a Tarjan instance for testing.
   */
  @Before
  public void setUp() {
    final BasicGraph graph = new BasicGraph();
    final BasicNode firstNode = new BasicNode(1);
    final BasicNode secondNode = new BasicNode(2);
    final BasicNode thirdNode = new BasicNode(3);
    final BasicNode fourthNode = new BasicNode(4);
    final BasicNode fifthNode = new BasicNode(5);
    final BasicNode sixthNode = new BasicNode(6);

    graph.addNode(firstNode);
    graph.addNode(secondNode);
    graph.addNode(thirdNode);
    graph.addNode(fourthNode);
    graph.addNode(fifthNode);
    graph.addNode(sixthNode);

    addEdgeInBothDirections(graph, firstNode, secondNode);
    addEdgeInBothDirections(graph, secondNode, thirdNode);
    addEdgeInBothDirections(graph, firstNode, thirdNode);
    addEdgeInBothDirections(graph, thirdNode, fourthNode);
    addEdgeInBothDirections(graph, firstNode, fourthNode);
    addEdgeInBothDirections(graph, firstNode, fifthNode);
    addEdgeInBothDirections(graph, fifthNode, secondNode);
    addEdgeInBothDirections(graph, fifthNode, sixthNode);
    addEdgeInBothDirections(graph, sixthNode, fourthNode);

    final BasicNode seventhNode = new BasicNode(7);
    final BasicNode eighthNode = new BasicNode(8);
    final BasicNode ninthNode = new BasicNode(9);
    final BasicNode tenthNode = new BasicNode(10);

    graph.addNode(seventhNode);
    graph.addNode(eighthNode);
    graph.addNode(ninthNode);
    graph.addNode(tenthNode);

    addEdgeInOneDirection(graph, fourthNode, seventhNode);
    addEdgeInOneDirection(graph, seventhNode, eighthNode);
    addEdgeInOneDirection(graph, eighthNode, ninthNode);
    addEdgeInOneDirection(graph, ninthNode, seventhNode);
    addEdgeInOneDirection(graph, tenthNode, eighthNode);

    mTarjan = new TarjanRecursive<>(graph);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.scc.tarjan.ATarjan#getLargestScc()}.
   */
  @Test
  public void testGetLargestScc() {
    final StronglyConnectedComponent<BasicNode> scc = mTarjan.getLargestScc();
    final Set<Integer> ids = scc.getNodes().stream().map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertEquals(6, ids.size());
    Assert.assertTrue(ids.contains(1));
    Assert.assertTrue(ids.contains(2));
    Assert.assertTrue(ids.contains(3));
    Assert.assertTrue(ids.contains(4));
    Assert.assertTrue(ids.contains(5));
    Assert.assertTrue(ids.contains(6));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.scc.tarjan.ATarjan#getSccs()}.
   */
  @Test
  public void testGetSccs() {
    final Collection<StronglyConnectedComponent<BasicNode>> sccs = mTarjan.getSccs();
    final Set<Integer> sizes = sccs.stream().map(StronglyConnectedComponent::size).collect(Collectors.toSet());
    Assert.assertEquals(3, sizes.size());
    Assert.assertTrue(sizes.contains(6));
    Assert.assertTrue(sizes.contains(3));
    Assert.assertTrue(sizes.contains(1));

    final Set<Integer> smallScc = sccs.stream().filter(scc -> scc.size() == 1).map(StronglyConnectedComponent::getNodes)
        .findAny().orElse(new HashSet<>()).stream().map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertTrue(smallScc.contains(10));

    final Set<Integer> middleScc =
        sccs.stream().filter(scc -> scc.size() == 3).map(StronglyConnectedComponent::getNodes).findAny()
            .orElse(new HashSet<>()).stream().map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertTrue(middleScc.contains(7));
    Assert.assertTrue(middleScc.contains(8));
    Assert.assertTrue(middleScc.contains(9));

    final Set<Integer> largeScc = sccs.stream().filter(scc -> scc.size() == 6).map(StronglyConnectedComponent::getNodes)
        .findAny().orElse(new HashSet<>()).stream().map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertTrue(largeScc.contains(1));
    Assert.assertTrue(largeScc.contains(2));
    Assert.assertTrue(largeScc.contains(3));
    Assert.assertTrue(largeScc.contains(4));
    Assert.assertTrue(largeScc.contains(5));
    Assert.assertTrue(largeScc.contains(6));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.scc.tarjan.TarjanRecursive#TarjanRecursive(de.tischner.cobweb.routing.model.graph.IGraph)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTarjanRecursive() {
    try {
      new TarjanRecursive<>(new RoadGraph<RoadNode, RoadEdge<RoadNode>>());
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
  private void addEdgeInBothDirections(final BasicGraph graph, final BasicNode first, final BasicNode second) {
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
  private void addEdgeInOneDirection(final BasicGraph graph, final BasicNode first, final BasicNode second) {
    graph.addEdge(new BasicEdge<>(mEdgeIdCounter, first, second, 1.0));
    mEdgeIdCounter++;
  }

}
