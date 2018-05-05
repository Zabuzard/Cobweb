package de.tischner.cobweb.util;

public enum EFileExtension {
  B_ZIP_TWO("bz2"), G_ZIP("gz"), NONE(""), OSM("osm"), UNKNOWN(null), XZ("xz");

  public static EFileExtension fromName(final String name) {
    for (final EFileExtension extension : EFileExtension.values()) {
      final String extensionName = extension.getName();
      if ((extensionName == null && name == null) || extensionName != null && extensionName.equals(name)) {
        return extension;
      }
    }
    return EFileExtension.UNKNOWN;
  }

  private final String mName;

  private EFileExtension(final String name) {
    mName = name;
  }

  public String getName() {
    return mName;
  }
}
