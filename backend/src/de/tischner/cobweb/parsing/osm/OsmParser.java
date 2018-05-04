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

public final class OsmParser {

  private final static Logger LOGGER = LoggerFactory.getLogger(OsmParser.class);
  private final static String REDUCED_PREFIX = "reduced_";

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

  private final Collection<IOsmFileHandler> mAllHandler;
  private final Path mDirectory;

  private final boolean mUseMetaData;

  public OsmParser(final Path directory, final Collection<IOsmFileHandler> allHandler) {
    this(directory, allHandler, false);
  }

  public OsmParser(final Path directory, final Collection<IOsmFileHandler> allHandler, final boolean useMetaData) {
    mDirectory = directory;
    mAllHandler = allHandler;
    mUseMetaData = useMetaData;
  }

  public void parseOsmFiles() throws ParseException {
    try {
      final Collection<Path> files = OsmParser.findOsmFilesToConsider(mDirectory);
      for (final Path file : files) {
        // Collect all handler that accept this file
        final List<IOsmFileHandler> interestedHandler = mAllHandler.stream().filter(handler -> handler.acceptFile(file))
            .collect(Collectors.toList());
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

  private InputStream pathToStream(final Path osmFile) throws IOException {
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

  private void streamFile(final Path file, final Collection<IOsmFileHandler> interestedHandler)
      throws OsmInputException, IOException {
    try (InputStream input = pathToStream(file)) {
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
