package de.tischner.cobweb.routing.algorithms.scc;

import java.util.Collection;

/**
 * Interface for algorithms that are able to compute strongly connected
 * components (SCCs) of a given structure.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of nodes
 */
public interface ISccComputation<N> {
  /**
   * Gets the largest SCC in the given structure.
   *
   * @return The largest SCC
   */
  StronglyConnectedComponent<N> getLargestScc();

  /**
   * Gets a collection of all SCCs in the given structure.
   *
   * @return A collection of all SCCs
   */
  Collection<StronglyConnectedComponent<N>> getSccs();
}
