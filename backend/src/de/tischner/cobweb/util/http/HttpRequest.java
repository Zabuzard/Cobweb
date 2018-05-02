package de.tischner.cobweb.util.http;

import java.util.Map;

public final class HttpRequest {
  private final String mContent;
  private final Map<String, String> mHeaders;
  private final String mProtocol;
  private final String mResource;
  private final String mType;

  public HttpRequest(final String type, final String resource, final String protocol, final Map<String, String> headers,
      final String content) {
    mType = type;
    mResource = resource;
    mProtocol = protocol;
    mHeaders = headers;
    mContent = content;
  }

  public String getContent() {
    return mContent;
  }

  public Map<String, String> getHeaders() {
    return mHeaders;
  }

  public String getProtocol() {
    return mProtocol;
  }

  public String getResource() {
    return mResource;
  }

  public String getType() {
    return mType;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("HttpRequest [type=");
    builder.append(this.mType);
    builder.append(", resource=");
    builder.append(this.mResource);
    builder.append(", protocol=");
    builder.append(this.mProtocol);
    builder.append(", headers=");
    builder.append(this.mHeaders);
    builder.append(", content=");
    builder.append(this.mContent);
    builder.append("]");
    return builder.toString();
  }
}
