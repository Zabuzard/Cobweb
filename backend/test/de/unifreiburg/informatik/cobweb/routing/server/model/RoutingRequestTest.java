package de.unifreiburg.informatik.cobweb.routing.server.model;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unifreiburg.informatik.cobweb.routing.model.graph.ETransportationMode;

/**
 * Test for the class {@link RoutingRequest}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RoutingRequestTest {
  /**
   * The routing request used for testing.
   */
  private RoutingRequest mRequest;

  /**
   * Setups a routing request instance for testing.
   */
  @Before
  public void setUp() {
    mRequest = new RoutingRequest(5L, 10L, 100L,
        Stream.of(ETransportationMode.BIKE, ETransportationMode.CAR).collect(Collectors.toSet()));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.server.model.RoutingRequest#getDepTime()}.
   */
  @Test
  public void testGetDepTime() {
    Assert.assertEquals(100L, mRequest.getDepTime());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.server.model.RoutingRequest#getFrom()}.
   */
  @Test
  public void testGetFrom() {
    Assert.assertEquals(5, mRequest.getFrom());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.server.model.RoutingRequest#getModes()}.
   */
  @Test
  public void testGetModes() {
    final Set<ETransportationMode> modes = mRequest.getModes();
    Assert.assertEquals(2, modes.size());
    Assert.assertTrue(modes.contains(ETransportationMode.CAR));
    Assert.assertTrue(modes.contains(ETransportationMode.BIKE));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.server.model.RoutingRequest#getTo()}.
   */
  @Test
  public void testGetTo() {
    Assert.assertEquals(10L, mRequest.getTo());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.server.model.RoutingRequest#RoutingRequest(long, long, long, java.util.Set)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testRoutingRequest() {
    try {
      new RoutingRequest(5L, 10L, 100L,
          Stream.of(ETransportationMode.BIKE, ETransportationMode.CAR).collect(Collectors.toSet()));
      new RoutingRequest(-10L, -10L, 0L, Collections.singleton(ETransportationMode.CAR));
      new RoutingRequest(0L, -5L, 100L, Collections.singleton(ETransportationMode.CAR));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
