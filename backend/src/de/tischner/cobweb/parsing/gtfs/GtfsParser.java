package de.tischner.cobweb.parsing.gtfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.onebusaway.gtfs.serialization.GtfsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.parsing.ParseException;
import de.tischner.cobweb.util.EFileExtension;
import de.tischner.cobweb.util.FileUtil;

/**
 * Parser that is able to stream GTFS files and parse GTFS entities. Will notify
 * and forward all entities to all registered {@link IGtfsFileHandler}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class GtfsParser {
  /**
   * Logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(GtfsParser.class);

  /**
   * Walks through the given directory or list of files and collects all GTFS
   * files that should be considered for parsing.
   *
   * @param directory Directory that contains the GTFS files or <tt>null</tt> if
   *                  <tt>files</tt> is used
   * @param files     A collection of files which contain the GTFS files or
   *                  <tt>null</tt> if <tt>directory</tt> is used
   * @return A collection of GTFS files that should be considered for parsing
   * @throws IOException If an I/O exception occurred wile walking through the
   *                     directory
   */
  private static Collection<Path> findGtfsFilesToConsider(final Path directory, final Collection<Path> files)
      throws IOException {
    final Collection<Path> allPaths;
    if (directory != null && Files.exists(directory)) {
      allPaths = Files.walk(directory).filter(Files::isRegularFile).collect(Collectors.toList());
    } else if (files != null) {
      allPaths = files.stream().filter(Files::isRegularFile).collect(Collectors.toList());
    } else {
      allPaths = Collections.emptyList();
    }

    // Filter out non-GTFS archives
    final List<Path> pathsToConsider = allPaths.stream().filter(GtfsParser::isGtfsArchive).collect(Collectors.toList());

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("GTFS files are {}, only considering {}", allPaths, pathsToConsider);
    }
    return pathsToConsider;
  }

  /**
   * Whether the given path is a GTFS archive or not, determined by its file
   * extension.
   *
   * @param gtfsArchive The GTFS archive in question
   * @return <tt>True</tt> if the given file is a GTFS archive, <tt>false</tt>
   *         otherwise
   */
  private static boolean isGtfsArchive(final Path gtfsArchive) {
    final EFileExtension extension = FileUtil.getFileExtension(gtfsArchive);
    return EFileExtension.ZIP.equals(extension);
  }

  /**
   * Streams the given GTFS file and notifies all given handler.
   *
   * @param file              GTFS file to stream
   * @param interestedHandler All handler that are interested in this file
   * @throws IOException If an I/O exception occurred while reading the file or
   *                     if the file is in a the wrong format
   */
  private static void streamFile(final Path file, final Collection<IGtfsFileHandler> interestedHandler)
      throws IOException {
    // Ignore file
    final GtfsHandlerForwarder forwarder = new GtfsHandlerForwarder(interestedHandler);
    final GtfsReader reader = new GtfsReader();
    reader.setInputLocation(file.toFile());
    reader.addEntityHandler(forwarder);
    reader.run();
    forwarder.complete();
  }

  /**
   * Handler to notify when parsing entities.
   */
  private final Collection<IGtfsFileHandler> mAllHandler;
  /**
   * Directory that contains the GTFS files or <tt>null</tt> if {@link #mFiles}
   * is used.
   */
  private final Path mDirectory;

  /**
   * Collection of files that contain the GTFS files or <tt>null</tt> if
   * {@link #mDirectory} is used.
   */
  private final Collection<Path> mFiles;

  /**
   * Creates a new GTFS parser which will parse GTFS files in the given
   * directory or collection of files and notify the given handler for all
   * parsed GTFS entities.
   *
   * @param directory  The directory that contains the GTFS files or
   *                   <tt>null</tt> if <tt>files</tt> is used
   * @param files      Collection of files that contain the GTFS files or
   *                   <tt>null</tt> if <tt>directory</tt> is used
   * @param allHandler The handler to notify when parsing entities
   */
  public GtfsParser(final Path directory, final Collection<Path> files, final Collection<IGtfsFileHandler> allHandler) {
    mDirectory = directory;
    mFiles = files;
    mAllHandler = allHandler;
  }

  /**
   * Parses the GTFS files in the given directory and notifies all given handler
   * that accept the file when parsing GTFS entities.<br>
   * <br>
   * The parser won't parse a file if no handler accepts the file.
   *
   * @throws ParseException If a parse exception occurred while reading the GTFS
   *                        files. For example if the directory is invalid or
   *                        the GTFS files are in a wrong format.
   */
  public void parseGtfsFiles() throws ParseException {
    try {
      final Collection<Path> files = GtfsParser.findGtfsFilesToConsider(mDirectory, mFiles);
      for (final Path file : files) {
        // Collect all handler that accept this file
        final List<IGtfsFileHandler> interestedHandler =
            mAllHandler.stream().filter(handler -> handler.isAcceptingFile(file)).collect(Collectors.toList());
        // Do not parse if nobody accepts
        if (interestedHandler.isEmpty()) {
          continue;
        }
        // Parse the file and notify all interested handler
        GtfsParser.streamFile(file, interestedHandler);
      }
    } catch (final IOException e) {
      throw new ParseException(e);
    }
  }
}
