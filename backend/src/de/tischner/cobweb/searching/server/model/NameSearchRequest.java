package de.tischner.cobweb.searching.server.model;

/**
 * POJO that models a name search request.<br>
 * <br>
 * A request consists of a name, which can be a prefix and fuzzy, and a maximal
 * amount of matches interested in. The response will not contain more matches
 * than specified.<br>
 * <br>
 * It has the exact structure that is expected as request format for the REST
 * API. It is primarily used to be constructed from the clients JSON request.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NameSearchRequest {
  /**
   * The maximal amount of matches interest in. The response will not contain
   * more matches than specified.
   */
  private int mAmount;
  /**
   * The name to search, can be a prefix and fuzzy.
   */
  private String mName;

  /**
   * Creates a new name search request.
   *
   * @param name   The name to search, can be a prefix and fuzzy
   * @param amount The maximal amount of matches interest in. The response will
   *               not contain more matches than specified.
   */
  public NameSearchRequest(final String name, final int amount) {
    mName = name;
    mAmount = amount;
  }

  /**
   * Creates a new empty name search request. Is used to construct the element
   * via reflection.
   */
  @SuppressWarnings("unused")
  private NameSearchRequest() {
    // Empty constructor for construction through reflection
  }

  /**
   * Gets the maximal amount of matches interest in. The response will not
   * contain more matches than specified.
   *
   * @return The maximal amount of matches interest in
   */
  public int getAmount() {
    return mAmount;
  }

  /**
   * Gets the name to search, can be a prefix and fuzzy.
   *
   * @return The name to search, can be a prefix and fuzzy
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
    builder.append("NameSearchRequest [name=");
    builder.append(mName);
    builder.append(", amount=");
    builder.append(mAmount);
    builder.append("]");
    return builder.toString();
  }
}
