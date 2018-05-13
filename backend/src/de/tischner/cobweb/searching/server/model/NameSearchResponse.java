package de.tischner.cobweb.searching.server.model;

import java.util.List;

/**
 * POJO that models a name search response.<br>
 * <br>
 * A response consists of a list of matches, sorted by relevance (most relevant
 * first). A match consists of the full name and the corresponding OSM node
 * ID.<br>
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
   * Creates a new name search response.
   *
   * @param matches A list of all computed matches, sorted by relevance (most
   *                relevant first)
   */
  public NameSearchResponse(final List<Match> matches) {
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
