package de.tischner.cobweb.routing.parsing.osm;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Tag;
import de.topobyte.osm4j.core.model.impl.Way;

/**
 * Test for the class {@link OsmRoadBuilder}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class OsmRoadBuilderTest {
  /**
   * The road builder used for testing.
   */
  private OsmRoadBuilder<RoadGraph<RoadNode, RoadEdge<RoadNode>>> mBuilder;
  /**
   * Counter used for generating unique edge IDs.
   */
  private long mEdgeIdCounter;
  /**
   * The graph used for testing.
   */
  private RoadGraph<RoadNode, RoadEdge<RoadNode>> mGraph;

  /**
   * Setups a road builder instance for testing.
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

    mBuilder = new OsmRoadBuilder<>(mGraph);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.osm.OsmRoadBuilder#buildEdge(de.topobyte.osm4j.core.model.iface.OsmWay, long, long)}.
   */
  @Test
  public void testBuildEdge() {
    final TLongArrayList nodes = new TLongArrayList();
    nodes.add(1L);
    nodes.add(6L);
    nodes.add(3L);
    final OsmWay way = new Way(20L, nodes, Arrays.asList(new Tag("highway", "motorway"), new Tag("maxspeed", "100")));

    final RoadEdge<RoadNode> edge = mBuilder.buildEdge(way, 1L, 6L);
    Assert.assertEquals(1L, edge.getSource().getId());
    Assert.assertEquals(6L, edge.getDestination().getId());
    Assert.assertEquals(20L, edge.getId());
    Assert.assertTrue(edge.getCost() > 0.0);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.osm.OsmRoadBuilder#buildNode(de.topobyte.osm4j.core.model.iface.OsmNode)}.
   */
  @Test
  public void testBuildNode() {
    final RoadNode node = mBuilder.buildNode(new Node(20L, 20.0, 20.0));
    Assert.assertEquals(20L, node.getId());
    Assert.assertEquals(20.0, node.getLatitude(), 0.0001);
    Assert.assertEquals(20.0, node.getLongitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.osm.OsmRoadBuilder#complete()}.
   */
  @Test
  public void testComplete() {
    final TLongArrayList nodes = new TLongArrayList();
    nodes.add(1L);
    nodes.add(6L);
    nodes.add(3L);
    final OsmWay way = new Way(20L, nodes, Arrays.asList(new Tag("highway", "motorway"), new Tag("maxspeed", "100")));

    final RoadEdge<RoadNode> edge = mBuilder.buildEdge(way, 1L, 6L);
    mGraph.addEdge(edge);
    final double costBefore = edge.getCost();

    final RoadNode node = mGraph.getNodeById(6L).get();
    node.setLatitude(100.0);
    node.setLongitude(100.0);

    mBuilder.complete();
    Assert.assertTrue(edge.getCost() > costBefore);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.osm.OsmRoadBuilder#OsmRoadBuilder(de.tischner.cobweb.routing.model.graph.IGraph)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testOsmRoadBuilder() {
    try {
      new OsmRoadBuilder<>(new RoadGraph<>());
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
