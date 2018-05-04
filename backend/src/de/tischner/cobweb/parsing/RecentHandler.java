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

public final class RecentHandler implements IFileHandler {
  private static final String DELIMITER = "\t";
  private final static Logger LOGGER = LoggerFactory.getLogger(RecentHandler.class);
  private final Path mInfo;
  private Map<Path, Long> mPathToLastModified;

  public RecentHandler(final Path info) throws IOException {
    mInfo = info;
    mPathToLastModified = new HashMap<>();
    initialize();
  }

  @Override
  public boolean acceptFile(final Path file) throws UncheckedIOException {
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

  public void initialize() throws IOException {
    if (!Files.isRegularFile(mInfo)) {
      return;
    }

    // Read in the file and parse all info
    try (Stream<String> lines = Files.lines(mInfo)) {
      final Pattern delimiterPatt = Pattern.compile(DELIMITER);
      mPathToLastModified = lines.map(delimiterPatt::split)
          .collect(Collectors.toMap(data -> Paths.get(data[0]), data -> Long.valueOf(data[1])));
    }
  }

  public void updateInfo() throws IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Updating info to: {}", mInfo);
    }

    // Write the updated info back
    final List<String> lines = mPathToLastModified.entrySet().stream()
        .map(entry -> entry.getKey() + DELIMITER + entry.getValue()).collect(Collectors.toList());
    Files.write(mInfo, lines);
  }
}
