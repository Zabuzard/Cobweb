package de.tischner.cobweb.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Utility class that provides common methods and values used by databases. Such
 * as SQL query templates or methods used to parse database results.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class DatabaseUtil {
  /**
   * SQL query to fetch all node name data. This selects node IDs and names of
   * entries that have a name.
   */
  static final String QUERY_ALL_NODE_NAME_DATA = "SELECT id, name FROM osm_node_tags WHERE name IS NOT NULL";
  /**
   * Delimiter used in SQL insert statements to separate the data that is to be
   * inserted.
   */
  static final String QUERY_DATA_DELIMITER = ", ";
  /**
   * Prefix of the SQL query to fetch highway data for given way IDs.
   */
  static final String QUERY_HIGHWAY_DATA_PREFIX = "SELECT id, highway, maxspeed FROM osm_way_tags WHERE id IN (";
  /**
   * SQL query to insert a node. Contains placeholder values for the node ID,
   * latitude and longitude.
   */
  static final String QUERY_INSERT_NODE = "REPLACE INTO osm_nodes (id, latitude, longitude) VALUES (?, ?, ?)";
  /**
   * SQL query to insert an ID mapping for nodes. Contains placeholder values
   * for the internal and OSM node ID.
   */
  static final String QUERY_INSERT_NODE_MAPPING = "REPLACE INTO osm_node_mappings (internal_id, osm_id) VALUES (?, ?)";
  /**
   * SQL query to insert node tag data. Contains placeholder values for the node
   * ID, name and highway tags.
   */
  static final String QUERY_INSERT_NODE_TAGS = "REPLACE INTO osm_node_tags (id, name, highway) VALUES (?, ?, ?)";
  /**
   * Suffix for a SQL insert query. Used right after the data that is to be
   * inserted.
   */
  static final String QUERY_INSERT_SUFFIX = ")";
  /**
   * SQL query to insert an ID mapping for ways. Contains placeholder values for
   * the internal and OSM node ID.
   */
  static final String QUERY_INSERT_WAY_MAPPING = "REPLACE INTO osm_way_mappings (internal_id, osm_id) VALUES (?, ?)";
  /**
   * SQL query to insert way tag data. Contains placeholder values for the way
   * ID, name, highway and maxspeed tags.
   */
  static final String QUERY_INSERT_WAY_TAGS =
      "REPLACE INTO osm_way_tags (id, name, highway, maxspeed) VALUES (?, ?, ?, ?)";
  /**
   * SQL query to fetch the ID of a node by its name. Contains a placeholder
   * value for the node name.
   */
  static final String QUERY_NODE_ID_BY_NAME = "SELECT id FROM osm_node_tags WHERE name = ?";
  /**
   * SQL query to fetch the internal ID of a node by its OSM ID. Contains a
   * placeholder value for the OSM ID.
   */
  static final String QUERY_NODE_INTERNAL_BY_OSM = "SELECT internal_id FROM osm_node_mappings WHERE osm_id = ?";
  /**
   * SQL query to fetch the name of a node by its ID. Contains a placeholder
   * value for the node ID.
   */
  static final String QUERY_NODE_NAME_BY_ID = "SELECT name FROM osm_node_tags WHERE id = ?";
  /**
   * SQL query to fetch the OSM ID of a node by its internal ID. Contains a
   * placeholder value for the internal ID.
   */
  static final String QUERY_NODE_OSM_BY_INTERNAL = "SELECT osm_id FROM osm_node_mappings WHERE internal_id = ?";
  /**
   * Placeholder value to use for prepared SQL statements.
   */
  static final String QUERY_PLACEHOLDER = "?";
  /**
   * Prefix of the SQL query to fetch spatial node data for given node IDs.
   */
  static final String QUERY_SPATIAL_NODE_DATA_PREFIX =
      "SELECT mappings.osm_id, mappings.internal_id, nodes.latitude, nodes.longitude FROM osm_nodes AS nodes,"
          + " osm_node_mappings AS mappings WHERE nodes.id = mappings.osm_id AND nodes.id IN (";
  /**
   * SQL query to fetch the id of a way by its name. Contains a placeholder
   * value for the way name.
   */
  static final String QUERY_WAY_ID_BY_NAME = "SELECT id FROM osm_way_tags WHERE name = ?";
  /**
   * SQL query to fetch the internal ID of a way by its OSM ID. Contains a
   * placeholder value for the OSM ID.
   */
  static final String QUERY_WAY_INTERNAL_BY_OSM = "SELECT internal_id FROM osm_way_mappings WHERE osm_id = ?";
  /**
   * SQL query to fetch the name of a way by its ID. Contains a placeholder
   * value for the way ID.
   */
  static final String QUERY_WAY_NAME_BY_ID = "SELECT name FROM osm_way_tags WHERE id = ?";
  /**
   * SQL query to fetch the OSM ID of a way by its internal ID. Contains a
   * placeholder value for the internal ID.
   */
  static final String QUERY_WAY_OSM_BY_INTERNAL = "SELECT osm_id FROM osm_way_mappings WHERE internal_id = ?";

  /**
   * Sets the given value to the given index position of the statement or
   * <tt>SQL NULL</tt> if the value is <tt>null</tt>.<br>
   * <br>
   * The implementation is equivalent to calling
   * {@code statement.setNull(index, Types.INTEGER)} if <tt>value</tt> is
   * <tt>null</tt> or {@code statement.setInt(index, value)} otherwise.
   *
   * @param index     The index position of the statement to set the value to
   * @param value     The value to set or <tt>null</tt> if it should be set to
   *                  <tt>SQL NULL</tt>
   * @param statement The statement to set the value for
   * @throws SQLException If an SQLException occurred while setting the value,
   *                      like an invalid index
   */
  static void setIntOrNull(final int index, final Integer value, final PreparedStatement statement)
      throws SQLException {
    if (value == null) {
      statement.setNull(index, Types.INTEGER);
    } else {
      statement.setInt(index, value);
    }
  }

  /**
   * Sets the given value to the given index position of the statement or
   * <tt>SQL NULL</tt> if the value is <tt>null</tt>.<br>
   * <br>
   * The implementation is equivalent to calling
   * {@code statement.setNull(index, Types.VARCHAR)} if <tt>value</tt> is
   * <tt>null</tt> or {@code statement.setString(index, value)} otherwise.
   *
   * @param index     The index position of the statement to set the value to
   * @param value     The value to set or <tt>null</tt> if it should be set to
   *                  <tt>SQL NULL</tt>
   * @param statement The statement to set the value for
   * @throws SQLException If an SQLException occurred while setting the value,
   *                      like an invalid index
   */
  static void setStringOrNull(final int index, final String value, final PreparedStatement statement)
      throws SQLException {
    if (value == null) {
      statement.setNull(index, Types.VARCHAR);
    } else {
      statement.setString(index, value);
    }
  }

  /**
   * Utility class. No implementation.
   */
  private DatabaseUtil() {

  }
}
