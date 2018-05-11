package de.tischner.cobweb.util.http;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link HttpResponse}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public class HttpResponseTest {
  /**
   * The response used for testing.
   */
  private HttpResponse mResponse;

  /**
   * Setups a response instance for testing.
   */
  @Before
  public void setUp() {
    final String content = "Hello World!";

    final Map<String, String> headers = new HashMap<>();
    headers.put("Content-Length", String.valueOf(content.getBytes(StandardCharsets.UTF_8).length));
    headers.put("Connection", "Keep-Alive");

    mResponse = new HttpResponse(content, EHttpContentType.TEXT, EHttpStatus.OK, headers);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpResponse#getContent()}.
   */
  @Test
  public void testGetContent() {
    Assert.assertEquals("Hello World!", mResponse.getContent());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpResponse#getContentType()}.
   */
  @Test
  public void testGetContentType() {
    Assert.assertEquals(EHttpContentType.TEXT, mResponse.getContentType());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpResponse#getHeaders()}.
   */
  @Test
  public void testGetHeaders() {
    final Map<String, String> headers = mResponse.getHeaders();
    Assert.assertEquals(2, headers.size());
    Assert.assertEquals(String.valueOf("Hello World!".getBytes(StandardCharsets.UTF_8).length),
        headers.get("Content-Length"));
    Assert.assertEquals("Keep-Alive", headers.get("Connection"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpResponse#getStatus()}.
   */
  @Test
  public void testGetStatus() {
    Assert.assertEquals(EHttpStatus.OK, mResponse.getStatus());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpResponse#HttpResponse(java.lang.String, de.tischner.cobweb.util.http.EHttpContentType, de.tischner.cobweb.util.http.EHttpStatus, java.util.Map)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testHttpResponse() {
    final String content = "Hello World!";

    final Map<String, String> headers = new HashMap<>();
    headers.put("Content-Length", String.valueOf(content.getBytes(StandardCharsets.UTF_8).length));
    headers.put("Connection", "Keep-Alive");

    try {
      new HttpResponse(content, EHttpContentType.TEXT, EHttpStatus.OK, headers);
      new HttpResponse("a", EHttpContentType.HTML, EHttpStatus.METHOD_NOT_ALLOWED,
          Collections.singletonMap("foo", "bar"));
      new HttpResponse("", EHttpContentType.TEXT, EHttpStatus.OK, Collections.emptyMap());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
