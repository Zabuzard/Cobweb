package de.unifreiburg.informatik.cobweb.routing.parsing.osm;

/**
 * POJO class which maps an OSM ID to an internal ID.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class IdMapping {
  /**
   * The internal ID of this mapping
   */
  private final int mInternalId;

  /**
   * Whether this mapping is for a node or a way.
   */
  private final boolean mIsNode;
  /**
   * The OSM ID of this mapping.
   */
  private final long mOsmId;

  /**
   * Creates a new mapping of an OSM ID to an internal ID.
   *
   * @param osmId      The OSM ID to map to the internal ID
   * @param internalId The internal ID to map to the OSM ID
   * @param isNode     <tt>True</tt> if this mapping is for a node,
   *                   <tt>false</tt> for a way
   */
  public IdMapping(final long osmId, final int internalId, final boolean isNode) {
    mOsmId = osmId;
    mInternalId = internalId;
    mIsNode = isNode;
  }

  /**
   * Gets the internal ID of this mapping.
   *
   * @return The internal ID to get
   */
  public int getInternalId() {
    return mInternalId;
  }

  /**
   * Gets the OSM ID of this mapping.
   *
   * @return The OSM ID to get
   */
  public long getOsmId() {
    return mOsmId;
  }

  /**
   * Whether or not this mapping is for a node or a way.
   *
   * @return <tt>True</tt> if this mapping is for a node, <tt>false</tt> for a
   *         way
   */
  public boolean isNode() {
    return mIsNode;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("IdMapping [isNode=");
    builder.append(mIsNode);
    builder.append(", osmId=");
    builder.append(mOsmId);
    builder.append(", internalId=");
    builder.append(mInternalId);
    builder.append("]");
    return builder.toString();
  }
}
