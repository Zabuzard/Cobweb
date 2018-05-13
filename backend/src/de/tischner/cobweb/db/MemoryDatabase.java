package de.tischner.cobweb.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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

/**
 * Implementation of a {@link IRoutingDatabase} which operates on an internal
 * in-memory database. Depending on the size of the database the memory
 * consumption can be quite high.<br>
 * <br>
 * Use {@link #initialize()} before using the database and {@link #shutdown()}
 * when finished using the it.<br>
 * <br>
 * Push data to the database by using {@link #offerOsmEntities(Iterable, int)}
 * and similar methods.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public class MemoryDatabase extends ADatabase {
  /**
   * The logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(MemoryDatabase.class);
  /**
   * Map connecting node names to their unique OSM IDs.
   */
  private final Map<String, Long> mNameToNode;
  /**
   * Map connecting way names to their unique OSM IDs.
   */
  private final Map<String, Long> mNameToWay;
  /**
   * Map connecting nodes IDs to their OSM name.
   */
  private final Map<Long, String> mNodeToName;
  /**
   * Map connecting node IDs to their spatial data.
   */
  private final Map<Long, SpatialNodeData> mNodeToSpatialData;
  /**
   * Map connecting way IDs to their highway data.
   */
  private final Map<Long, HighwayData> mWayToHighwayData;
  /**
   * Map connecting way IDs to their OSM names.
   */
  private final Map<Long, String> mWayToName;

  /**
   * Creates a new empty database.<br>
   * <br>
   * Use {@link #initialize()} before using the database and {@link #shutdown()}
   * when finished using it.
   */
  public MemoryDatabase() {
    mNameToNode = new HashMap<>();
    mNodeToSpatialData = new HashMap<>();
    mNameToWay = new HashMap<>();
    mNodeToName = new HashMap<>();
    mWayToName = new HashMap<>();
    mWayToHighwayData = new HashMap<>();
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.INameSearchDatabase#getAllNodeNameData()
   */
  @Override
  public Collection<NodeNameData> getAllNodeNameData() {
    return mNodeToName.entrySet().stream().map(entry -> new NodeNameData(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.db.IRoutingDatabase#getHighwayData(java.util.stream.
   * LongStream, int)
   */
  @Override
  public Collection<HighwayData> getHighwayData(final LongStream wayIds, final int size) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting highway data for {} ways", size);
    }
    final List<HighwayData> result = new ArrayList<>(size);
    wayIds.mapToObj(mWayToHighwayData::get).filter(data -> !Objects.isNull(data)).forEach(result::add);
    return result;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getNodeByName(java.lang.String)
   */
  @Override
  public Optional<Long> getNodeByName(final String name) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting node by name {}", name);
    }
    return Optional.ofNullable(mNameToNode.get(name));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getNodeName(long)
   */
  @Override
  public Optional<String> getNodeName(final long id) {
    return Optional.ofNullable(mNodeToName.get(id));
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.db.IRoutingDatabase#getSpatialNodeData(java.util.stream.
   * LongStream, int)
   */
  @Override
  public Collection<SpatialNodeData> getSpatialNodeData(final LongStream nodeIds, final int size) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting spatial data for {} nodes", size);
    }
    final List<SpatialNodeData> result = new ArrayList<>(size);
    nodeIds.mapToObj(mNodeToSpatialData::get).filter(data -> !Objects.isNull(data)).forEach(result::add);
    return result;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getWayByName(java.lang.String)
   */
  @Override
  public Optional<Long> getWayByName(final String name) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting way by name {}", name);
    }
    return Optional.ofNullable(mNameToWay.get(name));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getWayName(long)
   */
  @Override
  public Optional<String> getWayName(final long id) {
    return Optional.ofNullable(mWayToName.get(id));
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#initialize()
   */
  @Override
  public void initialize() {
    // Do nothing
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.db.IRoutingDatabase#offerOsmEntities(java.util.stream.
   * Stream, int)
   */
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

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#shutdown()
   */
  @Override
  public void shutdown() {
    LOGGER.info("Shutting down database");
    // Do nothing
  }

  /**
   * Adds the given OSM node to the database if not already contained.
   *
   * @param node The node to add to the database
   */
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

  /**
   * Adds the given OSM way to the database if not already contained.
   *
   * @param way The way to add to the database
   */
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
