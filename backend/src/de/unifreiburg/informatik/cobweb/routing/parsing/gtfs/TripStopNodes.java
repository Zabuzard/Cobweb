package de.unifreiburg.informatik.cobweb.routing.parsing.gtfs;

import de.unifreiburg.informatik.cobweb.routing.model.graph.IHasId;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ISpatial;

/**
 * POJO containing an arrival and the corresponding departure node of a trip at
 * a stop.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the nodes
 */
public final class TripStopNodes<N extends INode & IHasId & ISpatial> {
  /**
   * The arrival node of the trip at the stop.
   */
  private final N mArrNode;
  /**
   * The arrival time of the node in seconds since midnight.
   */
  private final int mArrTime;
  /**
   * The departure node of the trip at the stop.
   */
  private final N mDepNode;
  /**
   * The departure time of the node in seconds since midnight.
   */
  private final int mDepTime;

  /**
   * Creates a new instance with the given nodes and times.
   *
   * @param arrNode The arrival node of the trip at the stop
   * @param depNode The departure node of the trip at the stop
   * @param arrTime The arrival time of the node in seconds since midnight
   * @param depTime The departure time of the node in seconds since midnight
   */
  public TripStopNodes(final N arrNode, final N depNode, final int arrTime, final int depTime) {
    mArrNode = arrNode;
    mDepNode = depNode;
    mArrTime = arrTime;
    mDepTime = depTime;
  }

  /**
   * Gets the arrival node of the trip at the stop.
   *
   * @return The arrival node to get
   */
  public N getArrNode() {
    return mArrNode;
  }

  /**
   * Gets the arrival time of the node.
   *
   * @return The arrival time in seconds since midnight
   */
  public int getArrTime() {
    return mArrTime;
  }

  /**
   * Gets the departure node of the trip at the stop.
   *
   * @return The departure node to get
   */
  public N getDepNode() {
    return mDepNode;
  }

  /**
   * Gets the departure time of the node.
   *
   * @return The departure time in seconds since midnight
   */
  public int getDepTime() {
    return mDepTime;
  }
}
