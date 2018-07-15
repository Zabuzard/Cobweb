package de.unifreiburg.informatik.cobweb.routing.model.graph.transit;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ISpatial;

/**
 * A stop in a transit network. Has spatial information and a list of arrival
 * nodes.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the nodes
 */
public final class TransitStop<N extends INode> implements Serializable, ISpatial {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * A list of arrival nodes, must be sorted and should provide random access.
   */
  private final List<NodeTime<N>> mArrivalNodes;
  /**
   * The latitude coordinate of this stop.
   */
  private float mLatitude;
  /**
   * The longitude coordinate of this stop.
   */
  private float mLongitude;

  /**
   * Creates a new transit stop with the given nodes and coordinates.
   *
   * @param arrivalNodes The arrival nodes belonging to this stop, the list
   *                     <b>must be sorted</b> and should implement
   *                     {@link RandomAccess}
   * @param latitude     The latitude coordinate of this stop
   * @param longitude    The longitude coordinate of this stop
   */
  public TransitStop(final List<NodeTime<N>> arrivalNodes, final float latitude, final float longitude) {
    // The list must be sorted and should implement RandomAccess
    mArrivalNodes = arrivalNodes;
    mLatitude = latitude;
    mLongitude = longitude;
  }

  /**
   * Gets the arrival nodes belonging to this stop. The method does not
   * guarantee any order.<br>
   * <br>
   * Note that the returned collection is backed by the stop, changes to it are
   * reflected to the stop.
   *
   * @return The arrival nodes belonging to this stop
   */
  public Collection<NodeTime<N>> getArrivalNodes() {
    return mArrivalNodes;
  }

  @Override
  public float getLatitude() {
    return mLatitude;
  }

  @Override
  public float getLongitude() {
    return mLongitude;
  }

  @Override
  public void setLatitude(final float latitude) {
    mLatitude = latitude;
  }

  @Override
  public void setLongitude(final float longitude) {
    mLongitude = longitude;
  }
}
