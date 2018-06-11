package de.tischner.cobweb.util;

import org.junit.Assert;
import org.junit.Test;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;

/**
 * Test for the class {@link RoutingUtil}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RoutingUtilTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#degToRad(double)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testDegToRad() {
    Assert.assertEquals(0.0, RoutingUtil.degToRad(0.0), 0.0001);
    Assert.assertEquals(2 * Math.PI, RoutingUtil.degToRad(360.0), 0.0001);
    Assert.assertEquals(Math.PI, RoutingUtil.degToRad(180.0), 0.0001);
    Assert.assertEquals(0.5 * Math.PI, RoutingUtil.degToRad(90.0), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#distanceEquiRect(de.tischner.cobweb.routing.model.graph.ISpatial, de.tischner.cobweb.routing.model.graph.ISpatial)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testDistanceEquiRect() {
    RoadNode first = new RoadNode(1, 50.046725F, -5.423972F);
    RoadNode second = new RoadNode(2, 58.574237F, -3.156773F);
    Assert.assertEquals(0.0, RoutingUtil.distanceEquiRect(first, first), 0.0001);
    Assert.assertEquals(0.0, RoutingUtil.distanceEquiRect(second, second), 0.0001);
    Assert.assertEquals(960_000, RoutingUtil.distanceEquiRect(first, second), 1_000.0);
    Assert.assertEquals(960_000, RoutingUtil.distanceEquiRect(second, first), 1_000.0);
    Assert.assertEquals(RoutingUtil.distanceEquiRect(first, second), RoutingUtil.distanceEquiRect(second, first),
        0.0001);

    first = new RoadNode(1, 47.996452F, 7.841485F);
    second = new RoadNode(2, 47.996331F, 7.841392F);
    Assert.assertEquals(15, RoutingUtil.distanceEquiRect(first, second), 0.3);
    Assert.assertEquals(RoutingUtil.distanceEquiRect(first, second), RoutingUtil.distanceEquiRect(second, first),
        0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#getSpeedOfHighway(de.tischner.cobweb.parsing.osm.EHighwayType, int)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetSpeedOfHighway() {
    Assert.assertEquals(100.0, RoutingUtil.getSpeedOfHighway(EHighwayType.MOTORWAY, 100), 0.0001);
    Assert.assertTrue(RoutingUtil.getSpeedOfHighway(EHighwayType.MOTORWAY, -1) > 0);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#kmhToMs(double)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testKmhToMs() {
    Assert.assertEquals(10, RoutingUtil.kmhToMs(36), 0.0001);
    Assert.assertEquals(-10, RoutingUtil.kmhToMs(-36), 0.0001);
    Assert.assertEquals(0, RoutingUtil.kmhToMs(0), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#maximalRoadSpeed()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testMaximalRoadSpeed() {
    Assert.assertTrue(RoutingUtil.maximalRoadSpeed() > 0);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#millisToSeconds(long)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testMillisToSeconds() {
    Assert.assertEquals(1.0, RoutingUtil.millisToSeconds(1_000L), 0.0001);
    Assert.assertEquals(1.5, RoutingUtil.millisToSeconds(1_500L), 0.0001);
    Assert.assertEquals(0.0, RoutingUtil.millisToSeconds(0L), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#msToKmh(double)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testMsToKmh() {
    Assert.assertEquals(36, RoutingUtil.msToKmh(10), 0.0001);
    Assert.assertEquals(-36, RoutingUtil.msToKmh(-10), 0.0001);
    Assert.assertEquals(0, RoutingUtil.msToKmh(0), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#nanosToMillis(long)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNanosToMillis() {
    Assert.assertEquals(10L, RoutingUtil.nanosToMillis(10_000_000L));
    Assert.assertEquals(1L, RoutingUtil.nanosToMillis(1_500_000L));
    Assert.assertEquals(1L, RoutingUtil.nanosToMillis(1_000_000L));
    Assert.assertEquals(0L, RoutingUtil.nanosToMillis(0L));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#nanosToSeconds(long)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNanosToSeconds() {
    Assert.assertEquals(10.0, RoutingUtil.nanosToSeconds(10_000_000_000L), 0.0001);
    Assert.assertEquals(1.5, RoutingUtil.nanosToSeconds(1_500_000_000L), 0.0001);
    Assert.assertEquals(1.0, RoutingUtil.nanosToSeconds(1_000_000_000L), 0.0001);
    Assert.assertEquals(0.0, RoutingUtil.nanosToSeconds(0L), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#radToDeg(double)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testRadToDeg() {
    Assert.assertEquals(0.0, RoutingUtil.radToDeg(0.0), 0.0001);
    Assert.assertEquals(360, RoutingUtil.radToDeg(2 * Math.PI), 0.0001);
    Assert.assertEquals(180.0, RoutingUtil.radToDeg(Math.PI), 0.0001);
    Assert.assertEquals(90.0, RoutingUtil.radToDeg(0.5 * Math.PI), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#secondsToMillis(double)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testSecondsToMillis() {
    Assert.assertEquals(1000.0, RoutingUtil.secondsToMillis(1.0), 0.0001);
    Assert.assertEquals(0.0, RoutingUtil.secondsToMillis(0.0), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#secondsToNanos(double)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testSecondsToNanos() {
    Assert.assertEquals(1_000_000_000, RoutingUtil.secondsToNanos(1.0), 0.0001);
    Assert.assertEquals(0, RoutingUtil.secondsToNanos(0.0), 0.0001);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.RoutingUtil#travelTime(double, double)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testTravelTime() {
    Assert.assertEquals(36.0, RoutingUtil.travelTime(1_000.0, 100), 0.0001);
    Assert.assertEquals(0.0, RoutingUtil.travelTime(0.0, 100), 0.0001);
    Assert.assertEquals(1.0, RoutingUtil.travelTime(100.0, 360), 0.0001);
  }

}
