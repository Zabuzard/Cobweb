package de.tischner.cobweb.routing.model.graph.road;

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
   * @return <tt>True</tt> if the object is reversed, <tt>false</tt> otherwise
   */
  boolean isReversed();
}
