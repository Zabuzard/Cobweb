package de.unifreiburg.informatik.cobweb.searching.name.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.unifreiburg.informatik.cobweb.searching.name.model.NodeName;
import de.unifreiburg.informatik.cobweb.searching.name.model.NodeNameSet;
import de.unifreiburg.informatik.cobweb.searching.name.server.model.Match;
import de.unifreiburg.informatik.cobweb.searching.name.server.model.NameSearchRequest;
import de.unifreiburg.informatik.cobweb.searching.name.server.model.NameSearchResponse;
import de.unifreiburg.informatik.cobweb.util.RoutingUtil;
import de.unifreiburg.informatik.cobweb.util.http.EHttpContentType;
import de.unifreiburg.informatik.cobweb.util.http.HttpResponseBuilder;
import de.unifreiburg.informatik.cobweb.util.http.HttpUtil;
import de.zabuza.lexisearch.indexing.IKeyRecord;
import de.zabuza.lexisearch.indexing.Posting;
import de.zabuza.lexisearch.queries.FuzzyPrefixQuery;

/**
 * Class that handles a name search request. It parses the request, computes
 * corresponding matches and builds and sends a proper response.<br>
 * <br>
 * To handle a request call {@link #handleRequest(NameSearchRequest)}.
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
   * The query object to use for answering the fuzzy prefix query.
   */
  private final FuzzyPrefixQuery<IKeyRecord<String>> mFuzzyQuery;
  /**
   * The GSON object used to format JSON responses.
   */
  private final Gson mGson;
  /**
   * The maximal amount of matches to send.
   */
  private final int mMatchLimit;
  /**
   * The data-set of node names to query on.
   */
  private final NodeNameSet mNodeNames;

  /**
   * Creates a new handler which handles requests of the given client using the
   * given tools.<br>
   * <br>
   * To handle a request call {@link #handleRequest(NameSearchRequest)}.
   *
   * @param client     The client whose request to handle
   * @param gson       The GSON object used to format JSON responses
   * @param fuzzyQuery The query object to use for answering the fuzzy prefix
   *                   query
   * @param nodeNames  The data-set of node names to query on
   * @param matchLimit The maximal amount of matches to send
   */
  public RequestHandler(final Socket client, final Gson gson, final FuzzyPrefixQuery<IKeyRecord<String>> fuzzyQuery,
      final NodeNameSet nodeNames, final int matchLimit) {
    mClient = client;
    mGson = gson;
    mFuzzyQuery = fuzzyQuery;
    mNodeNames = nodeNames;
    mMatchLimit = matchLimit;
  }

  /**
   * Handles the given name search request. It computes matches and constructs
   * and sends a proper response.
   *
   * @param request The request to handle
   * @throws IOException If an I/O exception occurred while sending a response
   */
  public void handleRequest(final NameSearchRequest request) throws IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Handling request: {}", request);
    }
    final long startTime = System.nanoTime();

    // Get the search request
    final String name = request.getName();
    if (name.trim().isEmpty()) {
      sendEmptyResponse(startTime);
      return;
    }
    int amount = request.getAmount();
    if (amount <= 0) {
      sendEmptyResponse(startTime);
      return;
    }
    if (amount > mMatchLimit) {
      amount = mMatchLimit;
    }

    // Compute matches
    final List<Match> matches = buildMatches(mFuzzyQuery.searchOr(Collections.singletonList(name)), amount);

    final long endTime = System.nanoTime();

    // Build and send response
    final NameSearchResponse response = new NameSearchResponse(RoutingUtil.nanosToMillis(endTime - startTime), matches);
    sendResponse(response);
  }

  /**
   * Computes the list of matches corresponding to the given postings.
   *
   * @param postings The postings to compute matches for
   * @param amount   The maximal amount of matches to build. It is ensured that
   *                 the resulting list does not contain more entries than
   *                 specified by this parameter.
   * @return A list of maximal <code>amount</code> matches corresponding to the
   *         given postings
   */
  private List<Match> buildMatches(final List<Posting> postings, final int amount) {
    final int resultingAmount = Math.min(amount, postings.size());
    final List<Match> matches = new ArrayList<>(resultingAmount);

    final Iterator<Posting> postingIter = postings.iterator();
    for (int i = 0; i < resultingAmount; i++) {
      final Posting posting = postingIter.next();
      final IKeyRecord<String> record = mNodeNames.getKeyRecordById(posting.getId());
      final long nodeId = ((NodeName) record).getNodeId();
      final String name = record.getName();
      matches.add(new Match(nodeId, name));
    }
    return matches;
  }

  /**
   * Sends an empty name search response. This is usually used if the name to
   * search was empty or no match could be found.
   *
   * @param startTime The time the computation started, in nanoseconds. Must be
   *                  compatible with {@link System#nanoTime()}.
   * @throws IOException If an I/O exception occurred while sending the response
   */
  private void sendEmptyResponse(final long startTime) throws IOException {
    final long endTime = System.nanoTime();
    final NameSearchResponse response =
        new NameSearchResponse(RoutingUtil.nanosToMillis(endTime - startTime), Collections.emptyList());
    sendResponse(response);
  }

  /**
   * Sends the given name search response.
   *
   * @param response The response to send
   * @throws IOException If an I/O exception occurred while sending the response
   */
  private void sendResponse(final NameSearchResponse response) throws IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending response: {}", response);
    }
    final String content = mGson.toJson(response);
    HttpUtil.sendHttpResponse(
        new HttpResponseBuilder().setContentType(EHttpContentType.JSON).setContent(content).build(), mClient);
  }
}
