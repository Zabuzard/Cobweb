package de.tischner.cobweb.routing.server;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.tischner.cobweb.db.IRoutingDatabase;
import de.tischner.cobweb.routing.algorithms.shortestpath.IShortestPathComputation;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById;
import de.tischner.cobweb.routing.model.graph.road.IHasId;
import de.tischner.cobweb.routing.model.graph.road.ISpatial;
import de.tischner.cobweb.routing.server.model.RoutingRequest;
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
    // TODO Do something
    // TODO Remove debug print
    System.out.println(request);
    HttpUtil.sendHttpResponse(new HttpResponseBuilder().setContent("Hello World").build(), mClient);
  }
}
