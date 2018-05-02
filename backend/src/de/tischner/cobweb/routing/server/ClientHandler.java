package de.tischner.cobweb.routing.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

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
import de.tischner.cobweb.util.EHttpContentType;
import de.tischner.cobweb.util.EHttpStatus;
import de.tischner.cobweb.util.WebUtil;

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
      try (InputStream is = mClient.getInputStream()) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        // TODO Debug problems with headers/body and end of stream
        // Read in all lines until an empty line or end of stream.
        // Do not wait for the end of the stream because that means the client has timed
        // out.
        final ArrayList<String> lines = new ArrayList<>();
        while (true) {
          final String line = reader.readLine();
          System.out.println("Read: " + line);
          if (line == null || line.isEmpty()) {
            break;
          }
          lines.add(line);
        }

        handleRequest(lines.iterator());
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

  private void handleRequest(final Iterator<String> lines) throws IOException {
    // Reject if empty
    if (!lines.hasNext()) {
      WebUtil.sendHttpAnswer(EHttpStatus.BAD_REQUEST, mClient);
      return;
    }

    final String request = lines.next().trim();

    // Reject if request is empty
    if (request.isEmpty()) {
      WebUtil.sendHttpAnswer(EHttpStatus.BAD_REQUEST, mClient);
      return;
    }

    final ArrayList<String> body = new ArrayList<>();
    lines.forEachRemaining(body::add);

    // Reject if empty body
    if (body.isEmpty()) {
      WebUtil.sendHttpAnswer(EHttpStatus.BAD_REQUEST, mClient);
      return;
    }

    // TODO Implement something meaningful
    final Instant handleRequestStart = Instant.now();
    System.out.println("Request is: " + request);
    System.out.println("Body is: " + body);
    final Instant handleRequestEnd = Instant.now();
    WebUtil.sendHttpAnswer("Looks good, took " + Duration.between(handleRequestEnd, handleRequestStart),
        EHttpContentType.TEXT, EHttpStatus.OK, mClient);
  }

}
