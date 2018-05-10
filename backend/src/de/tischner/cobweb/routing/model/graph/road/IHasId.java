package de.tischner.cobweb.routing.model.graph.road;

/**
 * Interface for objects that have an ID.<br>
 * <br>
 * It is not specified if the ID must be unique to the object itself. The
 * implementing class is allowed to define the space in which the ID can be
 * interpreted as unique.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
@FunctionalInterface
public interface IHasId {
  /**
   * Gets the ID of the object.<br>
   * <br>
   * It is not specified if the ID must be unique to the object itself. The
   * implementing class is allowed to define the space in which the ID can be
   * interpreted as unique.
   *
   * @return The ID of the object
   */
  long getId();
}
