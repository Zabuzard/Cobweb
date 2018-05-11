package de.tischner.cobweb.util.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link HttpUtil}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class HttpUtilTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpUtil#parseContentType(java.lang.String)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testParseContentType() {
    Assert.assertEquals(EHttpContentType.HTML, HttpUtil.parseContentType("text/html; charset=UTF-8"));
    Assert.assertEquals(EHttpContentType.TEXT, HttpUtil.parseContentType("text/plain; charset=UTF-8"));
    Assert.assertEquals(EHttpContentType.TEXT, HttpUtil.parseContentType("text/plain; charset=UTF-8; foobar"));
    Assert.assertNull(HttpUtil.parseContentType("text/plaintest; charset=UTF-8"));
    Assert.assertNull(HttpUtil.parseContentType(""));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpUtil#parseRequest(java.io.InputStream)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testParseRequest() {
    final String httpNewline = "\r\n";
    final String content = "Hello World!";
    final byte[] contentRaw = content.getBytes(StandardCharsets.UTF_8);
    final StringBuilder sb = new StringBuilder(httpNewline);
    sb.append("GET /hello.html HTTP/1.1" + httpNewline);
    sb.append("Accept-Language: en-us" + httpNewline);
    sb.append("Content-Type: text/plaintest; charset=UTF-8" + httpNewline);
    sb.append("Content-Length: " + contentRaw.length + httpNewline);
    sb.append("Connection: Keep-Alive" + httpNewline);
    sb.append(httpNewline);

    final byte[] requestWithoutContentRaw = sb.toString().getBytes(StandardCharsets.UTF_8);
    final byte[] requestRaw = new byte[requestWithoutContentRaw.length + contentRaw.length];
    System.arraycopy(requestWithoutContentRaw, 0, requestRaw, 0, requestWithoutContentRaw.length);
    System.arraycopy(contentRaw, 0, requestRaw, requestWithoutContentRaw.length, contentRaw.length);

    final InputStream input = new ByteArrayInputStream(requestRaw);
    HttpRequest request = null;
    try {
      request = HttpUtil.parseRequest(input);
      Assert.assertEquals("GET", request.getType());
      Assert.assertEquals("/hello.html", request.getResource());
      Assert.assertEquals("HTTP/1.1", request.getProtocol());

      final Map<String, String> headers = request.getHeaders();
      Assert.assertEquals(4, headers.size());
      Assert.assertEquals("en-us", headers.get("Accept-Language"));
      Assert.assertEquals("text/plaintest; charset=UTF-8", headers.get("Content-Type"));
      Assert.assertEquals(String.valueOf(contentRaw.length), headers.get("Content-Length"));
      Assert.assertEquals("Keep-Alive", headers.get("Connection"));

      Assert.assertEquals(content, request.getContent());
    } catch (final IOException e) {
      Assert.fail();
    }
  }

}
