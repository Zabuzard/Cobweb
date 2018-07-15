package de.unifreiburg.informatik.cobweb.routing.model.timetable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link Connection}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ConnectionTest {
  /**
   * The connection used for testing.
   */
  private Connection mConnection;

  /**
   * Setups a connection instance for testing.
   */
  @Before
  public void setUp() {
    mConnection = new Connection(2, 5, 10, 12, 120, 150);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection#compareTo(de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection)}.
   */
  @Test
  public void testCompareTo() {
    Assert.assertTrue(mConnection.compareTo(mConnection) == 0);
    Assert.assertTrue(mConnection.compareTo(new Connection(2, 5, 10, 12, 120, 150)) == 0);

    // Departure time
    Assert.assertTrue(mConnection.compareTo(new Connection(2, 5, 10, 12, 121, 150)) < 0);
    Assert.assertTrue(mConnection.compareTo(new Connection(2, 5, 10, 12, 119, 150)) > 0);

    // Trip ID
    Assert.assertTrue(mConnection.compareTo(new Connection(3, 5, 10, 12, 120, 150)) < 0);
    Assert.assertTrue(mConnection.compareTo(new Connection(1, 5, 10, 12, 120, 150)) > 0);

    // Sequence index
    Assert.assertTrue(mConnection.compareTo(new Connection(2, 6, 10, 12, 120, 150)) < 0);
    Assert.assertTrue(mConnection.compareTo(new Connection(2, 4, 10, 12, 120, 150)) > 0);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection#Connection(int, int, int, int, int, int)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testConnection() {
    try {
      new Connection(2, 5, 10, 12, 120, 150);
      new Connection(0, 0, 0, 0, 0, 0);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Assert.assertEquals(mConnection, mConnection);
    Assert.assertEquals(mConnection, new Connection(2, 5, 10, 12, 120, 150));

    Assert.assertNotEquals(mConnection, new Connection(3, 5, 10, 12, 120, 150));
    Assert.assertNotEquals(mConnection, new Connection(2, 6, 10, 12, 120, 150));
    Assert.assertNotEquals(mConnection, new Connection(2, 5, 11, 12, 120, 150));
    Assert.assertNotEquals(mConnection, new Connection(2, 5, 10, 13, 120, 150));
    Assert.assertNotEquals(mConnection, new Connection(2, 5, 10, 12, 121, 150));
    Assert.assertNotEquals(mConnection, new Connection(2, 5, 10, 12, 120, 151));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection#getArrStopId()}.
   */
  @Test
  public void testGetArrStopId() {
    Assert.assertEquals(12, mConnection.getArrStopId());
    Assert.assertEquals(0, new Connection(2, 5, 10, 0, 120, 150).getArrStopId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection#getArrTime()}.
   */
  @Test
  public void testGetArrTime() {
    Assert.assertEquals(150, mConnection.getArrTime());
    Assert.assertEquals(0, new Connection(2, 5, 10, 0, 120, 0).getArrTime());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection#getDepStopId()}.
   */
  @Test
  public void testGetDepStopId() {
    Assert.assertEquals(10, mConnection.getDepStopId());
    Assert.assertEquals(0, new Connection(2, 5, 0, 0, 120, 0).getDepStopId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection#getDepTime()}.
   */
  @Test
  public void testGetDepTime() {
    Assert.assertEquals(120, mConnection.getDepTime());
    Assert.assertEquals(0, new Connection(2, 5, 0, 0, 0, 0).getDepTime());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection#getSequenceIndex()}.
   */
  @Test
  public void testGetSequenceIndex() {
    Assert.assertEquals(5, mConnection.getSequenceIndex());
    Assert.assertEquals(0, new Connection(2, 0, 0, 0, 0, 0).getSequenceIndex());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection#getTripId()}.
   */
  @Test
  public void testGetTripId() {
    Assert.assertEquals(2, mConnection.getTripId());
    Assert.assertEquals(0, new Connection(0, 0, 0, 0, 0, 0).getTripId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Connection#hashCode()}.
   */
  @Test
  public void testHashCode() {
    Assert.assertEquals(mConnection.hashCode(), mConnection.hashCode());
    Assert.assertEquals(mConnection.hashCode(), new Connection(2, 5, 10, 12, 120, 150).hashCode());

    Assert.assertNotEquals(mConnection.hashCode(), new Connection(3, 5, 10, 12, 120, 150).hashCode());
    Assert.assertNotEquals(mConnection.hashCode(), new Connection(2, 6, 10, 12, 120, 150).hashCode());
    Assert.assertNotEquals(mConnection.hashCode(), new Connection(2, 5, 11, 12, 120, 150).hashCode());
    Assert.assertNotEquals(mConnection.hashCode(), new Connection(2, 5, 10, 13, 120, 150).hashCode());
    Assert.assertNotEquals(mConnection.hashCode(), new Connection(2, 5, 10, 12, 121, 150).hashCode());
    Assert.assertNotEquals(mConnection.hashCode(), new Connection(2, 5, 10, 12, 120, 151).hashCode());
  }

}
