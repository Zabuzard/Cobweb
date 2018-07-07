package de.tischner.cobweb.routing.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.tischner.cobweb.db.IRoutingDatabase;
import de.tischner.cobweb.routing.algorithms.shortestpath.EdgePath;
import de.tischner.cobweb.routing.algorithms.shortestpath.IShortestPathComputation;
import de.tischner.cobweb.routing.algorithms.shortestpath.ShortestPathComputationFactory;
import de.tischner.cobweb.routing.model.graph.ETransportationMode;
import de.tischner.cobweb.routing.model.graph.EdgeCost;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGetNodeById;
import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.IHasTransportationMode;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.IPath;
import de.tischner.cobweb.routing.model.graph.ISpatial;
import de.tischner.cobweb.routing.model.graph.SpeedTransportationModeComparator;
import de.tischner.cobweb.routing.model.graph.road.IRoadNode;
import de.tischner.cobweb.routing.server.model.ERouteElementType;
import de.tischner.cobweb.routing.server.model.Journey;
import de.tischner.cobweb.routing.server.model.RouteElement;
import de.tischner.cobweb.routing.server.model.RoutingRequest;
import de.tischner.cobweb.routing.server.model.RoutingResponse;
import de.tischner.cobweb.util.RoutingUtil;
import de.tischner.cobweb.util.http.EHttpContentType;
import de.tischner.cobweb.util.http.HttpResponseBuilder;
import de.tischner.cobweb.util.http.HttpUtil;

/**
 * Class that handles a routing request. It parses the request, computes
 * corresponding shortest paths and builds and sends a proper response.<br>
 * <br>
 * To handle a request call {@link #handleRequest(RoutingRequest)}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 */
public final class RequestHandler<N extends INode & IHasId & ISpatial, E extends IEdge<N> & IHasId> {
  /**
   * Logger used for logging
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
  /**
   * The client whose request to handle.
   */
  private final Socket mClient;
  /**
   * The factory to use for generating algorithms for shortest path computation.
   */
  private final ShortestPathComputationFactory<N, E> mComputationFactory;
  /**
   * The database to use for fetching meta data for nodes and edges.
   */
  private final IRoutingDatabase mDatabase;
  /**
   * The GSON object used to format JSON responses.
   */
  private final Gson mGson;
  /**
   * The object that provides nodes by their ID.
   */
  private final IGetNodeById<N> mNodeProvider;
  /**
   * Comparator that sorts transportation modes ascending in their speed.
   */
  private final SpeedTransportationModeComparator mSpeedComparator;

  /**
   * Creates a new handler which handles requests of the given client using the
   * given tools.<br>
   * <br>
   * To handle a request call {@link #handleRequest(RoutingRequest)}.
   *
   * @param client             The client whose request to handle
   * @param gson               The GSON object used to format JSON responses
   * @param nodeProvider       The object that provides nodes by their ID
   * @param computationFactory The factory to use for generating algorithms for
   *                           shortest path computation
   * @param database           The database to use for fetching meta data for
   *                           nodes and edges
   */
  public RequestHandler(final Socket client, final Gson gson, final IGetNodeById<N> nodeProvider,
      final ShortestPathComputationFactory<N, E> computationFactory, final IRoutingDatabase database) {
    mClient = client;
    mGson = gson;
    mNodeProvider = nodeProvider;
    mComputationFactory = computationFactory;
    mDatabase = database;
    mSpeedComparator = new SpeedTransportationModeComparator();
  }

