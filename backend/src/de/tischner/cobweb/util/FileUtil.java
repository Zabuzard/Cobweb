package de.tischner.cobweb.util;

import java.io.File;
import java.nio.file.Path;

public final class FileUtil {
  public static EFileExtension getFileExtension(final File file) {
    return FileUtil.getFileExtension(file.getName());
  }

  public static EFileExtension getFileExtension(final Path file) {
    return FileUtil.getFileExtension(file.getFileName().toString());
  }

  public static EFileExtension getFileExtension(final String fileName) {
    final int dotIndex = fileName.lastIndexOf('.');
    if (dotIndex == -1) {
      return EFileExtension.NONE;
    }
    final String extension = fileName.substring(dotIndex + 1);
    return FileUtil.parseFileExtension(extension);
  }

  private static EFileExtension parseFileExtension(final String extension) {
    return EFileExtension.fromName(extension.toLowerCase());
  }

  /**
   * Utility class. No implementation.
   */
  private FileUtil() {

  }
}
