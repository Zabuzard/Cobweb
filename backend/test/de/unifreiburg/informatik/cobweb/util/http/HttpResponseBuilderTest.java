package de.unifreiburg.informatik.cobweb.util.http;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link HttpResponseBuilder}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class HttpResponseBuilderTest {

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.http.HttpResponseBuilder#build()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testBuild() {
    final HttpResponse response = new HttpResponseBuilder().build();
    Assert.assertEquals("", response.getContent());
    Assert.assertEquals(EHttpStatus.OK, response.getStatus());
    Assert.assertEquals(EHttpContentType.TEXT, response.getContentType());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.http.HttpResponseBuilder#HttpResponseBuilder()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testHttpResponseBuilder() {
    try {
      new HttpResponseBuilder();
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.http.HttpResponseBuilder#putHeader(java.lang.String, java.lang.String)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testPutHeader() {
    final HttpResponse response = new HttpResponseBuilder().putHeader("foo", "bar").putHeader("hello", "world").build();
    final Map<String, String> headers = response.getHeaders();
    Assert.assertEquals("bar", headers.get("foo"));
    Assert.assertEquals("world", headers.get("hello"));
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.http.HttpResponseBuilder#setContent(java.lang.String)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testSetContent() {
    final HttpResponse response = new HttpResponseBuilder().setContent("Hello World!").build();
    Assert.assertEquals("Hello World!", response.getContent());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.http.HttpResponseBuilder#setContentType(de.unifreiburg.informatik.cobweb.util.http.EHttpContentType)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testSetContentType() {
    final HttpResponse response = new HttpResponseBuilder().setContentType(EHttpContentType.HTML).build();
    Assert.assertEquals(EHttpContentType.HTML, response.getContentType());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.util.http.HttpResponseBuilder#setStatus(de.unifreiburg.informatik.cobweb.util.http.EHttpStatus)}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testSetStatus() {
    final HttpResponse response = new HttpResponseBuilder().setStatus(EHttpStatus.METHOD_NOT_ALLOWED).build();
    Assert.assertEquals(EHttpStatus.METHOD_NOT_ALLOWED, response.getStatus());
  }

}
