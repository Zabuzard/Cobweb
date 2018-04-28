package de.tischner.cobweb.db;

import java.util.Optional;
import java.util.Set;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.topobyte.osm4j.core.model.iface.OsmEntity;

public final class RoutingDatabase implements IRoutingDatabase {

  public RoutingDatabase() {
    // TODO Implement something
  }

  @Override
  public Optional<Long> getNodeByName(final String name) {
    // TODO Implement something
    return null;
  }

  @Override
  public Set<SpatialNodeData> getSpatialNodeData(final Iterable<Long> nodeIds) {
    return getSpatialNodeData(StreamSupport.stream(nodeIds.spliterator(), false).mapToLong(l -> (long) l));
  }

  @Override
  public Set<SpatialNodeData> getSpatialNodeData(final LongStream nodeIds) {
    // TODO Implement something
    return null;
  }

  @Override
  public Optional<Long> getWayIdByName(final String name) {
    // TODO Implement something
    return null;
  }

  @Override
  public void offerOsmEntities(final Iterable<OsmEntity> entities) {
    offerOsmEntities(StreamSupport.stream(entities.spliterator(), false));
  }

  @Override
  public void offerOsmEntities(final Stream<OsmEntity> entities) {
    // TODO Implement something
  }

}
