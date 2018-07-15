package de.unifreiburg.informatik.cobweb.db;

/**
 * POJO for node name data. Stores information about a node like its ID and
 * name.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NodeNameData {
  /**
   * Unique OSM ID of the node.
   */
  private final long mId;
  /**
   * Name of the node.
   */
  private final String mName;

  /**
   * Creates a new node name data object with the given attributes.
   *
   * @param id   Unique OSM ID of the node
   * @param name Name of the node
   */
  public NodeNameData(final long id, final String name) {
    mId = id;
    mName = name;
  }

  /**
   * Gets the unique OSM ID of the node.
   *
   * @return The node ID
   */
  public long getId() {
    return mId;
  }

  /**
   * Gets the name of the node.
   *
   * @return The name of the node
   */
  public String getName() {
    return mName;
  }
}
