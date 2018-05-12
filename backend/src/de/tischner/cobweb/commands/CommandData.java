package de.tischner.cobweb.commands;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/**
 * Pojo which represent command line arguments. Consists of a command and an
 * optional collection of paths.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class CommandData {
  /**
   * The command this data represents.
   */
  private final ECommand mCommand;
  /**
   * An optional collection of paths if present, else empty.
   */
  private final Collection<Path> mPaths;

  /**
   * Creates a new command data instance without any paths.
   *
   * @param command The command this data represents
   */
  public CommandData(final ECommand command) {
    this(command, Collections.emptyList());
  }

  /**
   * Creates a new command data instance.
   *
   * @param command The command this data represents
   * @param paths   A collection of paths or empty if not present
   */
  public CommandData(final ECommand command, final Collection<Path> paths) {
    mCommand = command;
    mPaths = paths;
  }

  /**
   * Gets the command this data represents.
   *
   * @return The command to get
   */
  public ECommand getCommand() {
    return mCommand;
  }

  /**
   * Gets the collection of paths this data represents.
   *
   * @return The collection to get or empty if not present
   */
  public Collection<Path> getPaths() {
    return mPaths;
  }
}
