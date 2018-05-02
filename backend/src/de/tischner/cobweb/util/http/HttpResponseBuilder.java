package de.tischner.cobweb.util.http;

import java.util.HashMap;
import java.util.Map;

public final class HttpResponseBuilder {
  private String mContent;
  private EHttpContentType mContentType;
  private final Map<String, String> mHeaders;
  private EHttpStatus mStatus;

  public HttpResponseBuilder() {
    mContent = "";
    mStatus = EHttpStatus.OK;
    mContentType = EHttpContentType.TEXT;
    mHeaders = new HashMap<>();
    mHeaders.put("Access-Control-Allow-Origin", "*");
    mHeaders.put("Connection", "close");
  }

  public HttpResponse build() {
    return new HttpResponse(mContent, mContentType, mStatus, mHeaders);
  }

  public HttpResponseBuilder putHeader(final String key, final String value) {
    mHeaders.put(key, value);
    return this;
  }

  public HttpResponseBuilder setContent(final String content) {
    mContent = content;
    return this;
  }

  public HttpResponseBuilder setContentType(final EHttpContentType contentType) {
    mContentType = contentType;
    return this;
  }

  public HttpResponseBuilder setStatus(final EHttpStatus status) {
    mStatus = status;
    return this;
  }
}
