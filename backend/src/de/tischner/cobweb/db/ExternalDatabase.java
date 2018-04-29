package de.tischner.cobweb.db;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.IDatabaseConfigProvider;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public final class ExternalDatabase implements IRoutingDatabase {

  private final static Logger LOGGER = LoggerFactory.getLogger(ExternalDatabase.class);
  private final IDatabaseConfigProvider mConfig;

  public ExternalDatabase(final IDatabaseConfigProvider config) {
    mConfig = config;
  }

  @Override
  public Optional<Long> getNodeByName(final String name) {
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(DatabaseUtil.QUERY_NODE_NAME_BY_ID)) {
        statement.setString(1, name);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Getting node by name {}, query is: {}", name, statement);
        }
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return Optional.of(result.getLong(1));
          }
          return Optional.empty();
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting node by name: {}", name, e);
      return Optional.empty();
    }
  }

  @Override
  public Collection<SpatialNodeData> getSpatialNodeData(final Iterable<Long> nodeIds, final int size) {
    return getSpatialNodeData(StreamSupport.stream(nodeIds.spliterator(), false).mapToLong(l -> (long) l), size);
  }

  @Override
  public Collection<SpatialNodeData> getSpatialNodeData(final LongStream nodeIds, final int size) {
    // Build the query
    final StringJoiner queryBuilder = new StringJoiner(DatabaseUtil.QUERY_SPATIAL_NODE_DATA_DELIMITER,
        DatabaseUtil.QUERY_SPATIAL_NODE_DATA_PREFIX, DatabaseUtil.QUERY_SPATIAL_NODE_DATA_SUFFIX);
    IntStream.range(0, size).forEach(i -> queryBuilder.add(DatabaseUtil.QUERY_PLACEHOLDER));

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting spatial data for {} nodes, query is: {}", size, queryBuilder.toString());
    }
    final List<SpatialNodeData> nodeData = new ArrayList<>(size);
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
        // Fill the statement
        final AtomicInteger counter = new AtomicInteger();
        nodeIds.forEach(id -> {
          try {
            statement.setLong(counter.incrementAndGet(), id);
          } catch (final SQLException e) {
            LOGGER.error("Error getting spatial data for {} nodes, can not set parameter {} with node id {}", size,
                counter.get(), id, e);
          }
        });

        // Execute the statement and collect the result
        try (ResultSet result = statement.executeQuery()) {
          while (result.next()) {
            final long id = result.getLong(1);
            final double latitude = result.getDouble(2);
            final double longitude = result.getDouble(3);
            nodeData.add(new SpatialNodeData(id, latitude, longitude));
          }
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting spatial data for {} nodes, current result is {}", size, nodeData, e);
    }

    return nodeData;
  }

  @Override
  public Optional<Long> getWayIdByName(final String name) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting way by name: {}", name);
    }
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(DatabaseUtil.QUERY_WAY_NAME_BY_ID)) {
        statement.setString(1, name);
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return Optional.of(result.getLong(1));
          }
          return Optional.empty();
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting way by name: {}", name, e);
      return Optional.empty();
    }
  }

  public void initialize() throws SQLException, IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Ensuring database has correct layout.");
    }
    // Create database tables if they don't exist already
    final Path initDbScript = mConfig.getInitDbScript();
    ScriptExecutor.executeScript(initDbScript, createConnection());
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
    try (Connection connection = createConnection()) {
      connection.setAutoCommit(false);

      // Queue all queries
      entities.forEach(entity -> {
        try {
          if (entity instanceof OsmNode) {
            queueOsmNode((OsmNode) entity, connection);
          } else if (entity instanceof OsmWay) {
            queueOsmWay((OsmWay) entity, connection);
          }
        } catch (final SQLException e) {
          LOGGER.error("Error queueing entity for database insertion: {}", entity, e);
        }
      });

      // Submit all queries
      connection.commit();
      connection.setAutoCommit(true);
    } catch (final SQLException e) {
      LOGGER.error("Error offering {} nodes to the database", size, e);
    }
  }

  public void shutdown() {
    LOGGER.info("Shutting down database");
    // TODO Implement something
  }

  private Connection createConnection() throws SQLException {
    return DriverManager.getConnection(mConfig.getJDBCUrl());
  }

  private void queueOsmNode(final OsmNode node, final Connection connection) throws SQLException {
    // Retrieve information
    final long id = node.getId();
    final double latitude = node.getLatitude();
    final double longitude = node.getLongitude();
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(node);
    final String name = tagToValue.get(DatabaseUtil.NAME_TAG);
    final String highway = tagToValue.get(DatabaseUtil.HIGHWAY_TAG);

    // Insert node data
    try (PreparedStatement nodeStatement = connection.prepareStatement(DatabaseUtil.QUERY_INSERT_NODE)) {
      nodeStatement.setLong(1, id);
      nodeStatement.setDouble(2, latitude);
      nodeStatement.setDouble(3, longitude);
      nodeStatement.executeUpdate();
    }

    // Insert tag data
    try (PreparedStatement tagStatement = connection.prepareStatement(DatabaseUtil.QUERY_INSERT_NODE_TAGS)) {
      tagStatement.setLong(1, id);
      DatabaseUtil.setStringOrNull(2, name, tagStatement);
      DatabaseUtil.setStringOrNull(3, highway, tagStatement);
      tagStatement.executeUpdate();
    }
  }

  private void queueOsmWay(final OsmWay way, final Connection connection) throws SQLException {
    // Retrieve information
    final long wayId = way.getId();
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(way);
    final String name = tagToValue.get(DatabaseUtil.NAME_TAG);
    final String highway = tagToValue.get(DatabaseUtil.HIGHWAY_TAG);
    final Integer maxSpeed = DatabaseUtil.parseMaxSpeed(tagToValue.get(DatabaseUtil.MAXSPEED_TAG));

    // Insert tag data
    try (PreparedStatement tagStatement = connection.prepareStatement(DatabaseUtil.QUERY_INSERT_WAY_TAGS)) {
      tagStatement.setLong(1, wayId);
      DatabaseUtil.setStringOrNull(2, name, tagStatement);
      DatabaseUtil.setStringOrNull(3, highway, tagStatement);
      DatabaseUtil.setIntOrNull(4, maxSpeed, tagStatement);
      tagStatement.executeUpdate();
    }

    // Iterate all nodes and insert way data
    for (int i = 0; i < way.getNumberOfNodes(); i++) {
      final long nodeId = way.getNodeId(i);
      try (PreparedStatement wayStatement = connection.prepareStatement(DatabaseUtil.QUERY_INSERT_WAY)) {
        wayStatement.setLong(1, wayId);
        wayStatement.setLong(2, nodeId);
        wayStatement.executeUpdate();
      }
    }
  }

}
