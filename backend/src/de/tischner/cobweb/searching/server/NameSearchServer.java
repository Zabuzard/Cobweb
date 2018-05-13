package de.tischner.cobweb.searching.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.INameSearchConfigProvider;
import de.tischner.cobweb.db.INameSearchDatabase;
import de.tischner.cobweb.searching.model.NodeNameSet;
import de.tischner.cobweb.searching.server.model.NameSearchRequest;
import de.tischner.cobweb.searching.server.model.NameSearchResponse;
import de.zabuza.lexisearch.indexing.IKeyRecord;
import de.zabuza.lexisearch.indexing.qgram.QGramProvider;
import de.zabuza.lexisearch.queries.FuzzyPrefixQuery;
import de.zabuza.lexisearch.ranking.PostingBeforeRecordRanking;

/**
 * A server which offers a REST API that is able to answer name search
 * requests.<br>
 * <br>
 * After construction the {@link #initialize()} method should be called.
 * Afterwards it can be started by using {@link #start()}. Request the server to
 * shutdown by using {@link #shutdown()}, the current status can be checked with
 * {@link #isRunning()}. Once a server was shutdown it should not be used
 * anymore, instead create a new one.<br>
 * <br>
 * A request consists of a name, which can be a prefix and fuzzy, and a maximal
 * amount of matches interested in. A response consists of a list of matches,
 * sorted by relevance (most relevant first). It also includes the time it
 * needed to answer the query in milliseconds. The response will not contain
 * more matches than specified by the request. A match consists of the full name
 * and the corresponding OSM node ID.<br>
 * <br>
 * The REST API communicates over HTTP by sending and receiving JSON objects.
 * Requests are parsed into {@link NameSearchRequest} and responses into
 * {@link NameSearchResponse}. Accepted HTTP methods are <tt>POST</tt> and
 * <tt>OPTIONS</tt>. The server will send <tt>BAD REQUEST</tt> to invalid
 * requests.<br>
 * <br>
 * The server itself handles clients in parallel using a cached thread pool. For
 * construction it wants a configuration and a database for retrieving the name
 * data-set.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NameSearchServer implements Runnable {
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(NameSearchServer.class);
  /**
   * The value to use for the <tt>q-grams</tt>, i.e. the <tt>q</tt>.
   */
  private static final int Q_GRAM_VALUE = 3;
  /**
   * The timeout used when waiting for a client to connect, in milliseconds. The
   * server status is checked after each timeout.
   */
  private static final int SOCKET_TIMEOUT = 2_000;
  /**
   * Configuration provider which provides the port that should be used by the
   * server.
   */
  private final INameSearchConfigProvider mConfig;
  /**
   * Database used for retrieving the name data-set.
   */
  private final INameSearchDatabase mDatabase;
  /**
   * The query object to use for answering fuzzy prefix queries.
   */
  private FuzzyPrefixQuery<IKeyRecord<String>> mFuzzyQuery;
  /**
   * The maximal amount of matches to send in a response.
   */
  private int mMatchLimit;
  /**
   * The data-set of node names to query on.
   */
  private NodeNameSet mNodeNames;
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
   * Creates a new name search server with the given configuration that works
   * with the given tools.<br>
   * <br>
   * After construction the {@link #initialize()} method should be called.
   * Afterwards it can be started by using {@link #start()}. Request the server
   * to shutdown by using {@link #shutdown()}, the current status can be checked
   * with {@link #isRunning()}. Once a server was shutdown it should not be used
   * anymore, instead create a new one.
   *
   * @param config   Configuration provider which provides the port that should
   *                 be used by the server
   * @param database Database used for retrieving the name data-set
   */
  public NameSearchServer(final INameSearchConfigProvider config, final INameSearchDatabase database) {
    mConfig = config;
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
    initializeFuzzyPrefixQuery();
    mMatchLimit = mConfig.getMatchLimit();
    mServerThread = new Thread(this);
    try {
      mServerSocket = new ServerSocket(mConfig.getNameSearchServerPort());
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
        final ClientHandler handler = new ClientHandler(requestId, client, mFuzzyQuery, mNodeNames, mMatchLimit);
        executor.execute(handler);
      } catch (final SocketTimeoutException e) {
        // Ignore the exception. The timeout is used to repeatedly check if the
        // server should continue running.
      } catch (final Exception e) {
        // TODO Implement some limit of repeated exceptions
        // Log every exception and try to keep alive
        LOGGER.error("Unknown exception in name search server routine", e);
      }
    }

    LOGGER.info("Name search server is shutting down");
  }

  /**
   * Requests the server to shutdown.<br>
   * <br>
   * The current status can be checked with {@link #isRunning()}. Once a server
   * was shutdown it should not be used anymore, instead create a new one.
   */
  public void shutdown() {
    mShouldRun = false;
    LOGGER.info("Set shutdown request to name search server");
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
    LOGGER.info("Starting name search server");
    mShouldRun = true;
    mServerThread.start();
  }

  /**
   * Initializes the query object which is used for answering fuzzy prefix
   * queries.
   */
  private void initializeFuzzyPrefixQuery() {
    LOGGER.info("Setting up fuzzy prefix query");
    final Instant fuzzyTimeStart = Instant.now();

    final QGramProvider qGramProvider = new QGramProvider(Q_GRAM_VALUE);
    final PostingBeforeRecordRanking<String> ranking = new PostingBeforeRecordRanking<>();
    mNodeNames = NodeNameSet.buildFromNodeNameData(mDatabase.getAllNodeNameData(), qGramProvider);
    mFuzzyQuery = new FuzzyPrefixQuery<>(mNodeNames, qGramProvider, ranking);
    LOGGER.info("Inverted index size: {}", mNodeNames.size());

    final Instant fuzzyTimeEnd = Instant.now();
    LOGGER.info("Setup took: {}", Duration.between(fuzzyTimeStart, fuzzyTimeEnd));
  }

}
