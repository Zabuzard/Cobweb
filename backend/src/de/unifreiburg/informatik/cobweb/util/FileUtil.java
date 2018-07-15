package de.unifreiburg.informatik.cobweb.util;

import java.io.File;
import java.nio.file.Path;

/**
 * Utility class that offers methods related to files.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class FileUtil {
  /**
   * Gets the file extension of the given file.
   *
   * @param file The file to get the extension of
   * @return The file extension of the given file or
   *         {@link EFileExtension#UNKNOWN} if not listed in the enum
   */
  public static EFileExtension getFileExtension(final File file) {
    return FileUtil.getFileExtension(file.getName());
  }

  /**
   * Gets the file extension of the given file.
   *
   * @param file The file to get the extension of
   * @return The file extension of the given file or
   *         {@link EFileExtension#UNKNOWN} if not listed in the enum
   */
  public static EFileExtension getFileExtension(final Path file) {
    return FileUtil.getFileExtension(file.getFileName().toString());
  }

  /**
   * Gets the file extension of the given file.
   *
   * @param filePath The path to the file to get the extension of
   * @return The file extension of the given file or
   *         {@link EFileExtension#UNKNOWN} if not listed in the enum
   */
  public static EFileExtension getFileExtension(final String filePath) {
    final int dotIndex = filePath.lastIndexOf('.');
    if (dotIndex == -1) {
      return EFileExtension.NONE;
    }
    final String extension = filePath.substring(dotIndex + 1);
    return FileUtil.parseFileExtension(extension);
  }

  /**
   * Gets the file extension corresponding to the given extension name.
   *
   * @param extension The name of the file extension
   * @return The corresponding file extension or {@link EFileExtension#UNKNOWN}
   *         if not listed in the enum
   */
  private static EFileExtension parseFileExtension(final String extension) {
    return EFileExtension.fromName(extension.toLowerCase());
  }

  /**
   * Utility class. No implementation.
   */
  private FileUtil() {

  }
}
