package de.tischner.cobweb.searching.name.server.model;

import java.util.List;

/**
 * POJO that models a name search response.<br>
 * <br>
 * A response consists of a list of matches, sorted by relevance (most relevant
 * first). It also includes the time it needed to answer the query in
 * milliseconds. A match consists of the full name and the corresponding OSM
 * node ID.<br>
 * <br>
 * It has the exact structure that is expected as response format for the REST
 * API. It is primarily used to be constructed and then encoded to JSON to be
 * send to the client.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NameSearchResponse {
  /**
   * A list of all computed matches, sorted by relevance (most relevant first).
   */
  private List<Match> mMatches;
  /**
   * The duration answering the query took, in milliseconds.
   */
  private long mTime;

  /**
   * Creates a new name search response.
   *
   * @param time    The duration answering the query took, in milliseconds
   * @param matches A list of all computed matches, sorted by relevance (most
   *                relevant first)
   */
  public NameSearchResponse(final long time, final List<Match> matches) {
    mTime = time;
    mMatches = matches;
  }

  /**
   * Creates a new empty name search response. Is used to construct the element
   * via reflection.
   */
  @SuppressWarnings("unused")
  private NameSearchResponse() {
    // Empty constructor for construction through reflection
  }

  /**
   * Gets a list of all computed matches, sorted by relevance (most relevant
   * first).
   *
   * @return A list of all computed matches, sorted by relevance (most relevant
   *         first)
   */
  public List<Match> getMatches() {
    return mMatches;
  }

  /**
   * Gets the duration answering the query took, in milliseconds.
   *
   * @return The duration to get
   */
  public long getTime() {
    return mTime;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("NameSearchResponse [matches=");
    builder.append(mMatches);
    builder.append("]");
    return builder.toString();
  }
}
