package de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.OptionalDouble;

import org.junit.Assert;
import org.junit.Test;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.link.LinkEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.tischner.cobweb.routing.model.graph.transit.TransitEdge;
import de.tischner.cobweb.routing.model.graph.transit.TransitNode;

/**
 * Test for the class {@link TransitModule}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TransitModuleTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.TransitModule#of(long)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testOf() {
    try {
      TransitModule.of(10L);
      TransitModule.of(0L);
      TransitModule.of(504L);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.TransitModule#provideEdgeCost(de.tischner.cobweb.routing.model.graph.IEdge, double)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testProvideEdgeCost() {
    // Midnight 2018/06/17
    final LocalDateTime dateTime = LocalDate.of(2018, 6, 17).atTime(LocalTime.MIDNIGHT);
    final TransitModule<ICoreNode, ICoreEdge<ICoreNode>> module =
        TransitModule.of(dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

    ICoreEdge<ICoreNode> edge = new RoadEdge<>(1, new RoadNode(1, 1.0F, 1.0F), new RoadNode(2, 2.0F, 2.0F),
        EHighwayType.PRIMARY, 100, Collections.singleton(ETransportationMode.CAR));
    Assert.assertFalse(module.provideEdgeCost(edge, 10.0).isPresent());

    edge = new TransitEdge<>(1, new TransitNode(1, 1.0F, 1.0F, 1), new TransitNode(2, 2.0F, 2.0F, 2), 5.0);
    Assert.assertFalse(module.provideEdgeCost(edge, 10.0).isPresent());

    edge = new LinkEdge<>(new TransitNode(1, 1.0F, 1.0F, 1), new RoadNode(2, 2.0F, 2.0F));
    Assert.assertFalse(module.provideEdgeCost(edge, 10.0).isPresent());

    // Edge available 100 seconds after midnight
    edge = new LinkEdge<>(new RoadNode(1, 1.0F, 1.0F), new TransitNode(2, 2.0F, 2.0F, 100));
    // Already traveling for 50 seconds
    OptionalDouble result = module.provideEdgeCost(edge, 50.0);
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals(50.0, result.getAsDouble(), 0.0001);

    // Edge available 100 seconds after midnight
    final int secondsOfDay = 24 * 60 * 60;
    edge = new LinkEdge<>(new RoadNode(1, 1.0F, 1.0F), new TransitNode(2, 2.0F, 2.0F, secondsOfDay + 100));
    // Already traveling for 20 seconds
    result = module.provideEdgeCost(edge, 20.0);
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals(80.0, result.getAsDouble(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.shortestpath.dijkstra.modules.TransitModule#TransitModule(long)}.
   */
  @SuppressWarnings({ "static-method", "unused" })
  @Test
  public void testTransitModule() {
    try {
      new TransitModule<>(10L);
      new TransitModule<>(0L);
      new TransitModule<>(504L);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
