package de.tischner.cobweb.routing.parsing.gtfs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.transit.ITransitIdGenerator;
import de.tischner.cobweb.routing.model.graph.transit.TransitGraph;
import de.tischner.cobweb.routing.model.graph.transit.TransitNode;

/**
 * Test for the class {@link GtfsConnectionBuilder}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class GtfsConnectionBuilderTest {
  /**
   * The builder used for testing.
   */
  private GtfsConnectionBuilder mBuilder;

  /**
   * Setups a builder instance for testing.
   */
  @Before
  public void setUp() {
    final ITransitIdGenerator generator = new TransitGraph<>();
    mBuilder = new GtfsConnectionBuilder(generator);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.gtfs.GtfsConnectionBuilder#buildEdge(de.tischner.cobweb.routing.model.graph.ICoreNode, de.tischner.cobweb.routing.model.graph.ICoreNode, double)}.
   */
  @Test
  public void testBuildEdge() {
    final TransitNode first = new TransitNode(1, 1.0F, 1.0F, 1);
    final TransitNode second = new TransitNode(2, 2.0F, 2.0F, 2);
    final ICoreEdge<ICoreNode> edge = mBuilder.buildEdge(first, second, 1.0);
    Assert.assertEquals(1, edge.getSource().getId());
    Assert.assertEquals(2, edge.getDestination().getId());
    Assert.assertEquals(1.0, edge.getCost(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.gtfs.GtfsConnectionBuilder#buildNode(float, float, int)}.
   */
  @Test
  public void testBuildNode() {
    final ICoreNode node = mBuilder.buildNode(1.0F, 1.0F, 1);
    Assert.assertEquals(1.0F, node.getLatitude(), 0.0001);
    Assert.assertEquals(1.0F, node.getLongitude(), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.parsing.gtfs.GtfsConnectionBuilder#GtfsConnectionBuilder(de.tischner.cobweb.routing.model.graph.transit.ITransitIdGenerator)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testGtfsConnectionBuilder() {
    try {
      new GtfsConnectionBuilder(new TransitGraph<>());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
