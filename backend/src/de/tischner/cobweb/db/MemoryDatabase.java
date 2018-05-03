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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.parsing.osm.OsmParseUtil;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public class MemoryDatabase extends ARoutingDatabase {
  private final static Logger LOGGER = LoggerFactory.getLogger(MemoryDatabase.class);
  private final Map<String, Long> mNameToNode;
  private final Map<String, Long> mNameToWay;
  private final Map<Long, String> mNodeToName;
  private final Map<Long, SpatialNodeData> mNodeToSpatialData;
  private final Map<Long, HighwayData> mWayToHighwayData;
  private final Map<Long, String> mWayToName;

  public MemoryDatabase() {
    mNameToNode = new HashMap<>();
    mNodeToSpatialData = new HashMap<>();
    mNameToWay = new HashMap<>();
    mNodeToName = new HashMap<>();
    mWayToName = new HashMap<>();
    mWayToHighwayData = new HashMap<>();
  }

  @Override
  public Collection<HighwayData> getHighwayData(final LongStream wayIds, final int size) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting highway data for {} ways", size);
    }
    final List<HighwayData> result = new ArrayList<>(size);
    wayIds.mapToObj(mWayToHighwayData::get).filter(data -> !Objects.isNull(data)).forEach(result::add);
    return result;
  }

  @Override
  public Optional<Long> getNodeByName(final String name) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting node by name {}", name);
    }
    return Optional.ofNullable(mNameToNode.get(name));
  }

  @Override
  public Optional<String> getNodeName(final long id) {
    return Optional.ofNullable(mNodeToName.get(id));
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
  public Optional<Long> getWayByName(final String name) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting way by name {}", name);
    }
    return Optional.ofNullable(mNameToWay.get(name));
  }

  @Override
  public Optional<String> getWayName(final long id) {
    return Optional.ofNullable(mWayToName.get(id));
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
    final String name = tagToValue.get(OsmParseUtil.NAME_TAG);

    // Insert node data
    mNodeToSpatialData.put(id, new SpatialNodeData(id, latitude, longitude));

    // Insert tag data
    if (name != null) {
      mNameToNode.put(name, id);
      mNodeToName.put(id, name);
    }
  }

  private void addOsmWay(final OsmWay way) {
    // Retrieve information
    final long id = way.getId();
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(way);
    final String name = tagToValue.get(OsmParseUtil.NAME_TAG);
    final EHighwayType highway = OsmParseUtil.parseHighwayType(tagToValue);
    final int maxSpeed = OsmParseUtil.parseMaxSpeed(tagToValue);

    // Insert tag data
    if (name != null) {
      mNameToWay.put(name, id);
      mWayToName.put(id, name);
    }
    mWayToHighwayData.put(id, new HighwayData(id, highway, maxSpeed));
  }

}
