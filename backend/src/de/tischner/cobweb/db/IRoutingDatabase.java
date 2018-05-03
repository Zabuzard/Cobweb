package de.tischner.cobweb.db;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import de.topobyte.osm4j.core.model.iface.OsmEntity;

public interface IRoutingDatabase {
  Optional<Long> getNodeByName(String name);

  Optional<String> getNodeName(long id);

  Collection<SpatialNodeData> getSpatialNodeData(Iterable<Long> nodeIds, int size);

  Collection<SpatialNodeData> getSpatialNodeData(LongStream nodeIds, int size);

  Optional<Long> getWayByName(String name);

  Optional<String> getWayName(long id);

  void offerOsmEntities(Iterable<OsmEntity> entities, int size);

  void offerOsmEntities(Stream<OsmEntity> entities, int size);
}
