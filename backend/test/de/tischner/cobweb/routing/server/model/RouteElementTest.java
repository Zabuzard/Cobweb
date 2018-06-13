package de.tischner.cobweb.routing.server.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.ETransportationMode;

/**
 * Test for the class {@link RouteElement}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RouteElementTest {
  /**
   * The route element used for testing.
   */
  private RouteElement mElement;

  /**
   * Setups a route element for testing.
   */
  @Before
  public void setUp() {
    mElement = new RouteElement(ERouteElementType.PATH, ETransportationMode.BIKE, "Main street",
        Arrays.asList(new float[] { 1.0F, 1.0F }, new float[] { 2.0F, 2.0F }, new float[] { 4.0F, 4.0F }));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.server.model.RouteElement#getGeom()}.
   */
  @Test
  public void testGetGeom() {
    final List<float[]> geom = mElement.getGeom();
    Assert.assertEquals(3, geom.size());
    final Iterator<float[]> geomIter = geom.iterator();

    final float[] first = geomIter.next();
    Assert.assertArrayEquals(new float[] { 1.0F, 1.0F }, first, 0.0001F);
    final float[] second = geomIter.next();
    Assert.assertArrayEquals(new float[] { 2.0F, 2.0F }, second, 0.0001F);
    final float[] third = geomIter.next();
    Assert.assertArrayEquals(new float[] { 4.0F, 4.0F }, third, 0.0001F);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.server.model.RouteElement#getMode()}.
   */
  @Test
  public void testGetMode() {
    Assert.assertEquals(ETransportationMode.BIKE, mElement.getMode());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.server.model.RouteElement#getName()}.
   */
  @Test
  public void testGetName() {
    Assert.assertEquals("Main street", mElement.getName());
    Assert.assertEquals("",
        new RouteElement(ERouteElementType.NODE, "", Arrays.asList(new float[] { 0.0F, 0.0F })).getName());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.server.model.RouteElement#getType()}.
   */
  @Test
  public void testGetType() {
    Assert.assertEquals(ERouteElementType.PATH, mElement.getType());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.server.model.RouteElement#RouteElement(de.tischner.cobweb.routing.server.model.ERouteElementType, de.tischner.cobweb.routing.model.graph.ETransportationMode, java.lang.String, java.util.List)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testRouteElementERouteElementTypeETransportationModeStringListOfdouble() {
    try {
      new RouteElement(ERouteElementType.PATH, ETransportationMode.BIKE, "Main street",
          Arrays.asList(new float[] { 1.0F, 1.0F }, new float[] { 2.0F, 2.0F }, new float[] { 4.0F, 4.0F }));
      new RouteElement(ERouteElementType.PATH, ETransportationMode.CAR, "",
          Arrays.asList(new float[] { -1.0F, -1.0F }, new float[] { 4.0F, 4.0F }, new float[] { 0.0F, 0.0F }));
      new RouteElement(ERouteElementType.NODE, ETransportationMode.BIKE, "Main street 5",
          Arrays.asList(new float[] { 1.0F, 1.0F }));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.server.model.RouteElement#RouteElement(de.tischner.cobweb.routing.server.model.ERouteElementType, java.lang.String, java.util.List)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testRouteElementERouteElementTypeStringListOfdouble() {
    try {
      new RouteElement(ERouteElementType.NODE, "Main street 5", Arrays.asList(new float[] { 1.0F, 1.0F }));
      new RouteElement(ERouteElementType.NODE, "", Arrays.asList(new float[] { 0.0F, 0.0F }));
      new RouteElement(ERouteElementType.NODE, "", Arrays.asList(new float[] { -5.0F, -5.0F }));
      new RouteElement(ERouteElementType.PATH, "Main street",
          Arrays.asList(new float[] { 1.0F, 1.0F }, new float[] { 2.0F, 2.0F }, new float[] { 4.0F, 4.0F }));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
