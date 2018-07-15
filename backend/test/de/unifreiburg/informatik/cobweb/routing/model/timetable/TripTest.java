package de.unifreiburg.informatik.cobweb.routing.model.timetable;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link Trip}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TripTest {
  /**
   * The trip used for testing.
   */
  private Trip mTrip;

  /**
   * Setups a trip instance for testing.
   */
  @Before
  public void setUp() {
    mTrip = new Trip(10);
    mTrip.addConnectionToSequence(new Connection(10, 0, 10, 11, 100, 120));
    mTrip.addConnectionToSequence(new Connection(10, 1, 11, 12, 120, 140));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Trip#addConnectionToSequence(de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection)}.
   */
  @Test
  public void testAddConnectionToSequence() {
    final Connection toAdd = new Connection(10, 2, 12, 13, 140, 160);
    mTrip.addConnectionToSequence(toAdd);
    final List<Connection> sequence = mTrip.getSequence();
    Assert.assertEquals(3, sequence.size());

    final Connection added = sequence.get(2);
    Assert.assertEquals(toAdd, added);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Trip#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mTrip, mTrip);
    Assert.assertEquals(mTrip, new Trip(10));

    Assert.assertNotEquals(mTrip, new Trip(9));
    Assert.assertNotEquals(mTrip, new Trip(0));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Trip#getConnectionAtSequenceIndex(int)}.
   */
  @Test
  public void testGetConnectionAtSequenceIndex() {
    Assert.assertEquals(0, mTrip.getConnectionAtSequenceIndex(0).getSequenceIndex());
    Assert.assertEquals(1, mTrip.getConnectionAtSequenceIndex(1).getSequenceIndex());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Trip#getId()}.
   */
  @Test
  public void testGetId() {
    Assert.assertEquals(10, mTrip.getId());
    Assert.assertEquals(0, new Trip(0).getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Trip#getSequence()}.
   */
  @Test
  public void testGetSequence() {
    final Connection toAdd = new Connection(10, 2, 12, 13, 140, 160);
    mTrip.addConnectionToSequence(toAdd);
    final List<Connection> sequence = mTrip.getSequence();
    Assert.assertEquals(3, sequence.size());

    Assert.assertEquals(0, sequence.get(0).getSequenceIndex());
    Assert.assertEquals(1, sequence.get(1).getSequenceIndex());
    Assert.assertEquals(2, sequence.get(2).getSequenceIndex());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Trip#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mTrip.hashCode(), mTrip.hashCode());
    Assert.assertEquals(mTrip.hashCode(), new Trip(10).hashCode());

    Assert.assertNotEquals(mTrip.hashCode(), new Trip(9).hashCode());
    Assert.assertNotEquals(mTrip.hashCode(), new Trip(0).hashCode());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Trip#Trip(int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTrip() {
    try {
      new Trip(10);
      new Trip(9);
      new Trip(0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
