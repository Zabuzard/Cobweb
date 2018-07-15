package de.unifreiburg.informatik.cobweb.config;

/**
 * Interface for classes that provide name search related configuration
 * settings.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface INameSearchConfigProvider {
  /**
   * Gets the maximal amount of matches to send by the name search server for a
   * query.
   *
   * @return The maximal amount of matches to send
   */
  int getMatchLimit();

  /**
   * Gets the port used by the name search server.
   *
   * @return The port used by the name search server
   */
  int getNameSearchServerPort();
}
