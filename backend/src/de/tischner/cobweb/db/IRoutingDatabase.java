package de.tischner.cobweb.db;

import java.util.Optional;
import java.util.Set;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import de.topobyte.osm4j.core.model.iface.OsmEntity;

public interface IRoutingDatabase {
  Optional<Long> getNodeByName(String name);

  Set<SpatialNodeData> getSpatialNodeData(Iterable<Long> nodeIds, int size);

  Set<SpatialNodeData> getSpatialNodeData(LongStream nodeIds, int size);

  Optional<Long> getWayIdByName(String name);

  void offerOsmEntities(Iterable<OsmEntity> entities, int size);

  void offerOsmEntities(Stream<OsmEntity> entities, int size);
}
