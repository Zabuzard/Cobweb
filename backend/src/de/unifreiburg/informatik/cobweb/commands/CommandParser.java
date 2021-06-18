package de.unifreiburg.informatik.cobweb.commands;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Utility class which provides methods to parse command line arguments.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class CommandParser {
  /**
   * The default command to use.
   */
  private static final ECommand DEFAULT_COMMAND = ECommand.START;

  /**
   * Parses the given command line arguments into a command data instance.<br>
   * <br>
   * Supported commands are
   * <ul>
   * <li><b><code>empty</code></b> or <b><code>args[0] = start</code></b>: Starts the
   * default service which answers routing requests over a REST API.</li>
   * <li><b><code>args[0] = reduce</code></b>: Reduces all input data such that the
   * default service will run faster.</li>
   * <li><b><code>args[0] = clean</code></b>: Clears the database and all cached and
   * serialized data.</li>
   * <li><b><code>args[0] = benchmark</code></b>: Initializes the API and benchmarks
   * the routing model.</li>
   * <li><b><code>args[1+]</code></b>: Paths to data files that should be used by
   * the commands instead of the files from the directories set in the
   * configuration file.
   * <ul>
   * <li><code>start</code>: Uses the given files as data files (OSM, GTFS) instead
   * of the set directories.</li>
   * <li><code>reduce</code>: Reduces the given unreduced data files (OSM, GTFS)
   * instead of the unreduced files in the set directories.</li>
   * <li><code>clean</code>: Not supported, will ignore paths.</li>
   * <li><code>benchmark</code>: Uses the given files as data files (OSM, GTFS)
   * instead of the set directories.</li>
   * </ul>
   * </li>
   * </ul>
   *
   * @param args The arguments to parse
   * @return A command data instance representing the given arguments
   */
  public static CommandData parseCommands(final String[] args) {
    if (args == null || args.length == 0) {
      return new CommandData(DEFAULT_COMMAND);
    }

    final ECommand command = ECommand.fromName(args[0]);
    if (command == null) {
      return new CommandData(DEFAULT_COMMAND);
    }

    if (args.length == 1) {
      return new CommandData(command);
    }

    final Collection<Path> paths = Arrays.stream(args, 1, args.length).map(Paths::get).collect(Collectors.toList());
    return new CommandData(command, paths);
  }

  /**
   * Utility class. No implementation.
   */
  private CommandParser() {

  }
}
