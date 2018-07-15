package de.unifreiburg.informatik.cobweb.db;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.unifreiburg.informatik.cobweb.routing.parsing.osm.IdMapping;

/**
 * Interface for databases that provide data relevant for routing.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IRoutingDatabase {
  /**
   * Gets highway data for all highways in the database that have one of the
   * given way IDs.
   *
   * @param wayIds The way IDs to get highway data for
   * @param size   The amount of way IDs to get data for, i.e. the size of
   *               <tt>wayIds</tt>. This value must be set correctly.
   * @return Highway data for all highways that match the given way IDs. If no
   *         highway data was found for a way ID, then it is not contained in
   *         the resulting collection. No guarantees on the order of the
   *         resulting collection are made, especially not that it matches the
   *         order of the given way IDs.
   */
  Collection<HighwayData> getHighwayData(Iterable<Long> wayIds, int size);

  /**
   * Gets highway data for all highways in the database that have one of the
   * given way IDs.
   *
   * @param wayIds The way IDs to get highway data for
   * @param size   The amount of way IDs to get data for, i.e. the size of
   *               <tt>wayIds</tt>. This value must be set correctly.
   * @return Highway data for all highways that match the given way IDs. If no
   *         highway data was found for a way ID, then it is not contained in
   *         the resulting collection. No guarantees on the order of the
   *         resulting collection are made, especially not that it matches the
   *         order of the given way IDs.
   */
  Collection<HighwayData> getHighwayData(LongStream wayIds, int size);

  /**
   * Attempts to get the unique internal ID of a node by its OSM ID.
   *
   * @param osmId The unique OSM ID of the node
   * @return The unique internal ID of the node or empty if no node with that
   *         OSM ID could be found
   */
  Optional<Integer> getInternalNodeByOsm(long osmId);

  /**
   * Attempts to get the unique internal ID of a way by its OSM ID.
   *
   * @param osmId The unique OSM ID of the way
   * @return The unique internal ID of the way or empty if no way with that OSM
   *         ID could be found
   */
  Optional<Integer> getInternalWayByOsm(long osmId);

  /**
   * Attempts to get the unique OSM ID of a node by its name.
   *
   * @param name The name of the node
   * @return The unique OSM ID of the node or empty if no node with that name
   *         could be found
   */
  Optional<Long> getNodeByName(String name);

  /**
   * Attempts to get the name of a node by its unique OSM ID.
   *
   * @param id The id of the node
   * @return The name of the node or empty if no node with the given ID could be
   *         found
   */
  Optional<String> getNodeName(long id);

  /**
   * Attempts to get the unique OSM ID of a node by its internal ID.
   *
   * @param internalId The unique internal ID of the node
   * @return The unique OSM ID of the node or empty if no node with that
   *         internal ID could be found
   */
  Optional<Long> getOsmNodeByInternal(int internalId);

  /**
   * Attempts to get the unique OSM ID of a way by its internal ID.
   *
   * @param internalId The unique internal ID of the way
   * @return The unique OSM ID of the way or empty if no way with that internal
   *         ID could be found
   */
  Optional<Long> getOsmWayByInternal(int internalId);

  /**
   * Gets spatial data for all nodes in the database that match one of the given
   * node IDs.
   *
   * @param nodeIds The node IDs to get spatial data for
   * @param size    The amount of node IDs to get data for, i.e. the size of
   *                <tt>nodeIds</tt>. This value must be set correctly.
   * @return Spatial data for all nodes that match the given node IDs. If no
   *         spatial data was found for a node ID, then it is not contained in
   *         the resulting collection. No guarantees on the order of the
   *         resulting collection are made, especially not that it matches the
   *         order of the given node IDs.
   */
  Collection<SpatialNodeData> getSpatialNodeData(Iterable<Long> nodeIds, int size);

  /**
   * Gets spatial data for all nodes in the database that match one of the given
   * node IDs.
   *
   * @param nodeIds The node IDs to get spatial data for
   * @param size    The amount of node IDs to get data for, i.e. the size of
   *                <tt>nodeIds</tt>. This value must be set correctly.
   * @return Spatial data for all nodes that match the given node IDs. If no
   *         spatial data was found for a node ID, then it is not contained in
   *         the resulting collection. No guarantees on the order of the
   *         resulting collection are made, especially not that it matches the
   *         order of the given node IDs.
   */
  Collection<SpatialNodeData> getSpatialNodeData(LongStream nodeIds, int size);

  /**
   * Attempts to get the unique OSM ID of a way by its name.
   *
   * @param name The name of the way
   * @return The unique OSM ID of the way or empty if no way with that name
   *         could be found
   */
  Optional<Long> getWayByName(String name);

  /**
   * Attempts to get the name of a way by its unique OSM ID.
   *
   * @param id The id of the way
   * @return The name of the way or empty if no way with the given ID could be
   *         found
   */
  Optional<String> getWayName(long id);

  /**
   * Initializes this database. Call this method prior to using the database.
   * Use {@link #shutdown()} when finished using it. Do not call this method
   * after the database was shutdown, instead create a new one.
   */
  void initialize();

  /**
   * Offers ID mappings to the database. The database will save all data it did
   * not already contain.<br>
   * <br>
   * To improve performance this method should be called with big amounts of
   * mappings since a connection will be established for each call.
   *
   * @param mappings The mappings to offer
   * @param size     The amount of mappings to offer, i.e. the size of
   *                 <tt>mappings</tt>. This value must be set correctly.
   */
  void offerIdMappings(Iterable<IdMapping> mappings, int size);

  /**
   * Offers ID mappings to the database. The database will save all data it did
   * not already contain.<br>
   * <br>
   * To improve performance this method should be called with big amounts of
   * mappings since a connection will be established for each call.
   *
   * @param mappings The mappings to offer
   * @param size     The amount of mappings to offer, i.e. the size of
   *                 <tt>mappings</tt>. This value must be set correctly.
   */
  void offerIdMappings(Stream<IdMapping> mappings, int size);

  /**
   * Offers OSM entities to the database. The database will save all data it did
   * not already contain.<br>
   * <br>
   * To improve performance this method should be called with big amounts of
   * entities since a connection will be established for each call.
   *
   * @param entities The entities to offer
   * @param size     The amount of entities to offer, i.e. the size of
   *                 <tt>entities</tt>. This value must be set correctly.
   */
  void offerOsmEntities(Iterable<OsmEntity> entities, int size);

  /**
   * Offers OSM entities to the database. The database will save all data it did
   * not already contain.<br>
   * <br>
   * To improve performance this method should be called with big amounts of
   * entities since a connection will be established for each call.
   *
   * @param entities The entities to offer
   * @param size     The amount of entities to offer, i.e. the size of
   *                 <tt>entities</tt>. This value must be set correctly.
   */
  void offerOsmEntities(Stream<OsmEntity> entities, int size);

  /**
   * Shuts the database down. Call this method when finished using it. Once the
   * database was shutdown it should not be used anymore. Instead create a new
   * one.
   */
  void shutdown();
}
