package de.tischner.cobweb.routing.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.tischner.cobweb.db.IRoutingDatabase;
import de.tischner.cobweb.routing.algorithms.shortestpath.IShortestPathComputation;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.IPath;
import de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById;
import de.tischner.cobweb.routing.model.graph.road.IHasId;
import de.tischner.cobweb.routing.model.graph.road.ISpatial;
import de.tischner.cobweb.routing.server.model.ERouteElementType;
import de.tischner.cobweb.routing.server.model.ETransportationMode;
import de.tischner.cobweb.routing.server.model.Journey;
import de.tischner.cobweb.routing.server.model.RouteElement;
import de.tischner.cobweb.routing.server.model.RoutingRequest;
import de.tischner.cobweb.routing.server.model.RoutingResponse;
import de.tischner.cobweb.util.RoutingUtil;
import de.tischner.cobweb.util.http.HttpResponseBuilder;
import de.tischner.cobweb.util.http.HttpUtil;

public final class RequestHandler<N extends INode & IHasId & ISpatial, E extends IEdge<N> & IHasId, G extends IGraph<N, E> & ICanGetNodeById<N>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
  private final Socket mClient;
  private final IShortestPathComputation<N, E> mComputation;
  private final IRoutingDatabase mDatabase;
  private final G mGraph;
  private final Gson mGson;

  public RequestHandler(final Socket client, final Gson gson, final G graph,
      final IShortestPathComputation<N, E> computation, final IRoutingDatabase database) {
    mClient = client;
    mGson = gson;
    mGraph = graph;
    mComputation = computation;
    mDatabase = database;
  }

  public void handleRequest(final RoutingRequest request) throws IOException {
    // Get the source and destination
    final Optional<N> sourceOptional = mGraph.getNodeById(request.getFrom());
    if (!sourceOptional.isPresent()) {
      sendEmptyResponse(request);
      return;
    }
    final Optional<N> destinationOptional = mGraph.getNodeById(request.getTo());
    if (!destinationOptional.isPresent()) {
      sendEmptyResponse(request);
      return;
    }

    // Nodes are known, compute the path
    final N source = sourceOptional.get();
    final N destination = destinationOptional.get();

    final Optional<IPath<N, E>> pathOptional = mComputation.computeShortestPath(source, destination);
    if (!pathOptional.isPresent()) {
      sendEmptyResponse(request);
      return;
    }

    // Path is present, build the resulting journey
    final IPath<N, E> path = pathOptional.get();
    final Journey journey = buildJourney(request, path);

    // Build and send response
    final RoutingResponse response = new RoutingResponse(request.getFrom(), request.getTo(),
        Collections.singletonList(journey));
    sendResponse(response);
  }

  private Journey buildJourney(final RoutingRequest request, final IPath<N, E> path) {
    final long depTime = request.getDepTime();
    final long duration = (long) Math.ceil(RoutingUtil.secondsToMilliseconds(path.getTotalCost()));
    final long arrTime = depTime + duration;

    // The route needs place for at least all edges and
    // the source and destination node
    final List<RouteElement> route = new ArrayList<>(path.length() + 2);

    // Build the route
    route.add(buildNode(path.getSource()));
    // TODO Add nodes if transportation mode changes
    route.add(buildPath(path));
    // Add destination if the path is not empty
    if (path.length() != 0) {
      route.add(buildNode(path.getDestination()));
    }

    return new Journey(depTime, arrTime, route);
  }

  private RouteElement buildNode(final N node) {
    final String name = mDatabase.getNodeName(node.getId()).orElse("");
    final double[] coordinates = new double[] { node.getLatitude(), node.getLongitude() };
    return new RouteElement(ERouteElementType.NODE, name, Collections.singletonList(coordinates));
  }

  private RouteElement buildPath(final IPath<N, E> path) {
    // TODO The current way of constructing a name may be inappropriate
    final StringJoiner nameJoiner = new StringJoiner(", ");
    final List<double[]> geom = new ArrayList<>(path.length() + 1);

    // Add the source
    final N source = path.getSource();
    geom.add(new double[] { source.getLatitude(), source.getLongitude() });
    mDatabase.getNodeName(source.getId()).ifPresent(nameJoiner::add);

    // Add all edge destinations
    final long lastWayId = -1;
    for (final E edge : path) {
      final N edgeDestination = edge.getDestination();
      geom.add(new double[] { edgeDestination.getLatitude(), edgeDestination.getLongitude() });

      final long wayId = edge.getId();
      if (wayId != lastWayId) {
        mDatabase.getWayName(wayId).ifPresent(nameJoiner::add);
      }
    }

    return new RouteElement(ERouteElementType.PATH, ETransportationMode.CAR, nameJoiner.toString(), geom);
  }

  private void sendEmptyResponse(final RoutingRequest request) throws IOException {
    final RoutingResponse response = new RoutingResponse(request.getFrom(), request.getTo(), Collections.emptyList());
    sendResponse(response);
  }

  private void sendResponse(final RoutingResponse response) throws IOException {
    final String content = mGson.toJson(response);
    HttpUtil.sendHttpResponse(new HttpResponseBuilder().setContent(content).build(), mClient);
  }
}
