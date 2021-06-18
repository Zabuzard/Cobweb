package de.unifreiburg.informatik.cobweb.routing.model.graph;

import java.util.EnumSet;
import java.util.Set;

/**
 * Interface for classes that have transportation mode restrictions. Such an
 * object can only be used with any of the given transportation modes.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IHasTransportationMode {
  /**
   * Gets the transportation modes allowed for this instance.<br>
   * <br>
   * For efficiency this method should return instances of {@link EnumSet} or
   * similar.
   *
   * @return The transportation modes allowed for this instace
   */
  Set<ETransportationMode> getTransportationModes();

  /**
   * Whether the instance has the given transportation mode.
   *
   * @param mode The mode in question
   * @return <code>True</code> if the instance has the given transportation mode,
   *         <code>false</code> otherwise
   */
  boolean hasTransportationMode(ETransportationMode mode);
}