  /**
   * Handles the given routing request. It computes shortest paths and
   * constructs and sends a proper response.
   *
   * @param request The request to handle
   * @throws IOException If an I/O exception occurred while sending a response
   */
  public void handleRequest(final RoutingRequest request) throws IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Handling request: {}", request);
    }
    final long startTime = System.nanoTime();

    // Get the source and destination
    final Optional<N> sourceOptional =
        mDatabase.getInternalNodeByOsm(request.getFrom()).flatMap(id -> mNodeProvider.getNodeById(id));
    if (!sourceOptional.isPresent()) {
      sendEmptyResponse(request, startTime);
      return;
    }
    final Optional<N> destinationOptional =
        mDatabase.getInternalNodeByOsm(request.getTo()).flatMap(id -> mNodeProvider.getNodeById(id));
    if (!destinationOptional.isPresent()) {
      sendEmptyResponse(request, startTime);
      return;
    }

    // Nodes are known, compute the path
    final N source = sourceOptional.get();
    final N destination = destinationOptional.get();

    final IShortestPathComputation<N, E> computation =
        mComputationFactory.createAlgorithm(request.getDepTime(), request.getModes());

    final long startCompTime = System.nanoTime();
    final Optional<IPath<N, E>> pathOptional = computation.computeShortestPath(source, destination);
    final long endCompTime = System.nanoTime();
    if (!pathOptional.isPresent()) {
      sendNotReachableResponse(request, startTime, startCompTime);
      return;
    }

    // Path is present, build the resulting journey
    final IPath<N, E> path = pathOptional.get();
    final Journey journey = buildJourney(request, path);

    final long endTime = System.nanoTime();

    // Build and send response
    final RoutingResponse response = new RoutingResponse(RoutingUtil.nanosToMillis(endTime - startTime),
        RoutingUtil.nanosToMillis(endCompTime - startCompTime), request.getFrom(), request.getTo(),
        Collections.singletonList(journey));
    sendResponse(response);
  }

  /**
   * Appends the given sub-path to the route.
   *
   * @param subPath The sub-path to add
   * @param mode    The transportation mode to use for this sub-path
   * @param route   The route to add the sub-path to
   */
  private void appendSubPath(final IPath<N, E> subPath, final ETransportationMode mode,
      final List<RouteElement> route) {
    route.add(buildNode(subPath.getSource()));
    route.add(buildPath(subPath, mode));
    route.add(buildNode(subPath.getDestination()));
  }

  /**
   * Builds a journey object which represents the given path.
   *
   * @param request The request the journey belongs to
   * @param path    The path the journey represents
   * @return The resulting journey
   */
  private Journey buildJourney(final RoutingRequest request, final IPath<N, E> path) {
    final long depTime = request.getDepTime();
    final long duration = (long) Math.ceil(RoutingUtil.secondsToMillis(path.getTotalCost()));
    final long arrTime = depTime + duration;

    // The route needs place for at least all edges and
    // the source and destination node
    final List<RouteElement> route = new ArrayList<>(path.length() + 2);

    // Build the route
    // If path is empty we use a singleton node only
    if (path.length() == 0) {
      route.add(buildNode(path.getSource()));
      return new Journey(depTime, arrTime, route);
    }

    EdgePath<N, E> currentPath = null;
    ETransportationMode currentMode = null;
    // Collect sub paths that use a single transportation mode
    for (final EdgeCost<N, E> edgeCost : path) {
      final E edge = edgeCost.getEdge();
      final ETransportationMode edgeMode = getModeOfEdge(request.getModes(), edge);

      // Mode differs
      if (edgeMode != currentMode || currentPath == null) {
        // Append current path
        if (currentPath != null) {
          appendSubPath(currentPath, currentMode, route);
        }
        // Prepare next path with new mode
        currentPath = new EdgePath<>();
        currentMode = edgeMode;
      }

      // Collect edge to current path
      currentPath.addEdge(edge, edgeCost.getCost());
    }
    // Append last path
    appendSubPath(currentPath, currentMode, route);

    return new Journey(depTime, arrTime, route);
  }

  /**
   * Builds a route element which represents the given node.
   *
   * @param node The node to represent
   * @return The resulting route element
   */
  private RouteElement buildNode(final N node) {
    final String name = mDatabase.getOsmNodeByInternal(node.getId()).flatMap(mDatabase::getNodeName).orElse("");
    final float[] coordinates = new float[] { node.getLatitude(), node.getLongitude() };
    return new RouteElement(ERouteElementType.NODE, name, Collections.singletonList(coordinates));
  }

  /**
   * Builds a route element which represents the given path.
   *
   * @param path The path to represent
   * @param mode The transportation mode to use for this path
   * @return The resulting route element
   */
  private RouteElement buildPath(final IPath<N, E> path, final ETransportationMode mode) {
    // TODO The current way of constructing a name may be inappropriate
    final StringJoiner nameJoiner = new StringJoiner(", ");
    final List<float[]> geom = new ArrayList<>(path.length() + 1);

    // Add the source
    final N source = path.getSource();
    geom.add(new float[] { source.getLatitude(), source.getLongitude() });
    if (source instanceof IRoadNode) {
      mDatabase.getOsmNodeByInternal(source.getId()).flatMap(mDatabase::getNodeName).ifPresent(nameJoiner::add);
    }

    // Add all edge destinations
    for (final EdgeCost<N, E> edgeCost : path) {
      final N edgeDestination = edgeCost.getEdge().getDestination();
      geom.add(new float[] { edgeDestination.getLatitude(), edgeDestination.getLongitude() });
    }

    return new RouteElement(ERouteElementType.PATH, mode, nameJoiner.toString(), geom);
  }

  /**
   * Decides for the transportation mode to use for the given edge based on the
   * modes it offers and the given restrictions.<br>
   * <br>
   * The method will choose the fastest available transportation mode.
   *
   * @param modeRestrictions The transportation modes allowed to use
   * @param edge             The edge to travel along
   * @return The transportation mode to use for the given edge
   */
  private ETransportationMode getModeOfEdge(final Set<ETransportationMode> modeRestrictions, final E edge) {
    if (!(edge instanceof IHasTransportationMode)) {
      // Fallback mode
      return ETransportationMode.CAR;
    }

    final Set<ETransportationMode> edgeModes = ((IHasTransportationMode) edge).getTransportationModes();

    // Pick the fastest mode that is available after applying the restrictions
    final Set<ETransportationMode> availableModes = EnumSet.copyOf(edgeModes);
    availableModes.retainAll(modeRestrictions);
    return Collections.max(availableModes, mSpeedComparator);
  }

  /**
   * Sends an empty routing response. This is usually used if no shortest path
   * could be found.
   *
   * @param request   The request to respond to
   * @param startTime The time the computation started, in nanoseconds. Must be
   *                  compatible with {@link System#nanoTime()}.
   * @throws IOException If an I/O exception occurred while sending the response
   */
  private void sendEmptyResponse(final RoutingRequest request, final long startTime) throws IOException {
    final long endTime = System.nanoTime();
    final RoutingResponse response = new RoutingResponse(RoutingUtil.nanosToMillis(endTime - startTime), 0L,
        request.getFrom(), request.getTo(), Collections.emptyList());
    sendResponse(response);
  }

  /**
   * Sends a not reachable response. This is usually used if no shortest path
   * could be found.
   *
   * @param request       The request to respond to
   * @param startTime     The time the computation started, in nanoseconds. Must
   *                      be compatible with {@link System#nanoTime()}.
   * @param startCompTime The time the computation of the shortest path started,
   *                      in nanoseconds. Must be compatible with
   *                      {@link System#nanoTime()}.
   * @throws IOException If an I/O exception occurred while sending the response
   */
  private void sendNotReachableResponse(final RoutingRequest request, final long startTime, final long startCompTime)
      throws IOException {
    final long endTime = System.nanoTime();
    final RoutingResponse response = new RoutingResponse(RoutingUtil.nanosToMillis(endTime - startTime),
        RoutingUtil.nanosToMillis(endTime - startCompTime), request.getFrom(), request.getTo(),
        Collections.emptyList());
    sendResponse(response);
  }

  /**
   * Sends the given routing response.
   *
   * @param response The response to send
   * @throws IOException If an I/O exception occurred while sending the response
   */
  private void sendResponse(final RoutingResponse response) throws IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending response: {}", response);
    }
    final String content = mGson.toJson(response);
    HttpUtil.sendHttpResponse(
        new HttpResponseBuilder().setContentType(EHttpContentType.JSON).setContent(content).build(), mClient);
  }
}
