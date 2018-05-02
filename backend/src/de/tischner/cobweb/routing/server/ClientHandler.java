package de.tischner.cobweb.routing.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.db.IRoutingDatabase;
import de.tischner.cobweb.routing.algorithms.shortestpath.IShortestPathComputation;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById;
import de.tischner.cobweb.routing.model.graph.road.IHasId;
import de.tischner.cobweb.routing.model.graph.road.ISpatial;
import de.tischner.cobweb.util.http.EHttpStatus;
import de.tischner.cobweb.util.http.HttpRequest;
import de.tischner.cobweb.util.http.HttpResponseBuilder;
import de.tischner.cobweb.util.http.HttpUtil;

public final class ClientHandler<N extends INode & IHasId & ISpatial, E extends IEdge<N> & IHasId, G extends IGraph<N, E> & ICanGetNodeById<N>>
    implements Runnable {

  private final static Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);
  private final Socket mClient;
  private final IShortestPathComputation<N, E> mComputation;
  private final IRoutingDatabase mDatabase;
  private final G mGraph;
  private final int mId;

  public ClientHandler(final int id, final Socket client, final G graph,
      final IShortestPathComputation<N, E> computation, final IRoutingDatabase database) {
    mId = id;
    mClient = client;
    mGraph = graph;
    mComputation = computation;
    mDatabase = database;
  }

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

  private void handleRequest(final HttpRequest request) throws IOException {
    // Method not allowed
    final String type = request.getType().toUpperCase();
    if (!type.equals("OPTIONS") && !type.equals("POST")) {
      HttpUtil.sendHttpResponse(new HttpResponseBuilder().setStatus(EHttpStatus.METHOD_NOT_ALLOWED)
          .putHeader("Allow", "OPTIONS, POST").build(), mClient);
      return;
    }

    if (type.equals("OPTIONS")) {
      serveOptionsRequest(request);
      return;
    }

    // Type is a post request
    servePost(request);
  }

  private void serveOptionsRequest(final HttpRequest request) throws IOException {
    // Send back the supported methods
    HttpUtil.sendHttpResponse(new HttpResponseBuilder().setStatus(EHttpStatus.OK)
        .putHeader("Access-Control-Allow-Methods", "POST").putHeader("Access-Control-Allow-Headers", "Content-Type")
        .putHeader("Access-Control-Max-Age", String.valueOf(86400)).putHeader("Connection", "Keep-Alive")
        .putHeader("Keep-Alive", "timeout=5, max=100").build(), mClient);
  }

  private void servePost(final HttpRequest request) throws IOException {
    // TODO Implement something meaningful
    System.out.println(request);

    HttpUtil.sendHttpResponse(new HttpResponseBuilder().setStatus(EHttpStatus.OK).setContent("Hello world").build(),
        mClient);
  }

}
