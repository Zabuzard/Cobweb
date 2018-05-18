package de.tischner.cobweb.db;

import java.util.Collection;
import java.util.stream.StreamSupport;

import de.tischner.cobweb.routing.parsing.osm.IdMapping;
import de.topobyte.osm4j.core.model.iface.OsmEntity;

/**
 * Abstract class for implementations of {@link IRoutingDatabase} and
 * {@link INameSearchDatabase}. Implements some of the overloaded methods by
 * using the core variant of the corresponding method.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public abstract class ADatabase implements IRoutingDatabase, INameSearchDatabase {
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
   * de.tischner.cobweb.db.IRoutingDatabase#offerIdMappings(java.lang.Iterable,
   * int)
   */
  @Override
  public void offerIdMappings(final Iterable<IdMapping> mappings, final int size) {
    offerIdMappings(StreamSupport.stream(mappings.spliterator(), false), size);
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
