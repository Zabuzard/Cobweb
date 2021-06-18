package de.unifreiburg.informatik.cobweb.parsing;

import java.nio.file.Path;

/**
 * Handler for a files. Acts as a filter that can either accept or reject a
 * given file.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
@FunctionalInterface
public interface IFileHandler {
  /**
   * Whether or not this handler accepts the given file.
   *
   * @param file The file in question
   * @return <code>True</code> if the file is accepted, <code>false</code> otherwise
   */
  boolean isAcceptingFile(Path file);
}
