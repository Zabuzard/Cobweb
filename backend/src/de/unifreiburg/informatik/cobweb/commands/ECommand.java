package de.unifreiburg.informatik.cobweb.commands;

/**
 * Enumeration of supported commands.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public enum ECommand {
  /**
   * Command which starts the API and then runs benchmarks on it.
   */
  BENCHMARK("benchmark"),
  /**
   * Command which clears the database and all cached and serialized data.
   */
  CLEAN("clean"),
  /**
   * Command which reduces all input data such that the default service will run
   * faster.
   */
  REDUCE("reduce"),
  /**
   * Command which starts the default service that answers routing requests over
   * a REST API.
   */
  START("start");

  /**
   * Gets the command that corresponds to the command name.
   *
   * @param name The command name
   * @return The corresponding command or <tt>null</tt> if not present
   */
  public static ECommand fromName(final String name) {
    for (final ECommand command : ECommand.values()) {
      if (command.getName().equals(name)) {
        return command;
      }
    }
    return null;
  }

  /**
   * The name of the command.
   */
  private final String mName;

  /**
   * Creates a new command with the given name.
   *
   * @param name The name of the command
   */
  private ECommand(final String name) {
    mName = name;
  }

  /**
   * Gets the name of the command.
   *
   * @return The name of the command
   */
  public String getName() {
    return mName;
  }
}
