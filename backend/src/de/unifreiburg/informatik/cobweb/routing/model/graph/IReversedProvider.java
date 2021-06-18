package de.unifreiburg.informatik.cobweb.routing.model.graph;

import java.io.Serializable;

/**
 * Interface for objects that provide a reversed state flag.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
@FunctionalInterface
public interface IReversedProvider extends Serializable {
  /**
   * Whether or not the object is reversed.
   *
   * @return <code>True</code> if the object is reversed, <code>false</code> otherwise
   */
  boolean isReversed();
}
