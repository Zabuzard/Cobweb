package de.tischner.cobweb.searching.nearest.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.INearestSearchConfigProvider;
import de.tischner.cobweb.db.INearestSearchDatabase;
import de.tischner.cobweb.routing.algorithms.nearestneighbor.INearestNeighborComputation;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.searching.nearest.server.model.NearestSearchRequest;
import de.tischner.cobweb.searching.nearest.server.model.NearestSearchResponse;

/**
 * A server which offers a REST API that is able to answer nearest neighboring
 * node search requests.<br>
 * <br>
 * After construction the {@link #initialize()} method should be called.
 * Afterwards it can be started by using {@link #start()}. Request the server to
 * shutdown by using {@link #shutdown()}, the current status can be checked with
 * {@link #isRunning()}. Once a server was shutdown it should not be used
 * anymore, instead create a new one.<br>
 * <br>
 * A request consists of a latitude and longitude. A response consists of the
 * nearest OSM node, including its unique OSM ID and its exact latitude and
 * longitude coordinates. It also includes the time it needed to answer the
 * query in milliseconds.<br>
 * <br>
 * The REST API communicates over HTTP by sending and receiving JSON objects.
 * Requests are parsed into {@link NearestSearchRequest} and responses into
 * {@link NearestSearchResponse}. Accepted HTTP methods are <tt>POST</tt> and
 * <tt>OPTIONS</tt>. The server will send <tt>BAD REQUEST</tt> to invalid
 * requests.<br>
 * <br>
 * The server itself handles clients in parallel using a cached thread pool. For
 * construction it wants a configuration and a nearest neighbor computation
 * object for retrieving the nodes.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NearestSearchServer implements Runnable {
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(NearestSearchServer.class);
  /**
   * The timeout used when waiting for a client to connect, in milliseconds. The
   * server status is checked after each timeout.
   */
  private static final int SOCKET_TIMEOUT = 2_000;
  /**
   * Configuration provider which provides the port that should be used by the
   * server.
   */
  private final INearestSearchConfigProvider mConfig;
  /**
   * The database to use for retrieving node data.
   */
  private final INearestSearchDatabase mDatabase;
  /**
   * The nearest neighbor computation algorithm to use.
   */
  private final INearestNeighborComputation<ICoreNode> mNearestNeighborComputation;
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
   * Creates a new nearest search server with the given configuration that works
   * with the given algorithm.<br>
   * <br>
   * After construction the {@link #initialize()} method should be called.
   * Afterwards it can be started by using {@link #start()}. Request the server
   * to shutdown by using {@link #shutdown()}, the current status can be checked
   * with {@link #isRunning()}. Once a server was shutdown it should not be used
   * anymore, instead create a new one.
   *
   * @param config                     Configuration provider which provides the
   *                                   port that should be used by the server
   * @param nearestNeighborComputation Nearest neighbor computation algorithm to
   *                                   use
   * @param database                   The database to use for retrieving node
   *                                   data
   */
  public NearestSearchServer(final INearestSearchConfigProvider config,
      final INearestNeighborComputation<ICoreNode> nearestNeighborComputation, final INearestSearchDatabase database) {
    mConfig = config;
    mNearestNeighborComputation = nearestNeighborComputation;
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
      mServerSocket = new ServerSocket(mConfig.getNearestSearchServerPort());
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
        final ClientHandler handler = new ClientHandler(requestId, client, mNearestNeighborComputation, mDatabase);
        executor.execute(handler);
      } catch (final SocketTimeoutException e) {
        // Ignore the exception. The timeout is used to repeatedly check if the
        // server should continue running.
      } catch (final Exception e) {
        // TODO Implement some limit of repeated exceptions
        // Log every exception and try to stay alive
        LOGGER.error("Unknown exception in nearest search server routine", e);
      }
    }

    LOGGER.info("Nearest search server is shutting down");
  }

  /**
   * Requests the server to shutdown.<br>
   * <br>
   * The current status can be checked with {@link #isRunning()}. Once a server
   * was shutdown it should not be used anymore, instead create a new one.
   */
  public void shutdown() {
    mShouldRun = false;
    LOGGER.info("Set shutdown request to nearest search server");
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
    LOGGER.info("Starting nearest search server");
    mShouldRun = true;
    mServerThread.start();
  }

}
