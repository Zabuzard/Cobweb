package de.tischner.cobweb.routing.model.graph;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link SpeedTransportationModeComparator}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class SpeedTransportationModeComparatorTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.SpeedTransportationModeComparator#compare(de.tischner.cobweb.routing.model.graph.ETransportationMode, de.tischner.cobweb.routing.model.graph.ETransportationMode)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testCompare() {
    final SpeedTransportationModeComparator comp = new SpeedTransportationModeComparator();

    Assert.assertEquals(0, comp.compare(ETransportationMode.CAR, ETransportationMode.CAR));
    Assert.assertTrue(comp.compare(ETransportationMode.CAR, ETransportationMode.TRAM) > 0);
    Assert.assertTrue(comp.compare(ETransportationMode.CAR, ETransportationMode.BIKE) > 0);
    Assert.assertTrue(comp.compare(ETransportationMode.CAR, ETransportationMode.FOOT) > 0);

    Assert.assertTrue(comp.compare(ETransportationMode.TRAM, ETransportationMode.CAR) < 0);
    Assert.assertEquals(0, comp.compare(ETransportationMode.TRAM, ETransportationMode.TRAM));
    Assert.assertTrue(comp.compare(ETransportationMode.TRAM, ETransportationMode.BIKE) > 0);
    Assert.assertTrue(comp.compare(ETransportationMode.TRAM, ETransportationMode.FOOT) > 0);

    Assert.assertTrue(comp.compare(ETransportationMode.BIKE, ETransportationMode.CAR) < 0);
    Assert.assertTrue(comp.compare(ETransportationMode.BIKE, ETransportationMode.TRAM) < 0);
    Assert.assertEquals(0, comp.compare(ETransportationMode.BIKE, ETransportationMode.BIKE));
    Assert.assertTrue(comp.compare(ETransportationMode.BIKE, ETransportationMode.FOOT) > 0);

    Assert.assertTrue(comp.compare(ETransportationMode.FOOT, ETransportationMode.CAR) < 0);
    Assert.assertTrue(comp.compare(ETransportationMode.FOOT, ETransportationMode.TRAM) < 0);
    Assert.assertTrue(comp.compare(ETransportationMode.FOOT, ETransportationMode.BIKE) < 0);
    Assert.assertEquals(0, comp.compare(ETransportationMode.FOOT, ETransportationMode.FOOT));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.model.graph.SpeedTransportationModeComparator#SpeedTransportationModeComparator()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testSpeedTransportationModeComparator() {
    try {
      new SpeedTransportationModeComparator();
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
