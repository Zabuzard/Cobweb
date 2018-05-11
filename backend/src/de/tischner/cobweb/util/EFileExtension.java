package de.tischner.cobweb.util;

/**
 * Enumeration of file extensions.<br>
 * <br>
 * Use {@link #UNKNOWN} if a file extension is not listed in this enum.
 * {@link #NONE} is used for files without any file extension.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public enum EFileExtension {
  /**
   * BZip2 file, known under the extension <tt>bz2</tt>.
   */
  B_ZIP_TWO("bz2"),
  /**
   * GZip file, known under the extension <tt>gz</tt>.
   */
  G_ZIP("gz"),
  /**
   * File without any file extension.
   */
  NONE(""),
  /**
   * OSM file, known under the extension <tt>osm</tt>.
   */
  OSM("osm"),
  /**
   * File with an extension other than listed in this enumeration.
   */
  UNKNOWN(null),
  /**
   * XZ file, known under the extension <tt>xz</tt>.
   */
  XZ("xz");

  /**
   * Gets the file extension that corresponds to the extension name.
   *
   * @param name The file extension name
   * @return The corresponding file extension or {@link #UNKNOWN} if not present
   */
  public static EFileExtension fromName(final String name) {
    for (final EFileExtension extension : EFileExtension.values()) {
      final String extensionName = extension.getName();
      if ((extensionName == null && name == null) || extensionName != null && extensionName.equals(name)) {
        return extension;
      }
    }
    return EFileExtension.UNKNOWN;
  }

  /**
   * The name of the file extension.
   */
  private final String mName;

  /**
   * Creates a new file extension with the given name.
   *
   * @param name The name of the file extension
   */
  private EFileExtension(final String name) {
    mName = name;
  }

  /**
   * Gets the name of the file extension.
   *
   * @return The name of the file extension
   */
  public String getName() {
    return mName;
  }
}
