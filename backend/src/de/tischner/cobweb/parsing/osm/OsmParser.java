package de.tischner.cobweb.parsing.osm;

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

import de.tischner.cobweb.parsing.ParseException;
import de.tischner.cobweb.util.EFileExtension;
import de.tischner.cobweb.util.FileUtil;
import de.topobyte.osm4j.core.access.OsmInputException;
import de.topobyte.osm4j.core.access.OsmReader;
import de.topobyte.osm4j.xml.dynsax.OsmXmlReader;

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
  private static final String REDUCED_PREFIX = "reduced_";

  /**
   * Walks through the given directory and collects all OSM files that should be
   * considered for parsing. If a file is prefixed with {@link #REDUCED_PREFIX},
   * the unreduced variant will not be considered.
   *
   * @param directory Directory that contains the OSM files
   * @return A collection of OSM files that should be considered for parsing
   * @throws IOException If an I/O exception occurred wile walking through the
   *                     directory
   */
  private static Collection<Path> findOsmFilesToConsider(final Path directory) throws IOException {
    final Set<Path> allPaths = Files.walk(directory).filter(Files::isRegularFile).collect(Collectors.toSet());
    final List<Path> pathsToConsider = new ArrayList<>();
    // Filter out non-reduced versions if reduced are available
    for (final Path path : allPaths) {
      final Path fileName = path.getFileName();
      final String fileNameAsText = fileName.toString();
      if (fileNameAsText.startsWith(REDUCED_PREFIX)) {
        // Consider reduced version
        pathsToConsider.add(path);
        continue;
      }

      // Collect only if there is no a reduced version
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
      case NONE: // Fall through
      case OSM:
        return bufferedInput;
      case B_ZIP_TWO:
        return new BZip2CompressorInputStream(bufferedInput);
      case G_ZIP:
        return new GzipCompressorInputStream(bufferedInput);
      case XZ:
        return new XZCompressorInputStream(bufferedInput);
      case UNKNOWN: // Fall through
      default: // Ignore file
        return null;
    }
  }

  /**
   * Handler to notify when parsing entities.
   */
  private final Collection<IOsmFileHandler> mAllHandler;
  /**
   * Directory that contains the OSM files.
   */
  private final Path mDirectory;

  /**
   * Whether or not meta data of OSM entities should be parsed.
   */
  private final boolean mUseMetaData;

  /**
   * Creates a new OSM parser which will parse OSM files in the given directory
   * and notify the given handler for all parsed OSM entities.<br>
   * <br>
   * The parser will not parse meta data of entities.
   *
   * @param directory  The directory that contains the OSM files
   * @param allHandler The handler to notify when parsing entities
   */
  public OsmParser(final Path directory, final Collection<IOsmFileHandler> allHandler) {
    this(directory, allHandler, false);
  }

  /**
   * Creates a new OSM parser which will parse OSM files in the given directory
   * and notify the given handler for all parsed OSM entities.
   *
   * @param directory   The directory that contains the OSM files
   * @param allHandler  The handler to notify when parsing entities
   * @param useMetaData Whether or not the parser should parse meta data for the
   *                    entities
   */
  public OsmParser(final Path directory, final Collection<IOsmFileHandler> allHandler, final boolean useMetaData) {
    mDirectory = directory;
    mAllHandler = allHandler;
    mUseMetaData = useMetaData;
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
      final Collection<Path> files = OsmParser.findOsmFilesToConsider(mDirectory);
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
