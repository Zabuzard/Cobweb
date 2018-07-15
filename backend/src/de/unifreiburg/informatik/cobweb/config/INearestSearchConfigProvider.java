package de.unifreiburg.informatik.cobweb.config;

/**
 * Interface for classes that provide nearest search related configuration
 * settings.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface INearestSearchConfigProvider {
  /**
   * Gets the port used by the nearest search server.
   *
   * @return The port used by the nearest search server
   */
  int getNearestSearchServerPort();
}
