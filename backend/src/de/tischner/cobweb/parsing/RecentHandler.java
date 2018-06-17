package de.tischner.cobweb.parsing;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File handler that only accepts files that are more recent or new compared to
 * the given info object.<br>
 * <br>
 * The method {@link #updateInfo()} is used to update the info file after
 * accepting new or more recent files.<br>
 * <br>
 * The format of the info files is assumed to be in a tab-separated format
 * (<tt>tsv</tt>) with two columns:
 * <ol>
 * <li>Path: The path to a file that is registered</li>
 * <li>last modified: The date the file was last modified when it was
 * registered, in milliseconds since epoch</li>
 * </ol>
 * And example info file might contain a line like
 *
 * <pre>
 * res\input\osmTest\freiburg-regbez-latest.osm    1525362312546
 * </pre>
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RecentHandler implements IFileHandler {
  /**
   * The delimiter to use in the file info format.
   */
  private static final String DELIMITER = "\t";
  /**
   * The logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(RecentHandler.class);
  /**
   * The path to the info file.
   */
  private final Path mInfo;
  /**
   * Map connecting paths to the date they where modified last, given in
   * milliseconds since epoch.
   */
  private Map<Path, Long> mPathToLastModified;

  /**
   * Creates a new recent handler using the given info file.
   *
   * @param info The path to the info file
   * @throws IOException If an I/O exception occurred while reading the info
   *                     file
   */
  public RecentHandler(final Path info) throws IOException {
    mInfo = info;
    initialize();
  }

  /**
   * Accepts the given file if it is more recent or new compared to the given
   * info file.<br>
   * <br>
   * That is, it accepts if the info object did not contain this path or if it
   * was modified after it was registered in the info object.<br>
   * <br>
   * It collects all accepted files. A call to {@link #updateInfo()} updates the
   * info object accordingly to the accepted files.
   *
   * @throws UncheckedIOException If an I/O exception occurred wile reading the
   *                              last modified property of the given file
   */
  @Override
  public boolean isAcceptingFile(final Path file) throws UncheckedIOException {
    long lastModified;
    try {
      lastModified = Files.getLastModifiedTime(file).toMillis();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    final Long lastModifiedInfo = mPathToLastModified.get(file);

    // Reject if file is not new and not more recent
    if (lastModifiedInfo != null && lastModified <= lastModifiedInfo) {
      return false;
    }

    // File is new or more recent
    mPathToLastModified.put(file, lastModified);
    return true;
  }

  /**
   * Updates the info file based on files that where accepted by
   * {@link #isAcceptingFile(Path)}.
   *
   * @throws IOException If an I/O exception occurred while writing to the info
   *                     file
   */
  public void updateInfo() throws IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Updating info to: {}", mInfo);
    }

    // Read current state
    final Map<Path, Long> currentInfo = readCurrentInfo();
    // Update
    currentInfo.putAll(mPathToLastModified);
    // Write the updated info back
    final List<String> lines = currentInfo.entrySet().stream()
        .map(entry -> entry.getKey() + DELIMITER + entry.getValue()).collect(Collectors.toList());
    // Ensure the directory structure is there
    Files.createDirectories(mInfo.getParent());
    Files.write(mInfo, lines);
  }

  /**
   * Initializes the handler by parsing the info file.
   *
   * @throws IOException If an I/O exception occurred when reading the info file
   */
  private void initialize() throws IOException {
    mPathToLastModified = readCurrentInfo();
  }

  /**
   * Reads the current content of the info file.
   *
   * @return The current content of the info file
   * @throws IOException If an I/O exception occurred when reading the info file
   */
  private Map<Path, Long> readCurrentInfo() throws IOException {
    if (!Files.isRegularFile(mInfo)) {
      return new HashMap<>();
    }

    // Read in the file and parse all info
    Map<Path, Long> pathToLastModified = new HashMap<>();
    try (Stream<String> lines = Files.lines(mInfo)) {
      final Pattern delimiterPatt = Pattern.compile(DELIMITER);
      pathToLastModified = lines.map(delimiterPatt::split)
          .collect(Collectors.toMap(data -> Paths.get(data[0]), data -> Long.valueOf(data[1])));
    }
    return pathToLastModified;
  }
}
