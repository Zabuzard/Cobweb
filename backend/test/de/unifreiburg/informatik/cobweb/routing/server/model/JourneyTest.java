package de.unifreiburg.informatik.cobweb.routing.server.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unifreiburg.informatik.cobweb.routing.model.graph.ETransportationMode;

/**
 * Test for the class {@link Journey}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class JourneyTest {
  /**
   * The journey used for testing.
   */
  private Journey mJourney;

  /**
   * Setups a journey instance for testing.
   */
  @Before
  public void setUp() {
    final RouteElement first = new RouteElement(ERouteElementType.NODE, "", Arrays.asList(new float[] { 1.0F, 1.0F }));
    final RouteElement second = new RouteElement(ERouteElementType.PATH, ETransportationMode.BIKE, "Main street",
        Arrays.asList(new float[] { 1.0F, 1.0F }, new float[] { 2.0F, 2.0F }, new float[] { 4.0F, 4.0F }));
    final RouteElement third =
        new RouteElement(ERouteElementType.NODE, "Wall street 5", Arrays.asList(new float[] { 4.0F, 4.0F }));
    mJourney = new Journey(100L, 200L, Arrays.asList(first, second, third));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.server.model.Journey#getArrTime()}.
   */
  @Test
  public void testGetArrTime() {
    Assert.assertEquals(200L, mJourney.getArrTime());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.server.model.Journey#getDepTime()}.
   */
  @Test
  public void testGetDepTime() {
    Assert.assertEquals(100L, mJourney.getDepTime());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.server.model.Journey#getRoute()}.
   */
  @Test
  public void testGetRoute() {
    final List<RouteElement> route = mJourney.getRoute();
    Assert.assertEquals(3, route.size());
    final Iterator<RouteElement> routeIter = route.iterator();

    final RouteElement first = routeIter.next();
    Assert.assertEquals(ERouteElementType.NODE, first.getType());
    final RouteElement second = routeIter.next();
    Assert.assertEquals(ERouteElementType.PATH, second.getType());
    final RouteElement third = routeIter.next();
    Assert.assertEquals(ERouteElementType.NODE, third.getType());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.server.model.Journey#Journey(long, long, java.util.List)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testJourney() {
    final RouteElement first = new RouteElement(ERouteElementType.NODE, "", Arrays.asList(new float[] { 1.0F, 1.0F }));
    final RouteElement second = new RouteElement(ERouteElementType.PATH, "Main street",
        Arrays.asList(new float[] { 1.0F, 1.0F }, new float[] { 2.0F, 2.0F }, new float[] { 4.0F, 4.0F }));
    final RouteElement third =
        new RouteElement(ERouteElementType.NODE, "Wall street 5", Arrays.asList(new float[] { 4.0F, 4.0F }));
    try {
      new Journey(100L, 200L, Arrays.asList(first, second, third));
      new Journey(0, 0, Collections.emptyList());
      new Journey(100L, 200L, Arrays.asList(first, second));
      new Journey(100L, 200L, Arrays.asList(first, third));
      new Journey(100L, 200L, Arrays.asList(first));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
