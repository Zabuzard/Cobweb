package de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor;

import java.util.Optional;

import de.unifreiburg.informatik.cobweb.routing.model.graph.ISpatial;

/**
 * Interface for algorithms that solve nearest neighbor computation problems.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> Type of the edges which must offer spatial data
 */
public interface INearestNeighborComputation<E extends ISpatial> {
  /**
   * Gets the neighbor nearest to the given point.
   *
   * @param point The point in question
   * @return The neighbor nearest to the given point or <tt>empty</tt> if there
   *         is no
   */
  public Optional<E> getNearestNeighbor(E point);
}
