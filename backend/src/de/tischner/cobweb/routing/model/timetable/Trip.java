package de.tischner.cobweb.routing.model.timetable;

import java.io.Serializable;
import java.util.List;

import org.eclipse.collections.impl.list.mutable.FastList;

import de.tischner.cobweb.routing.model.graph.IHasId;

/**
 * A trip of a transit network. Has an ID and a sequence of connections.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class Trip implements IHasId, Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The unique ID of the trip.
   */
  private final int mId;
  /**
   * The sequence of the connections represented by this trip.
   */
  private final List<Connection> mSequence;

  /**
   * Creates a new initially empty trip.
   *
   * @param id The unique ID of the trip
   */
  public Trip(final int id) {
    mId = id;
    mSequence = FastList.newList();
  }

  /**
   * Adds the given connection to the trip. Addition must be made in order
   * according to the sequence order.
   *
   * @param connection The connection to add
   */
  public void addConnectionToSequence(final Connection connection) {
    mSequence.add(connection);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Trip)) {
      return false;
    }
    final Trip other = (Trip) obj;
    if (this.mId != other.mId) {
      return false;
    }
    return true;
  }

  /**
   * Gets the connection in the sequence at the given index
   *
   * @param sequenceIndex The index in the sequence to get the connection for
   * @return The connection in the sequence at the given index
   * @throws IndexOutOfBoundsException If the index is below <tt>0</tt> or
   *                                   greater equals the sequence size
   */
  public Connection getConnectionAtSequenceIndex(final int sequenceIndex) throws IndexOutOfBoundsException {
    if (sequenceIndex < 0 || sequenceIndex >= mSequence.size()) {
      throw new IndexOutOfBoundsException();
    }
    return mSequence.get(sequenceIndex);
  }

  @Override
  public int getId() {
    return mId;
  }

  /**
   * Gets the sequence of connections represented by this trip.
   *
   * @return The sequence of connections represented by this trip
   */
  public List<Connection> getSequence() {
    return mSequence;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.mId;
    return result;
  }
}
