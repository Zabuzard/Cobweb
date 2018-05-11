package de.tischner.cobweb.routing.server.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        Arrays.asList(new double[] { 1.0, 1.0 }, new double[] { 2.0, 2.0 }, new double[] { 4.0, 4.0 }));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.server.model.RouteElement#getGeom()}.
   */
  @Test
  public void testGetGeom() {
    final List<double[]> geom = mElement.getGeom();
    Assert.assertEquals(3, geom.size());
    final Iterator<double[]> geomIter = geom.iterator();

    final double[] first = geomIter.next();
    Assert.assertArrayEquals(new double[] { 1.0, 1.0 }, first, 0.0001);
    final double[] second = geomIter.next();
    Assert.assertArrayEquals(new double[] { 2.0, 2.0 }, second, 0.0001);
    final double[] third = geomIter.next();
    Assert.assertArrayEquals(new double[] { 4.0, 4.0 }, third, 0.0001);
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
        new RouteElement(ERouteElementType.NODE, "", Arrays.asList(new double[] { 0.0, 0.0 })).getName());
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
   * {@link de.tischner.cobweb.routing.server.model.RouteElement#RouteElement(de.tischner.cobweb.routing.server.model.ERouteElementType, de.tischner.cobweb.routing.server.model.ETransportationMode, java.lang.String, java.util.List)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testRouteElementERouteElementTypeETransportationModeStringListOfdouble() {
    try {
      new RouteElement(ERouteElementType.PATH, ETransportationMode.BIKE, "Main street",
          Arrays.asList(new double[] { 1.0, 1.0 }, new double[] { 2.0, 2.0 }, new double[] { 4.0, 4.0 }));
      new RouteElement(ERouteElementType.PATH, ETransportationMode.CAR, "",
          Arrays.asList(new double[] { -1.0, -1.0 }, new double[] { 4.0, 4.0 }, new double[] { 0.0, 0.0 }));
      new RouteElement(ERouteElementType.NODE, ETransportationMode.BIKE, "Main street 5",
          Arrays.asList(new double[] { 1.0, 1.0 }));
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
      new RouteElement(ERouteElementType.NODE, "Main street 5", Arrays.asList(new double[] { 1.0, 1.0 }));
      new RouteElement(ERouteElementType.NODE, "", Arrays.asList(new double[] { 0.0, 0.0 }));
      new RouteElement(ERouteElementType.NODE, "", Arrays.asList(new double[] { -5.0, -5.0 }));
      new RouteElement(ERouteElementType.PATH, "Main street",
          Arrays.asList(new double[] { 1.0, 1.0 }, new double[] { 2.0, 2.0 }, new double[] { 4.0, 4.0 }));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
