package de.tischner.cobweb.parsing.osm;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link OsmParseUtil}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class OsmParseUtilTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.parsing.osm.OsmParseUtil#parseHighwayType(java.util.Map)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public final void testParseHighwayType() {
    Assert.assertEquals(EHighwayType.MOTORWAY,
        OsmParseUtil.parseHighwayType(Collections.singletonMap("highway", "motorway")));
    Assert.assertNull(OsmParseUtil.parseHighwayType(Collections.singletonMap("highway", "foobar")));
    Assert.assertNull(OsmParseUtil.parseHighwayType(Collections.singletonMap("name", "motorway")));
    Assert.assertNull(OsmParseUtil.parseHighwayType(Collections.emptyMap()));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.parsing.osm.OsmParseUtil#parseMaxSpeed(java.util.Map)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public final void testParseMaxSpeed() {
    Assert.assertEquals(50, OsmParseUtil.parseMaxSpeed(Collections.singletonMap("maxspeed", "50")));
    Assert.assertEquals(-1, OsmParseUtil.parseMaxSpeed(Collections.singletonMap("highway", "foobar")));
    Assert.assertEquals(-1, OsmParseUtil.parseMaxSpeed(Collections.singletonMap("name", "motorway")));
    Assert.assertEquals(-1, OsmParseUtil.parseMaxSpeed(Collections.emptyMap()));
    Assert.assertNotEquals(-1, OsmParseUtil.parseMaxSpeed(Collections.singletonMap("maxspeed", "walk")));
    Assert.assertNotEquals(-1, OsmParseUtil.parseMaxSpeed(Collections.singletonMap("maxspeed", "none")));
    Assert.assertNotEquals(-1, OsmParseUtil.parseMaxSpeed(Collections.singletonMap("maxspeed", "signals")));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.parsing.osm.OsmParseUtil#parseWayDirection(java.util.Map)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public final void testParseWayDirection() {
    Assert.assertEquals(0, OsmParseUtil.parseWayDirection(Collections.singletonMap("oneway", "no")));
    Assert.assertEquals(0, OsmParseUtil.parseWayDirection(Collections.singletonMap("oneway", "false")));
    Assert.assertEquals(0, OsmParseUtil.parseWayDirection(Collections.singletonMap("oneway", "")));
    Assert.assertEquals(0, OsmParseUtil.parseWayDirection(Collections.singletonMap("oneway", "0")));
    Assert.assertEquals(0, OsmParseUtil.parseWayDirection(Collections.singletonMap("maxspeed", "no")));
    Assert.assertEquals(0, OsmParseUtil.parseWayDirection(Collections.emptyMap()));

    Assert.assertTrue(OsmParseUtil.parseWayDirection(Collections.singletonMap("oneway", "yes")) > 0);
    Assert.assertTrue(OsmParseUtil.parseWayDirection(Collections.singletonMap("oneway", "true")) > 0);
    Assert.assertTrue(OsmParseUtil.parseWayDirection(Collections.singletonMap("oneway", "1")) > 0);

    Assert.assertTrue(OsmParseUtil.parseWayDirection(Collections.singletonMap("oneway", "-1")) < 0);
    Assert.assertTrue(OsmParseUtil.parseWayDirection(Collections.singletonMap("oneway", "reverse")) < 0);
  }

}
