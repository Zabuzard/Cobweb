package de.unifreiburg.informatik.cobweb.searching.name.server.model;

/**
 * POJO that models a match to a name search.<br>
 * <br>
 * A match consists of the full name and the corresponding OSM node ID.<br>
 * <br>
 * Is used in a {@link NameSearchResponse} and usually decoded into JSON.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class Match {
  /**
   * The unique OSM node ID of the matched node.
   */
  private long mId;
  /**
   * The full name of the matched node.
   */
  private String mName;

  /**
   * Creates a new match to a name search.
   *
   * @param id   The unique OSM node ID of the matched node
   * @param name The full name of the matched node
   */
  public Match(final long id, final String name) {
    mId = id;
    mName = name;
  }

  /**
   * Creates a new empty match. Is used to construct the element via reflection.
   */
  @SuppressWarnings("unused")
  private Match() {
    // Empty constructor for construction through reflection
  }

  /**
   * Gets the unique OSM node ID of the matched node.
   *
   * @return The unique OSM node ID of the matched node
   */
  public long getId() {
    return mId;
  }

  /**
   * Gets the full name of the matched node.
   *
   * @return The full name of the matched node
   */
  public String getName() {
    return mName;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Match [id=");
    builder.append(mId);
    builder.append(", name=");
    builder.append(mName);
    builder.append("]");
    return builder.toString();
  }
}
