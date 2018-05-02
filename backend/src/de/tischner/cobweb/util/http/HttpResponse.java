package de.tischner.cobweb.util.http;

import java.util.Map;

public final class HttpResponse {

  private final String mContent;

  private final EHttpContentType mContentType;
  private final Map<String, String> mHeaders;
  private final EHttpStatus mStatus;

  HttpResponse(final String content, final EHttpContentType contentType, final EHttpStatus status,
      final Map<String, String> headers) {
    mContent = content;
    mContentType = contentType;
    mStatus = status;
    mHeaders = headers;
  }

  public String getContent() {
    return mContent;
  }

  public EHttpContentType getContentType() {
    return mContentType;
  }

  public Map<String, String> getHeaders() {
    return mHeaders;
  }

  public EHttpStatus getStatus() {
    return mStatus;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("HttpResponse [status=");
    builder.append(this.mStatus);
    builder.append(", contentType=");
    builder.append(this.mContentType);
    builder.append(", headers=");
    builder.append(this.mHeaders);
    builder.append(", content=");
    builder.append(this.mContent);
    builder.append("]");
    return builder.toString();
  }
}
