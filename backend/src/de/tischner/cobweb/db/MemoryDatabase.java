package de.tischner.cobweb.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableLongIntMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.factory.primitive.IntLongMaps;
import org.eclipse.collections.impl.factory.primitive.LongIntMaps;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectLongMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.parsing.osm.OsmParseUtil;
import de.tischner.cobweb.routing.parsing.osm.IdMapping;
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
   * Map connecting internal node IDs to their OSM IDs.
   */
  private final MutableIntLongMap mInternalToNodeId;
  /**
   * Map connecting internal way IDs to their OSM IDs.
   */
  private final MutableIntLongMap mInternalToWayId;
  /**
   * Map connecting node names to their unique OSM IDs.
   */
  private final MutableObjectLongMap<String> mNameToNode;
  /**
   * Map connecting way names to their unique OSM IDs.
   */
  private final MutableObjectLongMap<String> mNameToWay;
  /**
   * Map connecting nodes IDs to their OSM name.
   */
  private final MutableLongObjectMap<String> mNodeToName;
  /**
   * Map connecting node IDs to their spatial data.
   */
  private final MutableLongObjectMap<SpatialNodeData> mNodeToSpatialData;
  /**
   * Map connecting OSM node IDs to their internal IDs.
   */
  private final MutableLongIntMap mOsmToNodeId;
  /**
   * Map connecting OSM way IDs to their internal IDs.
   */
  private final MutableLongIntMap mOsmToWayId;
  /**
   * Map connecting way IDs to their highway data.
   */
  private final MutableLongObjectMap<HighwayData> mWayToHighwayData;
  /**
   * Map connecting way IDs to their OSM names.
   */
  private final MutableLongObjectMap<String> mWayToName;

  /**
   * Creates a new empty database.<br>
   * <br>
   * Use {@link #initialize()} before using the database and {@link #shutdown()}
   * when finished using it.
   */
  public MemoryDatabase() {
    mNameToNode = ObjectLongMaps.mutable.empty();
    mNodeToSpatialData = LongObjectMaps.mutable.empty();
    mNameToWay = ObjectLongMaps.mutable.empty();
    mNodeToName = LongObjectMaps.mutable.empty();
    mWayToName = LongObjectMaps.mutable.empty();
    mWayToHighwayData = LongObjectMaps.mutable.empty();
    mInternalToNodeId = IntLongMaps.mutable.empty();
    mInternalToWayId = IntLongMaps.mutable.empty();
    mOsmToNodeId = LongIntMaps.mutable.empty();
    mOsmToWayId = LongIntMaps.mutable.empty();
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.INameSearchDatabase#getAllNodeNameData()
   */
  @Override
  public Collection<NodeNameData> getAllNodeNameData() {
    return mNodeToName.keyValuesView().collect(entry -> new NodeNameData(entry.getOne(), entry.getTwo())).toList();
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
   * @see de.tischner.cobweb.db.IRoutingDatabase#getInternalNodeByOsm(long)
   */
  @Override
  public Optional<Integer> getInternalNodeByOsm(final long osmId) {
    if (mOsmToNodeId.containsKey(osmId)) {
      return Optional.of(mOsmToNodeId.get(osmId));
    }
    return Optional.empty();
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getInternalWayByOsm(long)
   */
  @Override
  public Optional<Integer> getInternalWayByOsm(final long osmId) {
    if (mOsmToWayId.containsKey(osmId)) {
      return Optional.of(mOsmToWayId.get(osmId));
    }
    return Optional.empty();
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getNodeByName(java.lang.String)
   */
  @Override
  public Optional<Long> getNodeByName(final String name) {
    if (mNameToNode.containsKey(name)) {
      return Optional.of(mNameToNode.get(name));
    }
    return Optional.empty();
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
   * @see de.tischner.cobweb.db.IRoutingDatabase#getOsmNodeByInternal(int)
   */
  @Override
  public Optional<Long> getOsmNodeByInternal(final int internalId) {
    if (mInternalToNodeId.containsKey(internalId)) {
      return Optional.of(mInternalToNodeId.get(internalId));
    }
    return Optional.empty();
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getOsmWayByInternal(int)
   */
  @Override
  public Optional<Long> getOsmWayByInternal(final int internalId) {
    if (mInternalToWayId.containsKey(internalId)) {
      return Optional.of(mInternalToWayId.get(internalId));
    }
    return Optional.empty();
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
    nodeIds.mapToObj(mNodeToSpatialData::get).filter(data -> !Objects.isNull(data)).forEach(data -> {
      // Fetch internal ID of the node
      final SpatialNodeData fullData = new SpatialNodeData(mOsmToNodeId.get(data.getOsmId()), data.getOsmId(),
          data.getLatitude(), data.getLongitude());
      result.add(fullData);
    });
    return result;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getWayByName(java.lang.String)
   */
  @Override
  public Optional<Long> getWayByName(final String name) {
    if (mNameToWay.containsKey(name)) {
      return Optional.of(mNameToWay.get(name));
    }
    return Optional.empty();
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
   * de.tischner.cobweb.db.IRoutingDatabase#offerIdMappings(java.util.stream.
   * Stream, int)
   */
  @Override
  public void offerIdMappings(final Stream<IdMapping> mappings, final int size) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Offering {} mappings to the database", size);
    }

    mappings.forEach(mapping -> {
      if (mapping.isNode()) {
        mInternalToNodeId.put(mapping.getInternalId(), mapping.getOsmId());
        mOsmToNodeId.put(mapping.getOsmId(), mapping.getInternalId());
      } else {
        mInternalToWayId.put(mapping.getInternalId(), mapping.getOsmId());
        mOsmToWayId.put(mapping.getOsmId(), mapping.getInternalId());
      }
    });
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
    final long osmId = node.getId();
    final float latitude = (float) node.getLatitude();
    final float longitude = (float) node.getLongitude();
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(node);
    final String name = tagToValue.get(OsmParseUtil.NAME_TAG);

    // Insert node data, internal ID is implicitly fetched at request time
    mNodeToSpatialData.put(osmId, new SpatialNodeData(-1, osmId, latitude, longitude));

    // Insert tag data
    if (name != null) {
      mNameToNode.put(name, osmId);
      mNodeToName.put(osmId, name);
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
