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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.IDatabaseConfigProvider;
import de.tischner.cobweb.parsing.ParseException;
import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.tischner.cobweb.parsing.osm.OsmParseUtil;
import de.tischner.cobweb.routing.parsing.osm.IdMapping;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

/**
 * Implementation of a {@link IRoutingDatabase} which connects to an external
 * database by using JDBC.<br>
 * <br>
 * Settings are provided by a {@link IDatabaseConfigProvider} that is passed to
 * a constructor. Use {@link #initialize()} before using the database and
 * {@link #shutdown()} when finished using the it.<br>
 * <br>
 * Push data to the database by using {@link #offerOsmEntities(Iterable, int)}
 * and similar methods.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ExternalDatabase extends ADatabase {
  /**
   * The logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalDatabase.class);

  /**
   * Pushes the given ID mapping to the database if not already contained.<br>
   * <br>
   * In case multiple mappings are to be pushed, auto-commit should be set to
   * <tt>false</tt> before calling the method. The method will not force a
   * commit on its own.
   *
   * @param mapping    The mapping to push to the database
   * @param connection The connection to the database
   * @throws SQLException If an SQL exception occurred while trying to execute
   *                      the update
   */
  private static void queueMapping(final IdMapping mapping, final Connection connection) throws SQLException {
    String query;
    if (mapping.isNode()) {
      query = DatabaseUtil.QUERY_INSERT_NODE_MAPPING;
    } else {
      query = DatabaseUtil.QUERY_INSERT_WAY_MAPPING;
    }

    // Insert mapping
    try (PreparedStatement nodeStatement = connection.prepareStatement(query)) {
      nodeStatement.setInt(1, mapping.getInternalId());
      nodeStatement.setLong(2, mapping.getOsmId());
      nodeStatement.executeUpdate();
    }
  }

  /**
   * Pushes the given OSM node to the database if not already contained.<br>
   * <br>
   * In case multiple nodes are to be pushed, auto-commit should be set to
   * <tt>false</tt> before calling the method. The method will not force a
   * commit on its own.
   *
   * @param node       The node to push to the database
   * @param connection The connection to the database
   * @throws SQLException If an SQL exception occurred while trying to execute
   *                      the update
   */
  private static void queueOsmNode(final OsmNode node, final Connection connection) throws SQLException {
    // Retrieve information
    final long id = node.getId();
    final float latitude = (float) node.getLatitude();
    final float longitude = (float) node.getLongitude();
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(node);
    final String name = tagToValue.get(OsmParseUtil.NAME_TAG);
    final String highway = tagToValue.get(OsmParseUtil.HIGHWAY_TAG);

    // Insert node data
    try (PreparedStatement nodeStatement = connection.prepareStatement(DatabaseUtil.QUERY_INSERT_NODE)) {
      nodeStatement.setLong(1, id);
      nodeStatement.setFloat(2, latitude);
      nodeStatement.setFloat(3, longitude);
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

  /**
   * Pushes the given OSM way to the database if not already contained.<br>
   * <br>
   * In case multiple ways are to be pushed, auto-commit should be set to
   * <tt>false</tt> before calling the method. The method will not force a
   * commit on its own.
   *
   * @param way        The way to push to the database
   * @param connection The connection to the database
   * @throws SQLException If an SQL exception occurred while trying to execute
   *                      the update
   */
  private static void queueOsmWay(final OsmWay way, final Connection connection) throws SQLException {
    // Retrieve information
    final long wayId = way.getId();
    final Map<String, String> tagToValue = OsmModelUtil.getTagsAsMap(way);
    final String name = tagToValue.get(OsmParseUtil.NAME_TAG);
    final String highway = tagToValue.get(OsmParseUtil.HIGHWAY_TAG);
    Integer maxSpeed = OsmParseUtil.parseMaxSpeed(tagToValue);
    if (maxSpeed == -1) {
      maxSpeed = null;
    }

    // Insert tag data
    try (PreparedStatement tagStatement = connection.prepareStatement(DatabaseUtil.QUERY_INSERT_WAY_TAGS)) {
      tagStatement.setLong(1, wayId);
      DatabaseUtil.setStringOrNull(2, name, tagStatement);
      DatabaseUtil.setStringOrNull(3, highway, tagStatement);
      DatabaseUtil.setIntOrNull(4, maxSpeed, tagStatement);
      tagStatement.executeUpdate();
    }
  }

  /**
   * The configuration provider.
   */
  private final IDatabaseConfigProvider mConfig;

  /**
   * Creates a new external database object which uses the configuration given
   * by the provider.<br>
   * <br>
   * Use {@link #initialize()} before using the database and {@link #shutdown()}
   * when finished using it.
   *
   * @param config The configuration provider
   */
  public ExternalDatabase(final IDatabaseConfigProvider config) {
    mConfig = config;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.INameSearchDatabase#getAllNodeNameData()
   */
  @Override
  public Collection<NodeNameData> getAllNodeNameData() {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting all node name data");
    }
    final List<NodeNameData> nodeData = new ArrayList<>();
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(DatabaseUtil.QUERY_ALL_NODE_NAME_DATA)) {
        // Execute the statement and collect the result
        try (ResultSet result = statement.executeQuery()) {
          while (result.next()) {
            final long id = result.getLong(1);
            final String name = result.getString(2);
            nodeData.add(new NodeNameData(id, name));
          }
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting node name data, current result is {}", nodeData, e);
    }

    return nodeData;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.db.IRoutingDatabase#getHighwayData(java.util.stream.
   * LongStream, int)
   */
  @Override
  public Collection<HighwayData> getHighwayData(final LongStream wayIds, final int size) {
    // Build the query
    final StringJoiner queryBuilder = new StringJoiner(DatabaseUtil.QUERY_DATA_DELIMITER,
        DatabaseUtil.QUERY_HIGHWAY_DATA_PREFIX, DatabaseUtil.QUERY_INSERT_SUFFIX);
    IntStream.range(0, size).forEach(i -> queryBuilder.add(DatabaseUtil.QUERY_PLACEHOLDER));

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting highway data for {} ways", size);
    }
    final List<HighwayData> wayData = new ArrayList<>(size);
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
        // Fill the statement
        final AtomicInteger counter = new AtomicInteger();
        wayIds.forEach(id -> {
          try {
            statement.setLong(counter.incrementAndGet(), id);
          } catch (final SQLException e) {
            LOGGER.error("Error getting highway data for {} ways, can not set parameter {} with way id {}", size,
                counter.get(), id, e);
          }
        });

        // Execute the statement and collect the result
        try (ResultSet result = statement.executeQuery()) {
          while (result.next()) {
            final long id = result.getLong(1);
            final String highway = result.getString(2);
            int maxSpeed = result.getInt(3);
            if (maxSpeed == 0) {
              maxSpeed = -1;
            }
            wayData.add(new HighwayData(id, EHighwayType.fromName(highway), maxSpeed));
          }
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting highway data for {} ways, current result is {}", size, wayData, e);
    }

    return wayData;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getInternalNodeByOsm(long)
   */
  @Override
  public Optional<Integer> getInternalNodeByOsm(final long osmId) {
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(DatabaseUtil.QUERY_NODE_INTERNAL_BY_OSM)) {
        statement.setLong(1, osmId);
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return Optional.of(result.getInt(1));
          }
          return Optional.empty();
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting internal node by OSM: {}", osmId, e);
      return Optional.empty();
    }
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getInternalWayByOsm(long)
   */
  @Override
  public Optional<Integer> getInternalWayByOsm(final long osmId) {
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(DatabaseUtil.QUERY_WAY_INTERNAL_BY_OSM)) {
        statement.setLong(1, osmId);
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return Optional.of(result.getInt(1));
          }
          return Optional.empty();
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting internal way by OSM: {}", osmId, e);
      return Optional.empty();
    }
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getNodeByName(java.lang.String)
   */
  @Override
  public Optional<Long> getNodeByName(final String name) {
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(DatabaseUtil.QUERY_NODE_ID_BY_NAME)) {
        statement.setString(1, name);
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

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getNodeName(long)
   */
  @Override
  public Optional<String> getNodeName(final long id) {
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(DatabaseUtil.QUERY_NODE_NAME_BY_ID)) {
        statement.setLong(1, id);
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return Optional.ofNullable(result.getString(1));
          }
          return Optional.empty();
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting node name by id: {}", id, e);
      return Optional.empty();
    }
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getOsmNodeByInternal(int)
   */
  @Override
  public Optional<Long> getOsmNodeByInternal(final int internalId) {
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(DatabaseUtil.QUERY_NODE_OSM_BY_INTERNAL)) {
        statement.setInt(1, internalId);
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return Optional.of(result.getLong(1));
          }
          return Optional.empty();
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting OSM node by internal: {}", internalId, e);
      return Optional.empty();
    }
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getOsmWayByInternal(int)
   */
  @Override
  public Optional<Long> getOsmWayByInternal(final int internalId) {
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(DatabaseUtil.QUERY_WAY_OSM_BY_INTERNAL)) {
        statement.setInt(1, internalId);
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return Optional.of(result.getLong(1));
          }
          return Optional.empty();
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting OSM way by internal: {}", internalId, e);
      return Optional.empty();
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.db.IRoutingDatabase#getSpatialNodeData(java.util.stream.
   * LongStream, int)
   */
  @Override
  public Collection<SpatialNodeData> getSpatialNodeData(final LongStream nodeIds, final int size) {
    // Build the query
    final StringJoiner queryBuilder = new StringJoiner(DatabaseUtil.QUERY_DATA_DELIMITER,
        DatabaseUtil.QUERY_SPATIAL_NODE_DATA_PREFIX, DatabaseUtil.QUERY_INSERT_SUFFIX);
    IntStream.range(0, size).forEach(i -> queryBuilder.add(DatabaseUtil.QUERY_PLACEHOLDER));

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Getting spatial data for {} nodes", size);
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
            final long osmId = result.getLong(1);
            final int internalId = result.getInt(2);
            final float latitude = result.getFloat(3);
            final float longitude = result.getFloat(4);
            nodeData.add(new SpatialNodeData(internalId, osmId, latitude, longitude));
          }
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting spatial data for {} nodes, current result is {}", size, nodeData, e);
    }

    return nodeData;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getWayByName(java.lang.String)
   */
  @Override
  public Optional<Long> getWayByName(final String name) {
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(DatabaseUtil.QUERY_WAY_ID_BY_NAME)) {
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

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#getWayName(long)
   */
  @Override
  public Optional<String> getWayName(final long id) {
    try (Connection connection = createConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(DatabaseUtil.QUERY_WAY_NAME_BY_ID)) {
        statement.setLong(1, id);
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return Optional.ofNullable(result.getString(1));
          }
          return Optional.empty();
        }
      }
    } catch (final SQLException e) {
      LOGGER.error("Error getting way name by id: {}", id, e);
      return Optional.empty();
    }
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#initialize()
   */
  @Override
  public void initialize() throws ParseException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Ensuring database has correct layout.");
    }
    // Create database tables if they don't exist already
    final Path initDbScript = mConfig.getInitDbScript();
    try {
      ScriptExecutor.executeScript(initDbScript, createConnection());
    } catch (SQLException | IOException e) {
      throw new ParseException(e);
    }
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
      LOGGER.debug("Offering {} ID mappings to the database", size);
    }
    try (Connection connection = createConnection()) {
      connection.setAutoCommit(false);

      // Queue all queries
      mappings.forEach(mapping -> {
        try {
          ExternalDatabase.queueMapping(mapping, connection);
        } catch (final SQLException e) {
          LOGGER.error("Error queueing mapping for database insertion: {}", mapping, e);
        }
      });

      // Submit all queries
      connection.commit();
      connection.setAutoCommit(true);
    } catch (final SQLException e) {
      LOGGER.error("Error offering {} mappings to the database", size, e);
    }
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
      LOGGER.debug("Offering {} entities to the database", size);
    }
    try (Connection connection = createConnection()) {
      connection.setAutoCommit(false);

      // Queue all queries
      entities.forEach(entity -> {
        try {
          if (entity instanceof OsmNode) {
            ExternalDatabase.queueOsmNode((OsmNode) entity, connection);
          } else if (entity instanceof OsmWay) {
            ExternalDatabase.queueOsmWay((OsmWay) entity, connection);
          }
        } catch (final SQLException e) {
          LOGGER.error("Error queueing entity for database insertion: {}", entity, e);
        }
      });

      // Submit all queries
      connection.commit();
      connection.setAutoCommit(true);
    } catch (final SQLException e) {
      LOGGER.error("Error offering {} entities to the database", size, e);
    }
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.db.IRoutingDatabase#shutdown()
   */
  @Override
  public void shutdown() {
    // Nothing needs to be done
    LOGGER.info("Shutting down database");
  }

  /**
   * Creates a connection to the external database by using the JDBC URL
   * provided by the configuration provider.
   *
   * @return The connection to the external database
   * @throws SQLException If an SQL exception occurred while trying to connect
   *                      to the external database. For example if the JDB URL
   *                      was invalid.
   */
  private Connection createConnection() throws SQLException {
    return DriverManager.getConnection(mConfig.getJdbcUrl());
  }

}
