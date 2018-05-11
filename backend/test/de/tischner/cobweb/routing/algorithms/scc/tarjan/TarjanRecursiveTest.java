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
  private long mEdgeIdCounter;
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
    final BasicNode firstNode = new BasicNode(1L);
    final BasicNode secondNode = new BasicNode(2L);
    final BasicNode thirdNode = new BasicNode(3L);
    final BasicNode fourthNode = new BasicNode(4L);
    final BasicNode fifthNode = new BasicNode(5L);
    final BasicNode sixthNode = new BasicNode(6L);

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

    final BasicNode seventhNode = new BasicNode(7L);
    final BasicNode eighthNode = new BasicNode(8L);
    final BasicNode ninthNode = new BasicNode(9L);
    final BasicNode tenthNode = new BasicNode(10L);

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
    final Set<Long> ids = scc.getNodes().stream().map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertEquals(6, ids.size());
    Assert.assertTrue(ids.contains(1L));
    Assert.assertTrue(ids.contains(2L));
    Assert.assertTrue(ids.contains(3L));
    Assert.assertTrue(ids.contains(4L));
    Assert.assertTrue(ids.contains(5L));
    Assert.assertTrue(ids.contains(6L));
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

    final Set<Long> smallScc = sccs.stream().filter(scc -> scc.size() == 1).map(StronglyConnectedComponent::getNodes)
        .findAny().orElse(new HashSet<>()).stream().map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertTrue(smallScc.contains(10L));

    final Set<Long> middleScc = sccs.stream().filter(scc -> scc.size() == 3).map(StronglyConnectedComponent::getNodes)
        .findAny().orElse(new HashSet<>()).stream().map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertTrue(middleScc.contains(7L));
    Assert.assertTrue(middleScc.contains(8L));
    Assert.assertTrue(middleScc.contains(9L));

    final Set<Long> largeScc = sccs.stream().filter(scc -> scc.size() == 6).map(StronglyConnectedComponent::getNodes)
        .findAny().orElse(new HashSet<>()).stream().map(IHasId::getId).collect(Collectors.toSet());
    Assert.assertTrue(largeScc.contains(1L));
    Assert.assertTrue(largeScc.contains(2L));
    Assert.assertTrue(largeScc.contains(3L));
    Assert.assertTrue(largeScc.contains(4L));
    Assert.assertTrue(largeScc.contains(5L));
    Assert.assertTrue(largeScc.contains(6L));
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
