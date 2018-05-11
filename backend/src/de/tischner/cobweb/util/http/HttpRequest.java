package de.tischner.cobweb.util.http;

import java.util.Map;

/**
 * POJO that models a HTTP request.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class HttpRequest {
  /**
   * The content of the request, interpreted as string.
   */
  private final String mContent;
  /**
   * A map connecting HTTP headers to their values.
   */
  private final Map<String, String> mHeaders;
  /**
   * The protocol the HTTP request uses.
   */
  private final String mProtocol;
  /**
   * The resource the HTTP request requests.
   */
  private final String mResource;
  /**
   * The type of the request.
   */
  private final String mType;

  /**
   * Creates a new HTTP request with the given values.
   *
   * @param type     The type of the request, like <tt>POST</tt>
   * @param resource The resource the request requests
   * @param protocol The protocol used by the request
   * @param headers  A map connecting HTTP headers to their values
   * @param content  The content of the request, interpreted as string
   */
  public HttpRequest(final String type, final String resource, final String protocol, final Map<String, String> headers,
      final String content) {
    mType = type;
    mResource = resource;
    mProtocol = protocol;
    mHeaders = headers;
    mContent = content;
  }

  /**
   * Gets the content of the request, interpreted as string.
   *
   * @return The content of the request
   */
  public String getContent() {
    return mContent;
  }

  /**
   * Gets the HTTP headers of the request.
   *
   * @return A map connecting HTTP headers to their values
   */
  public Map<String, String> getHeaders() {
    return mHeaders;
  }

  /**
   * Gets the protocol used by the request.
   *
   * @return The protocol used by the request
   */
  public String getProtocol() {
    return mProtocol;
  }

  /**
   * Gets the resource the request requests.
   *
   * @return The requested resource
   */
  public String getResource() {
    return mResource;
  }

  /**
   * Gets the type of the HTTP request.
   *
   * @return The type of the request
   */
  public String getType() {
    return mType;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("HttpRequest [type=");
    builder.append(mType);
    builder.append(", resource=");
    builder.append(mResource);
    builder.append(", protocol=");
    builder.append(mProtocol);
    builder.append(", headers=");
    builder.append(mHeaders);
    builder.append(", content=");
    builder.append(mContent);
    builder.append("]");
    return builder.toString();
  }
}
