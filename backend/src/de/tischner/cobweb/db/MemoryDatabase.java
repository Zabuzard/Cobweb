package de.tischner.cobweb.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public class MemoryDatabase implements IRoutingDatabase {
  private final Map<String, Long> mNameToNode;
  private final Map<String, Long> mNameToWay;
  private final Map<Long, SpatialNodeData> mNodeToSpatialData;

  public MemoryDatabase() {
    mNameToNode = new HashMap<>();
    mNodeToSpatialData = new HashMap<>();
    mNameToWay = new HashMap<>();
  }

  @Override
  public Optional<Long> getNodeByName(final String name) {
    return Optional.ofNullable(mNameToNode.get(name));
  }

  @Override
  public Set<SpatialNodeData> getSpatialNodeData(final Iterable<Long> nodeIds, final int size) {
    return getSpatialNodeData(StreamSupport.stream(nodeIds.spliterator(), false).mapToLong(l -> (long) l), size);
  }

  @Override
  public Set<SpatialNodeData> getSpatialNodeData(final LongStream nodeIds, final int size) {
    final Set<SpatialNodeData> result = new HashSet<>(size);
    nodeIds.mapToObj(mNodeToSpatialData::get).filter(data -> !Objects.isNull(data)).forEach(result::add);
    return result;
  }

  @Override
  public Optional<Long> getWayIdByName(final String name) {
    return Optional.ofNullable(mNameToWay.get(name));
  }

  @Override
  public void offerOsmEntities(final Iterable<OsmEntity> entities, final int size) {
    offerOsmEntities(StreamSupport.stream(entities.spliterator(), false), size);
  }

  @Override
  public void offerOsmEntities(final Stream<OsmEntity> entities, final int size) {
    entities.forEach(entity -> {
      if (entity instanceof OsmNode) {
        addOsmNode((OsmNode) entity);
      } else if (entity instanceof OsmWay) {
        addOsmWay((OsmWay) entity);
      }
    });
  }

  private void addOsmNode(final OsmNode node) {
    // Retrieve information
    final long id = node.getId();
    final double latitude = node.getLatitude();
    final double longitude = node.getLongitude();
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(node);
    final String name = tagToValue.get(DatabaseUtil.NAME_TAG);

    // Insert node data
    mNodeToSpatialData.put(id, new SpatialNodeData(id, latitude, longitude));

    // Insert tag data
    if (name != null) {
      mNameToNode.put(name, id);
    }
  }

  private void addOsmWay(final OsmWay way) {
    // Retrieve information
    final long id = way.getId();
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(way);
    final String name = tagToValue.get(DatabaseUtil.NAME_TAG);

    // Insert tag data
    if (name != null) {
      mNameToWay.put(name, id);
    }
  }

}
