package de.tischner.cobweb.routing.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.IRoutingConfigProvider;
import de.tischner.cobweb.db.IRoutingDatabase;
import de.tischner.cobweb.routing.algorithms.shortestpath.IShortestPathComputation;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById;
import de.tischner.cobweb.routing.model.graph.road.IHasId;
import de.tischner.cobweb.routing.model.graph.road.ISpatial;

public final class RoutingServer<N extends INode & IHasId & ISpatial, E extends IEdge<N> & IHasId, G extends IGraph<N, E> & ICanGetNodeById<N>>
    implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingServer.class);
  private static final int SOCKET_TIMEOUT = 2_000;
  private final IShortestPathComputation<N, E> mComputation;
  private final IRoutingConfigProvider mConfig;
  private final IRoutingDatabase mDatabase;
  private final G mGraph;
  private ServerSocket mServerSocket;
  private Thread mServerThread;
  private volatile boolean mShouldRun;

  public RoutingServer(final IRoutingConfigProvider config, final G graph,
      final IShortestPathComputation<N, E> computation, final IRoutingDatabase database) {
    mConfig = config;
    mGraph = graph;
    mComputation = computation;
    mDatabase = database;
  }

  public void initialize() throws UncheckedIOException {
    mServerThread = new Thread(this);
    try {
      mServerSocket = new ServerSocket(mConfig.getRoutingServerPort());
      mServerSocket.setSoTimeout(SOCKET_TIMEOUT);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public boolean isRunning() {
    return mServerThread.isAlive();
  }

  @Override
  public void run() {
    final ExecutorService executor = Executors.newCachedThreadPool();
    int requestId = -1;

    LOGGER.info("Server ready and waiting for clients");
    while (mShouldRun) {
      try {
        // Accept a client, the handler will close it
        @SuppressWarnings("resource")
        final Socket client = mServerSocket.accept();

        // Handle the client
        requestId++;
        final ClientHandler<N, E, G> handler = new ClientHandler<>(requestId, client, mGraph, mComputation, mDatabase);
        executor.execute(handler);
      } catch (final SocketTimeoutException e) {
        // Ignore the exception. The timeout is used to repeatedly check if the server
        // should continue running.
      } catch (final Exception e) {
        // TODO Implement some limit of repeated exceptions
        // Log every exception and try to keep alive
        LOGGER.error("Unknown exception in routing server routine", e);
      }
    }

    LOGGER.info("Routing server is shutting down");
  }

  public void shutdown() {
    mShouldRun = false;
    LOGGER.info("Set shutdown request to routing server");
  }

  public void start() {
    if (isRunning()) {
      return;
    }
    LOGGER.info("Starting routing server");
    mShouldRun = true;
    mServerThread.start();
  }

}
