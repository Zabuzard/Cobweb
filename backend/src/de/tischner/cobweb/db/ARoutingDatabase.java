package de.tischner.cobweb.db;

import java.util.Collection;
import java.util.stream.StreamSupport;

import de.topobyte.osm4j.core.model.iface.OsmEntity;

public abstract class ARoutingDatabase implements IRoutingDatabase {
  @Override
  public Collection<HighwayData> getHighwayData(final Iterable<Long> wayIds, final int size) {
    return getHighwayData(StreamSupport.stream(wayIds.spliterator(), false).mapToLong(l -> (long) l), size);
  }

  @Override
  public Collection<SpatialNodeData> getSpatialNodeData(final Iterable<Long> nodeIds, final int size) {
    return getSpatialNodeData(StreamSupport.stream(nodeIds.spliterator(), false).mapToLong(l -> (long) l), size);
  }

  @Override
  public void offerOsmEntities(final Iterable<OsmEntity> entities, final int size) {
    offerOsmEntities(StreamSupport.stream(entities.spliterator(), false), size);
  }
}
