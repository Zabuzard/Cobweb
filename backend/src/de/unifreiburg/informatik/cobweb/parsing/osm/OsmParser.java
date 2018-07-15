package de.unifreiburg.informatik.cobweb.parsing.osm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.osm4j.core.access.OsmInputException;
import de.topobyte.osm4j.core.access.OsmReader;
import de.topobyte.osm4j.xml.dynsax.OsmXmlReader;
import de.unifreiburg.informatik.cobweb.parsing.ParseException;
import de.unifreiburg.informatik.cobweb.util.EFileExtension;
import de.unifreiburg.informatik.cobweb.util.FileUtil;

/**
 * Parser that is able to stream OSM files and parse OSM entities. Will notify
 * and forward all entities to all registered {@link IOsmFileHandler}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class OsmParser {
  /**
   * Logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(OsmParser.class);
  /**
   * Prefix of an OSM file that was reduced. The parser prefers them over an
   * unreduced variant.
   */
  static final String REDUCED_PREFIX = "reduced_";

  /**
   * Walks through the given directory or list of files and collects all OSM
   * files that should be considered for parsing. If a file is prefixed with
   * {@link #REDUCED_PREFIX}, the unreduced variant will not be considered. This
   * behavior can be reversed by using <tt>considerUnreduced</tt>.
   *
   * @param directory             Directory that contains the OSM files or
   *                              <tt>null</tt> if <tt>files</tt> is used
   * @param files                 A collection of files which contain the OSM
   *                              files or <tt>null</tt> if <tt>directory</tt>
   *                              is used
   * @param considerUnreducedOnly If <tt>true</tt> the behavior is reversed and
   *                              the method collects all unreduced variants
   * @return A collection of OSM files that should be considered for parsing
   * @throws IOException If an I/O exception occurred wile walking through the
   *                     directory
   */
  private static Collection<Path> findOsmFilesToConsider(final Path directory, final Collection<Path> files,
      final boolean considerUnreducedOnly) throws IOException {
    final Set<Path> allPaths;
    if (directory != null) {
      allPaths = Files.walk(directory).filter(Files::isRegularFile).collect(Collectors.toSet());
    } else {
      allPaths = files.stream().filter(Files::isRegularFile).collect(Collectors.toSet());
    }

    final List<Path> pathsToConsider = new ArrayList<>();
    // Filter out non-reduced versions if reduced are available, or reversed
    // behavior if flag is set
    for (final Path path : allPaths) {
      final Path fileName = path.getFileName();
      final String fileNameAsText = fileName.toString();
      if (!considerUnreducedOnly && fileNameAsText.startsWith(REDUCED_PREFIX)) {
        // Consider reduced version
        pathsToConsider.add(path);
        continue;
      } else if (considerUnreducedOnly && !fileNameAsText.startsWith(REDUCED_PREFIX)) {
        // Consider unreduced version
        pathsToConsider.add(path);
        continue;
      }

      if (considerUnreducedOnly) {
        // Never collect a reduced version
        continue;
      }

      // Collect only if there is no reduced version
      final String reducedFileNameAsText = REDUCED_PREFIX + fileNameAsText;
      final Path reducedFileName = Paths.get(reducedFileNameAsText);
      final Path reducedPath = path.getParent().resolve(reducedFileName);
      if (!allPaths.contains(reducedPath)) {
        pathsToConsider.add(path);
      }
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("OSM files are {}, only considering {}", allPaths, pathsToConsider);
    }
    return pathsToConsider;
  }

  /**
   * Creates an input stream for the given OSM file such that it is ready to be
   * used for parsing. Will correctly handle compressed files like <tt>bz2</tt>,
   * <tt>gz</tt> or <tt>xz</tt> files.
   *
   * @param osmFile The OSM file to create an input stream for
   * @return An input stream for the given OSM file
   * @throws IOException If an I/O exception occurred while opening the file
   */
  private static InputStream pathToStream(final Path osmFile) throws IOException {
    final BufferedInputStream bufferedInput = new BufferedInputStream(Files.newInputStream(osmFile));
    final EFileExtension extension = FileUtil.getFileExtension(osmFile);
    switch (extension) {
      case OSM:
        return bufferedInput;
      case B_ZIP_TWO:
        return new BZip2CompressorInputStream(bufferedInput);
      case G_ZIP:
        return new GzipCompressorInputStream(bufferedInput);
      case XZ:
        return new XZCompressorInputStream(bufferedInput);
      case NONE: // Ignore file
        return null;
      case UNKNOWN: // Ignore file
        return null;
      case ZIP: // Ignore file
        return null;
      default: // Ignore file
        return null;
    }
  }

  /**
   * Handler to notify when parsing entities.
   */
  private final Collection<IOsmFileHandler> mAllHandler;
  /**
   * If <tt>true</tt> the parser considers unreduced variants of OSM files only.
   */
  private final boolean mConsiderUnreducedOnly;
  /**
   * Directory that contains the OSM files or <tt>null</tt> if {@link #mFiles}
   * is used.
   */
  private final Path mDirectory;
  /**
   * Collection of files that contain the OSM files or <tt>null</tt> if
   * {@link #mDirectory} is used.
   */
  private final Collection<Path> mFiles;
  /**
   * If <tt>true</tt> each OSM file will be streamed twice, else only once
   */
  private final boolean mStreamTwice;
  /**
   * Whether or not meta data of OSM entities should be parsed.
   */
  private final boolean mUseMetaData;

  /**
   * Creates a new OSM parser which will parse OSM files in the given directory
   * or collection of files and notify the given handler for all parsed OSM
   * entities.<br>
   * <br>
   * The parser will not parse meta data of entities.
   *
   * @param directory  The directory that contains the OSM files or
   *                   <tt>null</tt> if <tt>files</tt> is used
   * @param files      Collection of files that contain the OSM files or
   *                   <tt>null</tt> if <tt>directory</tt> is used
   * @param allHandler The handler to notify when parsing entities
   */
  public OsmParser(final Path directory, final Collection<Path> files, final Collection<IOsmFileHandler> allHandler) {
    this(directory, files, allHandler, false, false, false);
  }

  /**
   * Creates a new OSM parser which will parse OSM files in the given directory
   * or collection of files and notify the given handler for all parsed OSM
   * entities.<br>
   * <br>
   * The parser will not parse meta data of entities.
   *
   * @param directory             The directory that contains the OSM files or
   *                              <tt>null</tt> if <tt>files</tt> is used
   * @param files                 Collection of files that contain the OSM files
   *                              or <tt>null</tt> if <tt>directory</tt> is used
   * @param allHandler            The handler to notify when parsing entities
   * @param considerUnreducedOnly If <tt>true</tt> the parser considers
   *                              unreduced variants of OSM files only
   * @param streamTwice           If <tt>true</tt> each OSM file will be
   *                              streamed twice, else only once
   */
  public OsmParser(final Path directory, final Collection<Path> files, final Collection<IOsmFileHandler> allHandler,
      final boolean considerUnreducedOnly, final boolean streamTwice) {
    this(directory, files, allHandler, considerUnreducedOnly, streamTwice, false);
  }

  /**
   * Creates a new OSM parser which will parse OSM files in the given directory
   * or collection of files and notify the given handler for all parsed OSM
   * entities.
   *
   * @param directory             The directory that contains the OSM files or
   *                              <tt>null</tt> if <tt>files</tt> is used
   * @param files                 Collection of files that contain the OSM files
   *                              or <tt>null</tt> if <tt>directory</tt> is used
   * @param allHandler            The handler to notify when parsing entities
   * @param considerUnreducedOnly If <tt>true</tt> the parser considers
   *                              unreduced variants of OSM files only
   * @param streamTwice           If <tt>true</tt> each OSM file will be
   *                              streamed twice, else only once
   * @param useMetaData           Whether or not the parser should parse meta
   *                              data for the entities
   */
  public OsmParser(final Path directory, final Collection<Path> files, final Collection<IOsmFileHandler> allHandler,
      final boolean considerUnreducedOnly, final boolean streamTwice, final boolean useMetaData) {
    mDirectory = directory;
    mFiles = files;
    mAllHandler = allHandler;
    mConsiderUnreducedOnly = considerUnreducedOnly;
    mUseMetaData = useMetaData;
    mStreamTwice = streamTwice;
  }

  /**
   * Parses the OSM files in the given directory and notifies all given handler
   * that accept the file when parsing OSM entities.<br>
   * <br>
   * The parser won't parse a file if no handler accepts the file.
   *
   * @throws ParseException If a parse exception occurred while reading the OSM
   *                        files. For example if the directory is invalid or
   *                        the OSM files are in a wrong format.
   */
  public void parseOsmFiles() throws ParseException {
    try {
      final Collection<Path> files = OsmParser.findOsmFilesToConsider(mDirectory, mFiles, mConsiderUnreducedOnly);
      for (final Path file : files) {
        // Collect all handler that accept this file
        final List<IOsmFileHandler> interestedHandler =
            mAllHandler.stream().filter(handler -> handler.isAcceptingFile(file)).collect(Collectors.toList());
        // Do not parse if nobody accepts
        if (interestedHandler.isEmpty()) {
          continue;
        }
        // Parse the file and notify all interested handler
        streamFile(file, interestedHandler);
        if (mStreamTwice) {
          streamFile(file, interestedHandler);
        }
      }
    } catch (IOException | OsmInputException e) {
      throw new ParseException(e);
    }
  }

  /**
   * Streams the given OSM file and notifies all given handler.
   *
   * @param file              OSM file to stream
   * @param interestedHandler All handler that are interested in this file
   * @throws OsmInputException If the OSM file is in a wrong format
   * @throws IOException       If an I/O exception occurred while reading the
   *                           file
   */
  private void streamFile(final Path file, final Collection<IOsmFileHandler> interestedHandler)
      throws OsmInputException, IOException {
    try (InputStream input = OsmParser.pathToStream(file)) {
      // Ignore file
      if (input == null) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("File type is not supported, skipping: {}", file);
        }
        return;
      }
      final OsmHandlerForwarder forwarder = new OsmHandlerForwarder(interestedHandler);
      final OsmReader reader = new OsmXmlReader(input, mUseMetaData);
      reader.setHandler(forwarder);
      reader.read();
    }
  }
}
