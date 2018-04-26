package de.tischner.cobweb.parsing;

import java.nio.file.Path;

public interface IFileHandler {
  boolean acceptFile(Path file);
}
