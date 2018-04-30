package de.tischner.cobweb.util;

import java.io.File;
import java.nio.file.Path;

public final class FileUtil {
  public static FileExtension getFileExtension(final File file) {
    return FileUtil.getFileExtension(file.getName());
  }

  public static FileExtension getFileExtension(final Path file) {
    return FileUtil.getFileExtension(file.getFileName().toString());
  }

  public static FileExtension getFileExtension(final String fileName) {
    final int dotIndex = fileName.lastIndexOf('.');
    if (dotIndex == -1) {
      return FileExtension.NONE;
    }
    final String extension = fileName.substring(dotIndex + 1);
    return FileUtil.parseFileExtension(extension);
  }

  private static FileExtension parseFileExtension(final String extension) {
    final String extensionLowered = extension.toLowerCase();
    switch (extensionLowered) {
    case "":
      return FileExtension.NONE;
    case "bz2":
      return FileExtension.B_ZIP_TWO;
    case "gz":
      return FileExtension.G_ZIP;
    case "xz":
      return FileExtension.XZ;
    case "osm":
      return FileExtension.OSM;
    default:
      return FileExtension.UNKNOWN;
    }
  }

  /**
   * Utility class. No implementation.
   */
  private FileUtil() {

  }
}
