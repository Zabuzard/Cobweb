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
import de.tischner.cobweb.routing.model.graph.IGetNodeById;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.ISpatial;
import de.tischner.cobweb.routing.server.model.RoutingRequest;
import de.tischner.cobweb.routing.server.model.RoutingResponse;

/**
 * A server which offers a REST API that is able to answer routing requests.<br>
 * <br>
 * After construction the {@link #initialize()} method should be called.
 * Afterwards it can be started by using {@link #start()}. Request the server to
 * shutdown by using {@link #shutdown()}, the current status can be checked with
 * {@link #isRunning()}. Once a server was shutdown it should not be used
 * anymore, instead create a new one.<br>
 * <br>
 * A request may consist of departure time, source and destination nodes and
 * meta-data like desired transportation modes. A response consists of departure
 * and arrival time, together with possible routes. It also includes the time it
 * needed to answer the query in milliseconds.<br>
 * <br>
 * The REST API communicates over HTTP by sending and receiving JSON objects.
 * Requests are parsed into {@link RoutingRequest} and responses into
 * {@link RoutingResponse}. Accepted HTTP methods are <tt>POST</tt> and
 * <tt>OPTIONS</tt>. The server will send <tt>BAD REQUEST</tt> to invalid
 * requests.<br>
 * <br>
 * The server itself handles clients in parallel using a cached thread pool. For
 * construction it wants a configuration, a graph to route on, an algorithm to
 * compute shortest paths with and a database for retrieving meta-data.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 * @param <G> Type of the graph
 */
public final class RoutingServer<N extends INode & IHasId & ISpatial, E extends IEdge<N> & IHasId,
    G extends IGraph<N, E> & IGetNodeById<N>> implements Runnable {
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingServer.class);
  /**
   * The timeout used when waiting for a client to connect, in milliseconds. The
   * server status is checked after each timeout.
   */
  private static final int SOCKET_TIMEOUT = 2_000;
  /**
   * The algorithm to use for shortest path computation.
   */
  private final IShortestPathComputation<N, E> mComputation;
  /**
   * Configuration provider which provides the port that should be used by the
   * server.
   */
  private final IRoutingConfigProvider mConfig;
  /**
   * Database used for retrieving meta-data about graph objects like nodes and
   * edges.
   */
  private final IRoutingDatabase mDatabase;
  /**
   * The graph to route on.
   */
  private final G mGraph;
  /**
   * The server socket to use for communication.
   */
  private ServerSocket mServerSocket;
  /**
   * The thread to run this server on.
   */
  private Thread mServerThread;
  /**
   * Whether or not the server thread should run.
   */
  private volatile boolean mShouldRun;

  /**
   * Creates a new routing server with the given configuration that works with
   * the given tools.<br>
   * <br>
   * After construction the {@link #initialize()} method should be called.
   * Afterwards it can be started by using {@link #start()}. Request the server
   * to shutdown by using {@link #shutdown()}, the current status can be checked
   * with {@link #isRunning()}. Once a server was shutdown it should not be used
   * anymore, instead create a new one.
   *
   * @param config      Configuration provider which provides the port that
   *                    should be used by the server
   * @param graph       The graph to route on
   * @param computation The algorithm to use for shortest path computation
   * @param database    Database used for retrieving meta-data about graph
   *                    objects like nodes and edges
   */
  public RoutingServer(final IRoutingConfigProvider config, final G graph,
      final IShortestPathComputation<N, E> computation, final IRoutingDatabase database) {
    mConfig = config;
    mGraph = graph;
    mComputation = computation;
    mDatabase = database;
  }

  /**
   * Initializes the server. Call this method prior to starting the server with
   * {@link #start()}. Do not call it again afterwards.
   *
   * @throws UncheckedIOException If an I/O exception occurred while creating
   *                              the server socket.
   */
  public void initialize() throws UncheckedIOException {
    mServerThread = new Thread(this);
    try {
      mServerSocket = new ServerSocket(mConfig.getRoutingServerPort());
      mServerSocket.setSoTimeout(SOCKET_TIMEOUT);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Whether or not the server is currently running.<br>
   * <br>
   * A request to shutdown can be send using {@link #shutdown()}.
   *
   * @return <tt>True</tt> if the server is running, <tt>false</tt> otherwise
   */
  public boolean isRunning() {
    return mServerThread.isAlive();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
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
        // Ignore the exception. The timeout is used to repeatedly check if the
        // server should continue running.
      } catch (final Exception e) {
        // TODO Implement some limit of repeated exceptions
        // Log every exception and try to keep alive
        LOGGER.error("Unknown exception in routing server routine", e);
      }
    }

    LOGGER.info("Routing server is shutting down");
  }

  /**
   * Requests the server to shutdown.<br>
   * <br>
   * The current status can be checked with {@link #isRunning()}. Once a server
   * was shutdown it should not be used anymore, instead create a new one.
   */
  public void shutdown() {
    mShouldRun = false;
    LOGGER.info("Set shutdown request to routing server");
  }

  /**
   * Starts the server.<br>
   * <br>
   * Make sure {@link #initialize()} is called before. Request the server to
   * shutdown by using {@link #shutdown()}, the current status can be checked
   * with {@link #isRunning()}. Once a server was shutdown it should not be used
   * anymore, instead create a new one.
   */
  public void start() {
    if (isRunning()) {
      return;
    }
    LOGGER.info("Starting routing server");
    mShouldRun = true;
    mServerThread.start();
  }

}
