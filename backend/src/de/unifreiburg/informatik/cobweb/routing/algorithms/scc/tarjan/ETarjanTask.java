package de.unifreiburg.informatik.cobweb.routing.algorithms.scc.tarjan;

/**
 * Tasks a {@link TarjanTaskElement} of Tarjans algorithm for SCC computation
 * wanders through until completion.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public enum ETarjanTask {
  /**
   * Processes the successors of this element.
   */
  GET_SUCCESSORS,
  /**
   * Registers the element to be processed.
   */
  INDEX,
  /**
   * Finishes this element by updating its low link value or establishing a new
   * SCC.
   */
  SET_LOWLINK
}
