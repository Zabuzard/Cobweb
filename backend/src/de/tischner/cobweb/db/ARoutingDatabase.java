package de.tischner.cobweb.db;

import java.util.Collection;
import java.util.stream.StreamSupport;

import de.topobyte.osm4j.core.model.iface.OsmEntity;

/**
 * Abstract class for implementations of {@link IRoutingDatabase}. Implements
 * some of the overloaded methods by using the core variant of the corresponding
 * method.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public abstract class ARoutingDatabase implements IRoutingDatabase {
  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.db.IRoutingDatabase#getHighwayData(java.lang.Iterable,
   * int)
   */
  @Override
  public Collection<HighwayData> getHighwayData(final Iterable<Long> wayIds, final int size) {
    return getHighwayData(StreamSupport.stream(wayIds.spliterator(), false).mapToLong(l -> (long) l), size);
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getSpatialNodeData(java.lang.
   * Iterable, int)
   */
  @Override
  public Collection<SpatialNodeData> getSpatialNodeData(final Iterable<Long> nodeIds, final int size) {
    return getSpatialNodeData(StreamSupport.stream(nodeIds.spliterator(), false).mapToLong(l -> (long) l), size);
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.db.IRoutingDatabase#offerOsmEntities(java.lang.Iterable,
   * int)
   */
  @Override
  public void offerOsmEntities(final Iterable<OsmEntity> entities, final int size) {
    offerOsmEntities(StreamSupport.stream(entities.spliterator(), false), size);
  }
}
