package de.tischner.cobweb.util.http;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the class {@link HttpRequest}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class HttpRequestTest {
  /**
   * The request used for testing.
   */
  private HttpRequest mRequest;

  /**
   * Setups a request instance for testing.
   */
  @Before
  public void setUp() {
    final String content = "Hello World!";

    final Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "text/plain");
    headers.put("Content-Length", String.valueOf(content.getBytes(StandardCharsets.UTF_8).length));
    headers.put("Connection", "Keep-Alive");

    mRequest = new HttpRequest("GET", "/hello.html", "HTTP/1.1", headers, content);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpRequest#getContent()}.
   */
  @Test
  public void testGetContent() {
    Assert.assertEquals("Hello World!", mRequest.getContent());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpRequest#getHeaders()}.
   */
  @Test
  public void testGetHeaders() {
    final Map<String, String> headers = mRequest.getHeaders();
    Assert.assertEquals(3, headers.size());
    Assert.assertEquals("text/plain", headers.get("Content-Type"));
    Assert.assertEquals(String.valueOf("Hello World!".getBytes(StandardCharsets.UTF_8).length),
        headers.get("Content-Length"));
    Assert.assertEquals("Keep-Alive", headers.get("Connection"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpRequest#getProtocol()}.
   */
  @Test
  public void testGetProtocol() {
    Assert.assertEquals("HTTP/1.1", mRequest.getProtocol());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpRequest#getResource()}.
   */
  @Test
  public void testGetResource() {
    Assert.assertEquals("/hello.html", mRequest.getResource());
  }

  /**
   * Test method for {@link de.tischner.cobweb.util.http.HttpRequest#getType()}.
   */
  @Test
  public void testGetType() {
    Assert.assertEquals("GET", mRequest.getType());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.util.http.HttpRequest#HttpRequest(java.lang.String, java.lang.String, java.lang.String, java.util.Map, java.lang.String)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testHttpRequest() {
    final String content = "Hello World!";

    final Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "text/plain");
    headers.put("Content-Length", String.valueOf(content.getBytes(StandardCharsets.UTF_8).length));
    headers.put("Connection", "Keep-Alive");

    try {
      new HttpRequest("GET", "/hello.html", "HTTP/1.1", headers, content);
      new HttpRequest("POST", "/", "HTTP/1.5", Collections.singletonMap("foo", "bar"), "");
      new HttpRequest("", "", "", Collections.emptyMap(), "");
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
