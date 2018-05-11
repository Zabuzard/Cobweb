package de.tischner.cobweb.routing.server.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link RoutingResponse}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RoutingResponseTest {
  /**
   * The response used for testing.
   */
  private RoutingResponse mResponse;

  /**
   * Setups a response instance for testing.
   */
  @Before
  public void setUp() {
    final RouteElement first = new RouteElement(ERouteElementType.NODE, "", Arrays.asList(new double[] { 1.0, 1.0 }));
    final RouteElement second = new RouteElement(ERouteElementType.PATH, ETransportationMode.BIKE, "Main street",
        Arrays.asList(new double[] { 1.0, 1.0 }, new double[] { 2.0, 2.0 }, new double[] { 4.0, 4.0 }));
    final RouteElement third =
        new RouteElement(ERouteElementType.NODE, "Wall street 5", Arrays.asList(new double[] { 4.0, 4.0 }));
    final Journey journey = new Journey(100L, 200L, Arrays.asList(first, second, third));
    mResponse = new RoutingResponse(5L, 10L, Arrays.asList(journey, journey));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.server.model.RoutingResponse#getFrom()}.
   */
  @Test
  public void testGetFrom() {
    Assert.assertEquals(5L, mResponse.getFrom());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.server.model.RoutingResponse#getJourneys()}.
   */
  @Test
  public void testGetJourneys() {
    final List<Journey> journeys = mResponse.getJourneys();
    Assert.assertEquals(2, journeys.size());
    final Iterator<Journey> journeyIter = journeys.iterator();

    final Journey first = journeyIter.next();
    Assert.assertEquals(200L, first.getArrTime());
    final Journey second = journeyIter.next();
    Assert.assertEquals(200L, second.getArrTime());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.server.model.RoutingResponse#getTo()}.
   */
  @Test
  public void testGetTo() {
    Assert.assertEquals(10L, mResponse.getTo());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.server.model.RoutingResponse#RoutingResponse(long, long, java.util.List)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testRoutingResponse() {
    final RouteElement first = new RouteElement(ERouteElementType.NODE, "", Arrays.asList(new double[] { 1.0, 1.0 }));
    final RouteElement second = new RouteElement(ERouteElementType.PATH, ETransportationMode.BIKE, "Main street",
        Arrays.asList(new double[] { 1.0, 1.0 }, new double[] { 2.0, 2.0 }, new double[] { 4.0, 4.0 }));
    final RouteElement third =
        new RouteElement(ERouteElementType.NODE, "Wall street 5", Arrays.asList(new double[] { 4.0, 4.0 }));
    final Journey journey = new Journey(100L, 200L, Arrays.asList(first, second, third));
    try {
      new RoutingResponse(5L, 10L, Arrays.asList(journey, journey));
      new RoutingResponse(-5L, -10L, Collections.singletonList(journey));
      new RoutingResponse(0L, 0L, Collections.emptyList());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
