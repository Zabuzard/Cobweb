package de.tischner.cobweb.util.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class HttpUtil {
  private static final String HTTP_NEW_LINE = "\r\n";
  private static final Charset STANDARD_CHARSET = StandardCharsets.UTF_8;

  public static EHttpContentType parseContentType(final String value) {
    if (value == null) {
      return null;
    }
    final String[] data = value.split("; ", 2);
    return EHttpContentType.fromTextValue(data[0]);
  }

  public static HttpRequest parseRequest(final InputStream input) throws IOException {
    // Read the request
    String request;
    while (true) {
      final String line = HttpUtil.readHttpLine(input);
      // According to the specification empty lines that appear before any content
      // need to be rejected
      if (line.isEmpty()) {
        continue;
      }

      request = line;
      break;
    }
    // Parse the request
    final String[] requestData = request.trim().split(" ");
    final String type = requestData[0];
    final String resource = requestData[1];
    final String protocol = requestData[2];

    // Read and parse the headers
    final Map<String, String> headers = new HashMap<>();
    while (true) {
      final String line = HttpUtil.readHttpLine(input);
      // Break if content starts
      if (line.isEmpty()) {
        break;
      }

      final String[] data = line.split(": ", 2);
      headers.put(data[0], data[1]);
    }

    // Read and parse the content
    final String contentLengthText = headers.get("Content-Length");
    String content = "";
    if (contentLengthText != null && !contentLengthText.isEmpty() && contentLengthText != "0") {
      final int contentLength = Integer.parseInt(contentLengthText);
      content = HttpUtil.readHttpContent(contentLength, input);
    }

    return new HttpRequest(type, resource, protocol, headers, content);
  }

  /**
   * Sends the given response to the given client by using the HTTP/1.0 protocol.
   *
   * @param response The response to send
   * @param client   Client to send to
   * @throws IOException If an I/O-Exception occurred.
   */
  public static void sendHttpResponse(final HttpResponse response, final Socket client) throws IOException {
    final String charset = STANDARD_CHARSET.displayName().toLowerCase();
    final byte[] contentRaw = response.getContent().getBytes(STANDARD_CHARSET);

    // Build response type
    final String responseType = "HTTP/1.0 " + response.getStatus().getStatusCode() + " " + response.getStatus()
        + HTTP_NEW_LINE;

    // Build response headers
    final StringBuilder responseHeaders = new StringBuilder();
    responseHeaders.append("Content-Length: " + contentRaw.length + HTTP_NEW_LINE);
    responseHeaders
        .append("Content-Type: " + response.getContentType().getTextValue() + "; charset=" + charset + HTTP_NEW_LINE);

    // Set all given headers
    for (final Entry<String, String> entry : response.getHeaders().entrySet()) {
      responseHeaders.append(entry.getKey() + ": " + entry.getValue() + HTTP_NEW_LINE);
    }

    // Write headers and content
    try (final DataOutputStream output = new DataOutputStream(client.getOutputStream())) {
      output.write(responseType.getBytes(STANDARD_CHARSET));
      output.write(responseHeaders.toString().getBytes(STANDARD_CHARSET));
      output.write(HTTP_NEW_LINE.getBytes(STANDARD_CHARSET));
      output.write(contentRaw);
    }
  }

  private static String readHttpContent(final int contentLength, final InputStream input) throws IOException {
    final byte[] contentRawBuffer = new byte[contentLength];
    final int amountRead = input.read(contentRawBuffer);
    if (amountRead == contentRawBuffer.length) {
      return new String(contentRawBuffer, STANDARD_CHARSET);
    }
    final byte[] contentRaw = Arrays.copyOf(contentRawBuffer, amountRead);
    return new String(contentRaw, STANDARD_CHARSET);
  }

  private static String readHttpLine(final InputStream input) throws IOException {
    final ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();

    int valueBefore = 0;
    int value = 0;
    while (value != -1 && !(valueBefore == '\r' && value == '\n')) {
      valueBefore = value;
      value = input.read();

      if (value == '\r' || value == '\n') {
        continue;
      }

      byteArrayOutput.write(value);
    }

    if (value == -1 && byteArrayOutput.size() == 0) {
      return null;
    }

    return byteArrayOutput.toString(STANDARD_CHARSET.name());
  }
}
