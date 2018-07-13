package de.tischner.cobweb.routing.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import de.tischner.cobweb.db.IRoutingDatabase;
import de.tischner.cobweb.routing.algorithms.shortestpath.ShortestPathComputationFactory;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.IGetNodeById;
import de.tischner.cobweb.routing.server.model.RoutingRequest;
import de.tischner.cobweb.util.MemberFieldNamingStrategy;
import de.tischner.cobweb.util.http.EHttpContentType;
import de.tischner.cobweb.util.http.EHttpStatus;
import de.tischner.cobweb.util.http.HttpRequest;
import de.tischner.cobweb.util.http.HttpResponseBuilder;
import de.tischner.cobweb.util.http.HttpUtil;

/**
 * Class that handles a routing client. It is designed to communicate with a
 * client via HTTP and serve routing requests.<br>
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
   * Resource that is to be requested from a client if he submits a routing
   * query.
   */
  private static final String API_RESOURCE = "/route";
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);
  /**
   * The client to handle.
   */
  private final Socket mClient;
  /**
   * The factory to use for generating algorithms for shortest path computation.
   */
  private final ShortestPathComputationFactory mComputationFactory;
  /**
   * The database to use for fetching meta data for nodes and edges.
   */
  private final IRoutingDatabase mDatabase;
  /**
   * The unique ID of this client request.
   */
  private final int mId;
  /**
   * The object that provides nodes by their ID.
   */
  private final IGetNodeById<ICoreNode> mNodeProvider;

  /**
   * Creates a new handler which handles the given client using the given
   * tools.<br>
   * <br>
   * To handle the client call {@link #run()}. The method should only be called
   * once, the object should not be used anymore after the method has finished.
   *
   * @param id                 The unique ID of this client request
   * @param client             The client to handle
   * @param nodeProvider       The object that provides nodes by their ID
   * @param computationFactory The factory to use for generating algorithms for
   *                           shortest path computation
   * @param database           The database to use for fetching meta data for
   *                           nodes and edges
   */
  public ClientHandler(final int id, final Socket client, final IGetNodeById<ICoreNode> nodeProvider,
      final ShortestPathComputationFactory computationFactory, final IRoutingDatabase database) {
    mId = id;
    mClient = client;
    mNodeProvider = nodeProvider;
    mComputationFactory = computationFactory;
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
    LOGGER.info("Handling routing HTTP request with id: {}", mId);

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
   * Serves a HTTP request of type <tt>OPTIONS</tt>.
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
   * Serves a HTTP request of type <tt>POST</tt>.
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
      final RoutingRequest routingRequest = gson.fromJson(request.getContent(), RoutingRequest.class);
      final RequestHandler handler = new RequestHandler(mClient, gson, mNodeProvider, mComputationFactory, mDatabase);
      handler.handleRequest(routingRequest);
    } catch (final JsonSyntaxException e) {
      HttpUtil.sendHttpResponse(new HttpResponseBuilder().setStatus(EHttpStatus.BAD_REQUEST).build(), mClient);
      return;
    }
  }
}
