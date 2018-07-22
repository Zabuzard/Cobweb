package de.unifreiburg.informatik.cobweb.routing.model.timetable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link Timetable}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TimetableTest {
  /**
   * The timetable used for testing.
   */
  private Timetable mTable;

  /**
   * Setups a timetable instance for testing.
   */
  @Before
  public void setUp() {
    mTable = new Timetable();
    mTable.addStop(new Stop(1, 1.1f, 2.2f));
    mTable.addStop(new Stop(2, 3.3f, 4.4f));
    mTable.addStop(new Stop(3, 5.5f, 6.6f));
    mTable.addTrip(new Trip(1));

    final Collection<Connection> connections = new ArrayList<>();
    connections.add(new Connection(1, 0, 1, 2, 100, 120));
    connections.add(new Connection(1, 1, 2, 3, 120, 140));
    mTable.addConnections(connections);
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable#addConnections(java.util.Collection)}.
   */
  @Test
  public void testAddConnections() {
    mTable.addConnections(Collections.singletonList(new Connection(1, 2, 3, 1, 140, 160)));
    final Iterator<Connection> connectionIter = mTable.getConnectionsStartingSince(0);

    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(0, connectionIter.next().getSequenceIndex());
    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(1, connectionIter.next().getSequenceIndex());
    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(2, connectionIter.next().getSequenceIndex());

    Assert.assertFalse(connectionIter.hasNext());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable#addStop(de.unifreiburg.informatik.cobweb.routing.model.timetable.Stop)}.
   */
  @Test
  public void testAddStop() {
    mTable.addStop(new Stop(4, 7.7f, 8.8f));
    Assert.assertEquals(4, mTable.getStops().size());
    Assert.assertEquals(4, mTable.getStop(4).getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable#addTrip(de.unifreiburg.informatik.cobweb.routing.model.timetable.Trip)}.
   */
  @Test
  public void testAddTrip() {
    mTable.addTrip(new Trip(2));
    Assert.assertEquals(2, mTable.getTrip(2).getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable#generateUniqueStopId()}.
   */
  @Test
  public void testGenerateUniqueStopId() {
    final Set<Integer> ids = new HashSet<>();
    for (int i = 0; i < 1000; i++) {
      Assert.assertTrue(ids.add(mTable.generateUniqueStopId()));
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable#generateUniqueTripId()}.
   */
  @Test
  public void testGenerateUniqueTripId() {
    final Set<Integer> ids = new HashSet<>();
    for (int i = 0; i < 1000; i++) {
      Assert.assertTrue(ids.add(mTable.generateUniqueTripId()));
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable#getConnectionsStartingSince(int)}.
   */
  @Test
  public void testGetConnectionsStartingSince() {
    mTable.addConnections(Collections.singletonList(new Connection(1, 2, 3, 1, 140, 160)));
    Iterator<Connection> connectionIter = mTable.getConnectionsStartingSince(0);
    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(0, connectionIter.next().getSequenceIndex());
    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(1, connectionIter.next().getSequenceIndex());
    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(2, connectionIter.next().getSequenceIndex());
    Assert.assertFalse(connectionIter.hasNext());

    connectionIter = mTable.getConnectionsStartingSince(130);
    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(2, connectionIter.next().getSequenceIndex());
    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(0, connectionIter.next().getSequenceIndex());
    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(1, connectionIter.next().getSequenceIndex());
    Assert.assertFalse(connectionIter.hasNext());

    connectionIter = mTable.getConnectionsStartingSince(140);
    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(2, connectionIter.next().getSequenceIndex());
    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(0, connectionIter.next().getSequenceIndex());
    Assert.assertTrue(connectionIter.hasNext());
    Assert.assertEquals(1, connectionIter.next().getSequenceIndex());
    Assert.assertFalse(connectionIter.hasNext());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable#getStop(int)}.
   */
  @Test
  public void testGetStop() {
    Assert.assertEquals(1, mTable.getStop(1).getId());
    Assert.assertEquals(2, mTable.getStop(2).getId());
    Assert.assertEquals(3, mTable.getStop(3).getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable#getStops()}.
   */
  @Test
  public void testGetStops() {
    final Collection<Stop> stops = mTable.getStops();
    Assert.assertEquals(3, stops.size());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable#getTrip(int)}.
   */
  @Test
  public void testGetTrip() {
    Assert.assertEquals(1, mTable.getTrip(1).getId());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.routing.model.timetable.Timetable#Timetable()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTimetable() {
    try {
      new Timetable();
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
