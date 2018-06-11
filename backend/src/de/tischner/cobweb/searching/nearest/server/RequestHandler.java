package de.tischner.cobweb.searching.nearest.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.tischner.cobweb.db.INearestSearchDatabase;
import de.tischner.cobweb.routing.algorithms.nearestneighbor.INearestNeighborComputation;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.tischner.cobweb.searching.nearest.server.model.NearestSearchRequest;
import de.tischner.cobweb.searching.nearest.server.model.NearestSearchResponse;
import de.tischner.cobweb.util.RoutingUtil;
import de.tischner.cobweb.util.http.EHttpContentType;
import de.tischner.cobweb.util.http.HttpResponseBuilder;
import de.tischner.cobweb.util.http.HttpUtil;

/**
 * Class that handles a nearest search request. It parses the request, computes
 * corresponding matches and builds and sends a proper response.<br>
 * <br>
 * To handle a request call {@link #handleRequest(NearestSearchRequest)}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RequestHandler {
  /**
   * Logger used for logging
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
  /**
   * The client whose request to handle.
   */
  private final Socket mClient;
  /**
   * The database to use for retrieving node data.
   */
  private final INearestSearchDatabase mDatabase;
  /**
   * The GSON object used to format JSON responses.
   */
  private final Gson mGson;
  /**
   * The nearest neighbor computation algorithm to use.
   */
  private final INearestNeighborComputation<ICoreNode> mNearestNeighborComputation;

  /**
   * Creates a new handler which handles requests of the given client using the
   * given algorithm.<br>
   * <br>
   * To handle a request call {@link #handleRequest(NearestSearchRequest)}.
   *
   * @param client                     The client whose request to handle
   * @param gson                       The GSON object used to format JSON
   *                                   responses
   * @param nearestNeighborComputation Nearest neighbor computation algorithm to
   *                                   use
   * @param database                   The database to use for retrieving node
   *                                   data
   */
  public RequestHandler(final Socket client, final Gson gson,
      final INearestNeighborComputation<ICoreNode> nearestNeighborComputation, final INearestSearchDatabase database) {
    mClient = client;
    mGson = gson;
    mNearestNeighborComputation = nearestNeighborComputation;
    mDatabase = database;
  }

  /**
   * Handles the given nearest search request. It computes the nearest node and
   * constructs and sends a proper response.
   *
   * @param request The request to handle
   * @throws IOException If an I/O exception occurred while sending a response
   */
  public void handleRequest(final NearestSearchRequest request) throws IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Handling request: {}", request);
    }
    final long startTime = System.nanoTime();

    final RoadNode wrapperRequestNode = new RoadNode(-1, request.getLatitude(), request.getLongitude());
    final Optional<ICoreNode> possibleNearestNode = mNearestNeighborComputation.getNearestNeighbor(wrapperRequestNode);
    if (!possibleNearestNode.isPresent()) {
      sendEmptyResponse(startTime);
      return;
    }
    final ICoreNode nearestNode = possibleNearestNode.get();
    final Optional<Long> possibleId = mDatabase.getOsmNodeByInternal(nearestNode.getId());
    if (!possibleId.isPresent()) {
      sendEmptyResponse(startTime);
      return;
    }
    final long id = possibleId.get();

    final long endTime = System.nanoTime();

    // Build and send response
    final NearestSearchResponse response = new NearestSearchResponse(RoutingUtil.nanosToMillis(endTime - startTime), id,
        nearestNode.getLatitude(), nearestNode.getLongitude());
    sendResponse(response);
  }

  /**
   * Sends an empty nearest search response. This is usually used if no nearest
   * node could be found.
   *
   * @param startTime The time the computation started, in nanoseconds. Must be
   *                  compatible with {@link System#nanoTime()}.
   * @throws IOException If an I/O exception occurred while sending the response
   */
  private void sendEmptyResponse(final long startTime) throws IOException {
    final long endTime = System.nanoTime();
    final NearestSearchResponse response =
        new NearestSearchResponse(RoutingUtil.nanosToMillis(endTime - startTime), -1L, 0.0f, 0.0f);
    sendResponse(response);
  }

  /**
   * Sends the given nearest search response.
   *
   * @param response The response to send
   * @throws IOException If an I/O exception occurred while sending the response
   */
  private void sendResponse(final NearestSearchResponse response) throws IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending response: {}", response);
    }
    final String content = mGson.toJson(response);
    HttpUtil.sendHttpResponse(
        new HttpResponseBuilder().setContentType(EHttpContentType.JSON).setContent(content).build(), mClient);
  }
}
