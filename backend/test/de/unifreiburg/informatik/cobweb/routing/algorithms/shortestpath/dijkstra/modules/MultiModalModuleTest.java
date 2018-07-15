package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import java.util.Collections;
import java.util.EnumSet;
import java.util.OptionalDouble;

import org.junit.Assert;
import org.junit.Test;

import de.unifreiburg.informatik.cobweb.parsing.osm.EHighwayType;
import de.unifreiburg.informatik.cobweb.routing.model.graph.BasicEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.BasicNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ETransportationMode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.road.RoadEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.road.RoadNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitNode;

/**
 * Test for the class {@link MultiModalModule}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class MultiModalModuleTest {

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules.MultiModalModule#considerEdgeForRelaxation(de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge, de.unifreiburg.informatik.cobweb.routing.model.graph.INode)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testConsiderEdgeForRelaxation() {
    final MultiModalModule<INode, IEdge<INode>> firstModule = MultiModalModule.of(EnumSet.of(ETransportationMode.CAR));
    final IEdge<INode> firstEdge = new BasicEdge<>(1, new BasicNode(1), new BasicNode(2), 1);
    Assert.assertTrue(firstModule.considerEdgeForRelaxation(firstEdge, null));

    MultiModalModule<ICoreNode, ICoreEdge<ICoreNode>> secondModule =
        MultiModalModule.of(EnumSet.of(ETransportationMode.CAR));
    ICoreEdge<ICoreNode> secondEdge =
        new TransitEdge<>(1, new TransitNode(1, 1.0F, 1.0F, 1), new TransitNode(2, 2.0F, 2.0F, 2), 10.0);
    Assert.assertFalse(secondModule.considerEdgeForRelaxation(secondEdge, null));
    secondModule = MultiModalModule.of(EnumSet.of(ETransportationMode.TRAM, ETransportationMode.CAR));
    Assert.assertTrue(secondModule.considerEdgeForRelaxation(secondEdge, null));

    secondEdge = new RoadEdge<>(1, new RoadNode(1, 1.0F, 1.0F), new RoadNode(2, 2.0F, 2.0F), EHighwayType.PRIMARY, 100,
        EnumSet.of(ETransportationMode.CAR, ETransportationMode.FOOT, ETransportationMode.BIKE));
    Assert.assertTrue(secondModule.considerEdgeForRelaxation(secondEdge, null));
    secondModule = MultiModalModule.of(EnumSet.of(ETransportationMode.FOOT, ETransportationMode.BIKE));
    Assert.assertTrue(secondModule.considerEdgeForRelaxation(secondEdge, null));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules.MultiModalModule#MultiModalModule(java.util.Set)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testMultiModalModule() {
    try {
      new MultiModalModule<>(Collections.emptySet());
      new MultiModalModule<>(EnumSet.of(ETransportationMode.CAR));
      new MultiModalModule<>(EnumSet.of(ETransportationMode.CAR, ETransportationMode.TRAM, ETransportationMode.FOOT));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules.MultiModalModule#of(java.util.Set)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testOf() {
    try {
      MultiModalModule.of(Collections.emptySet());
      MultiModalModule.of(EnumSet.of(ETransportationMode.CAR));
      MultiModalModule.of(EnumSet.of(ETransportationMode.CAR, ETransportationMode.TRAM, ETransportationMode.FOOT));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.modules.MultiModalModule#provideEdgeCost(de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge, double)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testProvideEdgeCost() {
    final MultiModalModule<INode, IEdge<INode>> firstModule = MultiModalModule.of(EnumSet.of(ETransportationMode.CAR));
    final IEdge<INode> firstEdge = new BasicEdge<>(1, new BasicNode(1), new BasicNode(2), 1);
    Assert.assertFalse(firstModule.provideEdgeCost(firstEdge, 0.0).isPresent());

    MultiModalModule<ICoreNode, ICoreEdge<ICoreNode>> secondModule =
        MultiModalModule.of(EnumSet.of(ETransportationMode.CAR, ETransportationMode.TRAM));
    ICoreEdge<ICoreNode> secondEdge =
        new TransitEdge<>(1, new TransitNode(1, 1.0F, 1.0F, 1), new TransitNode(2, 2.0F, 2.0F, 2), 10.0);
    Assert.assertFalse(secondModule.provideEdgeCost(secondEdge, 0.0).isPresent());

    secondEdge = new RoadEdge<>(1, new RoadNode(1, 1.0F, 1.0F), new RoadNode(2, 2.0F, 2.0F), EHighwayType.PRIMARY, 100,
        EnumSet.of(ETransportationMode.CAR, ETransportationMode.FOOT, ETransportationMode.BIKE));
    Assert.assertFalse(secondModule.provideEdgeCost(secondEdge, 0.0).isPresent());

    secondModule = MultiModalModule.of(EnumSet.of(ETransportationMode.BIKE, ETransportationMode.TRAM));
    final OptionalDouble result = secondModule.provideEdgeCost(secondEdge, 0.0);
    Assert.assertTrue(result.isPresent());
    // Travel time with bike is greater than with car
    Assert.assertTrue(result.getAsDouble() > secondEdge.getCost());
  }

}
