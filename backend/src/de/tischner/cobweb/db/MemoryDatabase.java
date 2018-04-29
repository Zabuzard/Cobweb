package de.tischner.cobweb.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public class MemoryDatabase implements IRoutingDatabase {
  private final static Logger LOGGER = LoggerFactory.getLogger(MemoryDatabase.class);
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
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting node by name {}", name);
    }
    return Optional.ofNullable(mNameToNode.get(name));
  }

  @Override
  public Collection<SpatialNodeData> getSpatialNodeData(final Iterable<Long> nodeIds, final int size) {
    return getSpatialNodeData(StreamSupport.stream(nodeIds.spliterator(), false).mapToLong(l -> (long) l), size);
  }

  @Override
  public Collection<SpatialNodeData> getSpatialNodeData(final LongStream nodeIds, final int size) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting spatial data for {} nodes", size);
    }
    final List<SpatialNodeData> result = new ArrayList<>(size);
    nodeIds.mapToObj(mNodeToSpatialData::get).filter(data -> !Objects.isNull(data)).forEach(result::add);
    return result;
  }

  @Override
  public Optional<Long> getWayIdByName(final String name) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting way by name {}", name);
    }
    return Optional.ofNullable(mNameToWay.get(name));
  }

  @Override
  public void offerOsmEntities(final Iterable<OsmEntity> entities, final int size) {
    offerOsmEntities(StreamSupport.stream(entities.spliterator(), false), size);
  }

  @Override
  public void offerOsmEntities(final Stream<OsmEntity> entities, final int size) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Offering {} nodes to the database", size);
    }

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
