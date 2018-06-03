package de.tischner.cobweb.routing.parsing.gtfs;

import java.io.IOException;
import java.nio.file.Path;

import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.FareAttribute;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.Frequency;
import org.onebusaway.gtfs.model.Pathway;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tischner.cobweb.config.IRoutingConfigProvider;
import de.tischner.cobweb.parsing.RecentHandler;
import de.tischner.cobweb.parsing.gtfs.GtfsEntityForwarder;
import de.tischner.cobweb.parsing.gtfs.IGtfsFileHandler;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGetNodeById;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.IHasId;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.ISpatial;

/**
 * Implementation of an {@link IGtfsFileHandler} which constructs a realistic
 * time expanded transit graph that consists of transit nodes and edges out of
 * the given GTFS data.<br>
 * <br>
 * The graph can be cached, then the handler will only parse files that provide
 * data the graph does not already contain. The node and edge instances itself
 * are created using a given builder.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 * @param <G> Type of the graph
 */
public final class GtfsRealisticTimeExpandedHandler<N extends INode & IHasId & ISpatial, E extends IEdge<N>,
    G extends IGraph<N, E> & IGetNodeById<N>> extends GtfsEntityForwarder implements IGtfsFileHandler {
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(GtfsRealisticTimeExpandedHandler.class);
  /**
   * The graph to insert parsed nodes and edges into.
   */
  private final G mGraph;
  /**
   * The handler to use which determines the GTFS files that contain more recent
   * or new data than the data already stored in the graph. Will only be used if
   * the configuration has set the use of a graph cache.
   */
  private final RecentHandler mRecentHandler;
  /**
   * Whether or not a graph cache is to be used. This determines if GTFS files
   * should be filtered by a {@link RecentHandler} or not.
   */
  private final boolean mUseGraphCache;

  /**
   * Creates a new GTFS realistic time expanded handler which operates on the
   * given graph using the given configuration.<br>
   * <br>
   * The builder will be used to construct the nodes and edges that are to be
   * inserted into the graph.
   *
   * @param graph  The graph to insert nodes and edges into
   * @param config Configuration provider which provides graph cache information
   * @throws IOException If an I/O exception occurred while reading the graph
   *                     cache information
   */
  public GtfsRealisticTimeExpandedHandler(final G graph, final IRoutingConfigProvider config) throws IOException {
    // TODO A lot, currently only a super early draft
    mGraph = graph;

    mUseGraphCache = config.useGraphCache();
    if (mUseGraphCache) {
      mRecentHandler = new RecentHandler(config.getGraphCacheInfo());
    } else {
      mRecentHandler = null;
    }
  }

  @Override
  public void complete() throws IOException {
    mRecentHandler.updateInfo();
  }

  @Override
  public void handle(final Agency agency) {
    // Ignore, not interested in
    // TODO Remove test
    LOGGER.info("TEST: Agency is: " + agency.getName());
  }

  @Override
  public void handle(final FareAttribute fareAttribute) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final FareRule fareRule) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final FeedInfo feedInfo) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final Frequency frequency) {
    // TODO Unsure
  }

  @Override
  public void handle(final Pathway pathway) {
    // TODO Unsure
  }

  @Override
  public void handle(final Route route) {
    // TODO Implement
  }

  @Override
  public void handle(final ServiceCalendar serviceCalendar) {
    // TODO Implement
  }

  @Override
  public void handle(final ServiceCalendarDate serviceCalendarDate) {
    // TODO Implement
  }

  @Override
  public void handle(final ShapePoint shapePoint) {
    // Ignore, not interested in
  }

  @Override
  public void handle(final Stop stop) {
    // TODO Implement
  }

  @Override
  public void handle(final StopTime stopTime) {
    // TODO Implement
  }

  @Override
  public void handle(final Transfer transfer) {
    // TODO Implement
  }

  @Override
  public void handle(final Trip trip) {
    // TODO Implement
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.parsing.IFileHandler#acceptFile(java.nio.file.Path)
   */
  @Override
  public boolean isAcceptingFile(final Path file) {
    // Check if the files content is not already included in the cache
    if (mUseGraphCache && !mRecentHandler.isAcceptingFile(file)) {
      return false;
    }

    // Accept all GTFS files
    LOGGER.info("Accepts file {}", file);
    return true;
  }
}
