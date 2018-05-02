package de.tischner.cobweb.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class WebUtil {
  private static final Charset STANDARD_CHARSET = StandardCharsets.UTF_8;

  public static String formatContentType(final EHttpContentType contentType) {
    switch (contentType) {
    case TEXT:
      return "text/plain";
    case HTML:
      return "text/html";
    case CSS:
      return "text/css";
    case JS:
      return "application/javascript";
    case JSON:
      return "application/json";
    case PNG:
      return "image/png";
    case JPG:
      return "image/jpeg";
    default:
      return null;
    }
  }

  public static int formatStatus(final EHttpStatus status) {
    switch (status) {
    case OK:
      return 200;
    case NO_CONTENT:
      return 204;
    case BAD_REQUEST:
      return 400;
    case FORBIDDEN:
      return 403;
    case NOT_FOUND:
      return 404;
    case INTERNAL_SERVER_ERROR:
      return 500;
    case NOT_IMPLEMENTED:
      return 501;
    default:
      return -1;
    }
  }

  /**
   * Sends the given answer with the given parameters to the given client by using
   * the HTTP/1.0 protocol.
   *
   * @param answerRaw   Answer to send as raw bytes
   * @param client      Client to send to
   * @param contentType Type of the content to send
   * @param status      The status of the answer to send
   * @throws IOException If an I/O-Exception occurred.
   */
  public static void sendHttpAnswer(final byte[] answerRaw, final EHttpContentType contentType,
      final EHttpStatus status, final Socket client) throws IOException {
    EHttpStatus statusToUse = status;
    byte[] answerRawToUse = answerRaw;

    String contentTypeText = WebUtil.formatContentType(contentType);
    if (contentTypeText == null) {
      contentTypeText = "text/plain";
      statusToUse = EHttpStatus.INTERNAL_SERVER_ERROR;
      // In case of an server error inside this method, don't send the intended
      // message. It might contain sensible data.
      answerRawToUse = "".getBytes(STANDARD_CHARSET);
    }

    int statusNumber = WebUtil.formatStatus(statusToUse);
    if (statusNumber == -1) {
      statusToUse = EHttpStatus.INTERNAL_SERVER_ERROR;
      statusNumber = 500;
      // In case of an server error inside this method, don't send the intended
      // message. It might contain sensible data.
      answerRawToUse = "".getBytes(STANDARD_CHARSET);
    }

    final String charset = STANDARD_CHARSET.displayName().toLowerCase();

    final String nextLine = "\r\n";
    final StringBuilder answer = new StringBuilder();
    answer.append("HTTP/1.0 " + statusNumber + " " + statusToUse + nextLine);
    answer.append("Content-Length: " + answerRawToUse.length + nextLine);
    answer.append("Content-Type: " + contentTypeText + "; charset=" + charset + nextLine);
    answer.append("Connection: close" + nextLine);
    answer.append(nextLine);

    try (final DataOutputStream output = new DataOutputStream(client.getOutputStream())) {
      output.write(answer.toString().getBytes(STANDARD_CHARSET));
      output.write(answerRawToUse);
    }
  }

  /**
   * Sends an empty answer with the given parameters to the given client by using
   * the HTTP/1.0 protocol.
   *
   * @param client      Client to send to
   * @param contentType Type of the content to send
   * @param status      The status of the answer to send
   * @throws IOException If an I/O-Exception occurred.
   */
  public static void sendHttpAnswer(final EHttpContentType contentType, final EHttpStatus status, final Socket client)
      throws IOException {
    WebUtil.sendHttpAnswer("", contentType, status, client);
  }

  /**
   * Sends only a status code with an empty answer to the given client by using
   * the HTTP/1.0 protocol.
   *
   * @param status The status of the answer to send
   * @param client Client to send to
   * @throws IOException If an I/O-Exception occurred.
   */
  public static void sendHttpAnswer(final EHttpStatus status, final Socket client) throws IOException {
    WebUtil.sendHttpAnswer(EHttpContentType.TEXT, status, client);
  }

  /**
   * Sends the given answer with the given parameters to the given client by using
   * the HTTP/1.0 protocol.
   *
   * @param answerText  Answer to send
   * @param client      Client to send to
   * @param contentType Type of the content to send
   * @param status      The status of the answer to send
   * @throws IOException If an I/O-Exception occurred.
   */
  public static void sendHttpAnswer(final String answerText, final EHttpContentType contentType,
      final EHttpStatus status, final Socket client) throws IOException {
    WebUtil.sendHttpAnswer(answerText.getBytes(STANDARD_CHARSET), contentType, status, client);
  }
}
