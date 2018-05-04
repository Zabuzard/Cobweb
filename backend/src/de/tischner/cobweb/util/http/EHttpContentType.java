package de.tischner.cobweb.util.http;

/**
 * Enumeration of valid HTTP/1.0 content types, also known as MIME types.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public enum EHttpContentType {
  /**
   * Type used for CSS stylesheet files.
   */
  CSS("text/css"),
  /**
   * Type used for HTML files.
   */
  HTML("text/html"),
  /**
   * Type for JPG image files.
   */
  JPG("image/jpeg"),
  /**
   * Type used for javascript files.
   */
  JS("application/javascript"),
  /**
   * Type used for json objects.
   */
  JSON("application/json"),
  /**
   * Type used for PNG image files.
   */
  PNG("image/png"),
  /**
   * Type for regular text files.
   */
  TEXT("text/plain");

  public static EHttpContentType fromTextValue(final String textValue) {
    for (final EHttpContentType contentType : EHttpContentType.values()) {
      if (contentType.getTextValue().equals(textValue)) {
        return contentType;
      }
    }
    return null;
  }

  private final String mTextValue;

  private EHttpContentType(final String textValue) {
    mTextValue = textValue;
  }

  public String getTextValue() {
    return mTextValue;
  }
}
