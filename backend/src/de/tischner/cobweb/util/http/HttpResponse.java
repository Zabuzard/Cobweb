package de.tischner.cobweb.util.http;

import java.util.Map;

/**
 * POJO that models a HTTP response.<br>
 * <br>
 * The class {@link HttpResponseBuilder} can be used to easily build instances
 * of this class, especially if values should receive default values.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class HttpResponse {
  /**
   * The content of the response, interpreted as string.
   */
  private final String mContent;
  /**
   * The type of the content.
   */
  private final EHttpContentType mContentType;
  /**
   * A map connecting HTTP headers to their values.
   */
  private final Map<String, String> mHeaders;
  /**
   * The HTTP status of the response.
   */
  private final EHttpStatus mStatus;

  /**
   * Creates a new HTTP response with the given values.
   *
   * @param content     The content of the response, interpreted as string
   * @param contentType The type of the content
   * @param status      The HTTP status of the response
   * @param headers     A map connecting HTTP headers to their values
   */
  public HttpResponse(final String content, final EHttpContentType contentType, final EHttpStatus status,
      final Map<String, String> headers) {
    mContent = content;
    mContentType = contentType;
    mStatus = status;
    mHeaders = headers;
  }

  /**
   * Gets the content of the response, interpreted as string.
   *
   * @return The content of the response
   */
  public String getContent() {
    return mContent;
  }

  /**
   * Gets the type of the content.
   *
   * @return The type of the content
   */
  public EHttpContentType getContentType() {
    return mContentType;
  }

  /**
   * Gets the HTTP headers of the response
   *
   * @return A map connecting HTTP headers to their values
   */
  public Map<String, String> getHeaders() {
    return mHeaders;
  }

  /**
   * Gets the HTTP status of the response.
   *
   * @return The HTTP status of the response
   */
  public EHttpStatus getStatus() {
    return mStatus;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("HttpResponse [status=");
    builder.append(mStatus);
    builder.append(", contentType=");
    builder.append(mContentType);
    builder.append(", headers=");
    builder.append(mHeaders);
    builder.append(", content=");
    builder.append(mContent);
    builder.append("]");
    return builder.toString();
  }
}
