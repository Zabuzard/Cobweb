package de.tischner.cobweb.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseUtil {
  private final static Logger LOGGER = LoggerFactory.getLogger(DatabaseUtil.class);

  static final String HIGHWAY_TAG = "highway";
  static final String MAXSPEED_TAG = "maxspeed";
  static final String NAME_TAG = "name";
  static final String QUERY_INSERT_NODE = "REPLACE INTO osm_nodes (id, latitude, longitude) VALUES (?, ?, ?)";
  static final String QUERY_INSERT_NODE_TAGS = "REPLACE INTO osm_node_tags (id, name, highway) VALUES (?, ?, ?)";
  static final String QUERY_INSERT_WAY_TAGS = "REPLACE INTO osm_way_tags (id, name, highway, maxspeed) VALUES (?, ?, ?, ?)";
  static final String QUERY_NODE_ID_BY_NAME = "SELECT name FROM osm_node_tags WHERE id = ?";
  static final String QUERY_NODE_NAME_BY_ID = "SELECT id FROM osm_node_tags WHERE name = ?";
  static final String QUERY_PLACEHOLDER = "?";
  static final String QUERY_SPATIAL_NODE_DATA_DELIMITER = ", ";
  static final String QUERY_SPATIAL_NODE_DATA_PREFIX = "SELECT id, latitude, longitude FROM osm_nodes WHERE id IN (";
  static final String QUERY_SPATIAL_NODE_DATA_SUFFIX = ")";
  static final String QUERY_WAY_ID_BY_NAME = "SELECT name FROM osm_way_tags WHERE id = ?";
  static final String QUERY_WAY_NAME_BY_ID = "SELECT id FROM osm_way_tags WHERE name = ?";

  static Integer parseMaxSpeed(final String maxSpeedText) {
    // TODO Make this method more modular, out-source to a dedicated OSM tag parser
    if (maxSpeedText == null) {
      return null;
    }
    // "none" means there is no speed limit, use a default value
    if (maxSpeedText.equals("none")) {
      return 120;
    }
    // "walk" means the speed limit is at which humans tend to walk
    if (maxSpeedText.equals("walk")) {
      return 7;
    }
    // "signals" means the speed limit changes according to signal signs, use a
    // default value
    if (maxSpeedText.equals("signals")) {
      return 120;
    }
    // TODO Format allows '60' (then its kmh) but also '60 mph' (then we need to
    // convert)
    // TODO Current errors: '5 mph', '50;100' and '100, 70'
    try {
      return Integer.parseInt(maxSpeedText);
    } catch (final NumberFormatException e) {
      // Use a default value instead
      LOGGER.error("Can not parse maxspeed value: {}", maxSpeedText);
      return 50;
    }
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
