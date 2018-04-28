package de.tischner.cobweb.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public final class DatabaseUtil {
  static final String HIGHWAY_TAG = "highway";

  static final String MAXSPEED_TAG = "maxspeed";
  static final String NAME_TAG = "name";
  static final String QUERY_INSERT_NODE = "REPLACE INTO osm_nodes (id, latitude, longitude) VALUES (?, ?, ?)";
  static final String QUERY_INSERT_NODE_TAGS = "REPLACE INTO osm_node_tags (id, name, highway) VALUES (?, ?, ?)";
  static final String QUERY_INSERT_WAY = "REPLACE INTO osm_way_nodes (way_id, node_id) VALUES (?, ?)";
  static final String QUERY_INSERT_WAY_TAGS = "REPLACE INTO osm_way_tags (id, name, highway, maxspeed) VALUES (?, ?, ?, ?)";
  static final String QUERY_NODE_NAME_BY_ID = "SELECT id FROM osm_node_tags WHERE name = ?";
  static final String QUERY_PLACEHOLDER = "?";
  static final String QUERY_SPATIAL_NODE_DATA_DELIMITER = ", ";
  static final String QUERY_SPATIAL_NODE_DATA_PREFIX = "SELECT id, latitude, longitude FROM osm_nodes WHERE id IN (";
  static final String QUERY_SPATIAL_NODE_DATA_SUFFIX = ")";
  static final String QUERY_WAY_NAME_BY_ID = "SELECT id FROM osm_way_tags WHERE name = ?";

  static Integer parseMaxSpeed(final String maxSpeedText) {
    if (maxSpeedText == null) {
      return null;
    }
    // TODO Format allows '60' (then its kmh) but also '60 mph' (then we need to
    // convert)
    return Integer.parseInt(maxSpeedText);
  }

  static void setIntOrNull(final int index, final Integer value, final PreparedStatement statement)
      throws SQLException {
    if (value == null) {
      statement.setNull(index, Types.INTEGER);
    } else {
      statement.setInt(index, value);
    }
  }

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
