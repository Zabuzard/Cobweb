package de.unifreiburg.informatik.cobweb.util.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder used to build instances of {@link HttpResponse}.<br>
 * <br>
 * Creating a new instance of this builder will use default values for the
 * response that represent a valid response with empty text. Use the setter
 * methods to set different values and finally construct an instance using
 * {@link #build()}. Most methods return the builder instance which allows a
 * cascade style of using the class like
 * {@code new HttpResponseBuilder().setContent("Hello World").build()}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class HttpResponseBuilder {
  /**
   * The content of the HTTP response to build.
   */
  private String mContent;
  /**
   * The content type of the response to build.
   */
  private EHttpContentType mContentType;
  /**
   * A map connecting HTTP headers to their values.
   */
  private final Map<String, String> mHeaders;
  /**
   * The HTTP status of the response to build.
   */
  private EHttpStatus mStatus;

  /**
   * Creates a new HTTP response builder with default values for a valid
   * response with an empty text.<br>
   * <br>
   * Use the setter methods to set different values and finally construct an
   * instance using {@link #build()}.
   */
  public HttpResponseBuilder() {
    mContent = "";
    mStatus = EHttpStatus.OK;
    mContentType = EHttpContentType.TEXT;
    mHeaders = new HashMap<>();
    mHeaders.put("Access-Control-Allow-Origin", "*");
    mHeaders.put("Connection", "close");
  }

  /**
   * Builds an instance of a {@link HttpResponse} with the values currently set
   * in this builder.
   *
   * @return An instance of a response with the current set values
   */
  public HttpResponse build() {
    return new HttpResponse(mContent, mContentType, mStatus, mHeaders);
  }

  /**
   * Puts the given value to the given header key.
   *
   * @param key   The header key to set a value for
   * @param value The value to set
   * @return The builder instance
   */
  public HttpResponseBuilder putHeader(final String key, final String value) {
    mHeaders.put(key, value);
    return this;
  }

  /**
   * Sets the content of the response to build.
   *
   * @param content The content to set
   * @return The builder instance
   */
  public HttpResponseBuilder setContent(final String content) {
    mContent = content;
    return this;
  }

  /**
   * Sets the content type of the response to build.
   *
   * @param contentType The content type to set
   * @return The builder instance
   */
  public HttpResponseBuilder setContentType(final EHttpContentType contentType) {
    mContentType = contentType;
    return this;
  }

  /**
   * Sets the HTTP status of the response to build.
   *
   * @param status The status to set
   * @return The builder instance
   */
  public HttpResponseBuilder setStatus(final EHttpStatus status) {
    mStatus = status;
    return this;
  }
}
