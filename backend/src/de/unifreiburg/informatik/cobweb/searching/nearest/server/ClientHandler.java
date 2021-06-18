package de.unifreiburg.informatik.cobweb.searching.nearest.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import de.unifreiburg.informatik.cobweb.db.INearestSearchDatabase;
import de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.INearestNeighborComputation;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.searching.nearest.server.model.NearestSearchRequest;
import de.unifreiburg.informatik.cobweb.util.MemberFieldNamingStrategy;
import de.unifreiburg.informatik.cobweb.util.http.EHttpContentType;
import de.unifreiburg.informatik.cobweb.util.http.EHttpStatus;
import de.unifreiburg.informatik.cobweb.util.http.HttpRequest;
import de.unifreiburg.informatik.cobweb.util.http.HttpResponseBuilder;
import de.unifreiburg.informatik.cobweb.util.http.HttpUtil;

/**
 * Class that handles a nearest search client. It is designed to communicate
 * with a client via HTTP and serve nearest search requests.<br>
 * <br>
 * To handle the client call {@link #run()}. The method should only be called
 * once, the object should not be used anymore after the method has
 * finished.<br>
 * <br>
 * The client handler will close the client on its own. So it should not be
 * closed outside.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ClientHandler implements Runnable {
  /**
   * Resource that is to be requested from a client if he submits a nearest
   * search query.
   */
  private static final String API_RESOURCE = "/nearestsearch";
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);
  /**
   * The client to handle.
   */
  private final Socket mClient;
  /**
   * The database to use for retrieving node data.
   */
  private final INearestSearchDatabase mDatabase;
  /**
   * The unique ID of this client request.
   */
  private final int mId;
  /**
   * The nearest neighbor computation algorithm to use.
   */
  private final INearestNeighborComputation<ICoreNode> mNearestNeighborComputation;

  /**
   * Creates a new handler which handles the given client using the given
   * algorithm.<br>
   * <br>
   * To handle the client call {@link #run()}. The method should only be called
   * once, the object should not be used anymore after the method has finished.
   *
   * @param id                         The unique ID of this client request
   * @param client                     The client to handle
   * @param nearestNeighborComputation Nearest neighbor computation algorithm to
   *                                   use
   * @param database                   The database to use for retrieving node
   *                                   data
   */
  public ClientHandler(final int id, final Socket client,
      final INearestNeighborComputation<ICoreNode> nearestNeighborComputation, final INearestSearchDatabase database) {
    mId = id;
    mClient = client;
    mNearestNeighborComputation = nearestNeighborComputation;
    mDatabase = database;
  }

  /**
   * Handles the clients request.<br>
   * <br>
   * The method should only be called once, the object should not be used
   * anymore after the method has finished. The method catches and logs all
   * errors and exceptions.<br>
   * <br>
   * The client handler will close the client on its own. So it should not be
   * closed outside.
   */
  @Override
  public void run() {
    try {
      // Handle the client
      try (InputStream input = mClient.getInputStream()) {
        handleRequest(HttpUtil.parseRequest(input));
      }
    } catch (final Throwable e) {
      // Log every error
      LOGGER.error("Unknown error while handling the client: {}", mId, e);
    } finally {
      try {
        // Close the client
        mClient.close();
      } catch (final IOException e) {
        LOGGER.error("Exception while closing the client socket", e);
      }
    }
  }

  /**
   * Handles the given HTTP request.
   *
   * @param request The request to handle
   * @throws IOException If an I/O exception occurred while sending the response
   *                     to the client
   */
  private void handleRequest(final HttpRequest request) throws IOException {
    // TODO Maybe don't log always
    LOGGER.info("Handling nearest search HTTP request with id: {}", mId);

    // Method not allowed
    final String type = request.getType().toUpperCase();
    if (!type.equals("OPTIONS") && !type.equals("POST")) {
      HttpUtil.sendHttpResponse(new HttpResponseBuilder().setStatus(EHttpStatus.METHOD_NOT_ALLOWED)
          .putHeader("Allow", "OPTIONS, POST").build(), mClient);
      return;
    }

    if (type.equals("OPTIONS")) {
      serveOptionsRequest();
      return;
    }

    // Type is a post request
    servePost(request);
  }

  /**
   * Serves a HTTP request of type <code>OPTIONS</code>.
   *
   * @throws IOException If an I/O exception occurred while sending the response
   *                     to the client
   */
  private void serveOptionsRequest() throws IOException {
    // Send back the supported methods
    HttpUtil.sendHttpResponse(new HttpResponseBuilder().setStatus(EHttpStatus.OK)
        .putHeader("Access-Control-Allow-Methods", "POST").putHeader("Access-Control-Allow-Headers", "Content-Type")
        .putHeader("Access-Control-Max-Age", String.valueOf(86400)).putHeader("Connection", "Keep-Alive")
        .putHeader("Keep-Alive", "timeout=5, max=100").build(), mClient);
  }

  /**
   * Serves a HTTP request of type <code>POST</code>.
   *
   * @param request The request to serve
   * @throws IOException If an I/O exception occurred while sending the response
   *                     to the client
   */
  private void servePost(final HttpRequest request) throws IOException {
    if (!request.getResource().equals(API_RESOURCE)) {
      HttpUtil.sendHttpResponse(new HttpResponseBuilder().setStatus(EHttpStatus.NOT_IMPLEMENTED).build(), mClient);
      return;
    }

    final EHttpContentType contentType = HttpUtil.parseContentType(request.getHeaders().get("Content-Type"));
    if (contentType == null || contentType != EHttpContentType.JSON) {
      HttpUtil.sendHttpResponse(new HttpResponseBuilder().setStatus(EHttpStatus.BAD_REQUEST).build(), mClient);
      return;
    }

    // Parse the JSON request and handle it
    final Gson gson = new GsonBuilder().setFieldNamingStrategy(new MemberFieldNamingStrategy()).create();
    try {
      final NearestSearchRequest nearestSearchRequest = gson.fromJson(request.getContent(), NearestSearchRequest.class);
      final RequestHandler handler = new RequestHandler(mClient, gson, mNearestNeighborComputation, mDatabase);
      handler.handleRequest(nearestSearchRequest);
    } catch (final JsonSyntaxException e) {
      HttpUtil.sendHttpResponse(new HttpResponseBuilder().setStatus(EHttpStatus.BAD_REQUEST).build(), mClient);
      return;
    }
  }
}
