package de.unifreiburg.informatik.cobweb.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unifreiburg.informatik.cobweb.routing.algorithms.metrics.AsTheCrowFliesMetric;
import de.unifreiburg.informatik.cobweb.routing.algorithms.nearestneighbor.CoverTree;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.IHasPathCost;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.IShortestPathComputation;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.ShortestPathComputationFactory;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.dijkstra.Dijkstra;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.hybridmodel.IAccessNodeComputation;
import de.unifreiburg.informatik.cobweb.routing.model.RoutingModel;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IGraph;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitNode;
import de.unifreiburg.informatik.cobweb.util.collections.Pair;

/**
 * A benchmark suite can be used for running benchmarks and recording their
 * results. Use {@link #start()} to start the benchmarks. The suite can be
 * seeded, use {@link #getSeed()} to get the current used seed.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class BenchmarkSuite implements IQueryNodeProvider {
  /**
   * The date to use for the benchmark.
   */
  private static final LocalDate DATE_TO_BENCHMARK = LocalDate.of(2018, 10, 10);
  /**
   * The size of the line buffer.
   */
  private static final int LINES_BUFFER_SIZE = 200;
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkSuite.class);
  /**
   * The amount of random queries the nearest neighbor computation is averaged
   * over.
   */
  private static final int NEAREST_NEIGHBOR_AVERAGING = 1_000;
  /**
   * The amount of nodes to add to the tree per step in the nearest neighbor
   * computation.
   */
  private static final int NEAREST_NEIGHBOR_STEPS = 10_000;
  /**
   * The path to where the results are saved to.
   */
  private static final Path RESULTS_PATH = Paths.get("benchmarkResults.tsv");
  /**
   * The amount of random queries the uni-modal time dependent shortest path
   * computation is averaged over.
   */
  private static final int UNI_MODAL_TIME_DEPENDENT_AVERAGING = 50;
  /**
   * The amount of seconds to increase the departure time with each step for the
   * uni-modal time dependent shortest path computation.
   */
  private static final long UNI_MODAL_TIME_DEPENDENT_DEP_TIME_STEPS = 10 * 60;
  /**
   * The amount of random queries the uni-modal time independent shortest path
   * computation is averaged over.
   */
  private static final int UNI_MODAL_TIME_INDEPENDENT_AVERAGING = 50;
  /**
   * The Dijkstra rank to start with measuring for the uni-modal time
   * independent shortest path computation.
   */
  private static final int UNI_MODAL_TIME_INDEPENDENT_STARTING_RANK = 0;
  /**
   * The minimal Dijkstra rank to end with measuring for the uni-modal time
   * independent shortest path computation.
   */
  private static final int UNI_MODAL_TIME_MINIMAL_END_RANK = 15;

  /**
   * Method to use for increasing measurement accuracy by introducing a cleanup
   * time.
   */
  private static void cleanup() {
    System.gc();

    try {
      Thread.sleep(1_000);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Method to use for increasing measurement accuracy by introducing a warmup
   * time.
   */
  private static void warmup() {
    LOGGER.info("Warming up");

    try {
      Thread.sleep(1_000);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    // Allocate some values
    int[] values = new int[100_000_000];
    for (int i = 0; i < values.length; i++) {
      values[i] = i;
    }
    values = null;
    BenchmarkSuite.cleanup();
  }

  /**
   * The factory to use for shortest path computation creation.
   */
  private ShortestPathComputationFactory mFactory;

  /**
   * Buffer for lines to write to the resulting file.
   */
  private final List<String> mLinesBuffer;
  /**
   * The routing model to use.
   */
  private final RoutingModel mModel;

  /**
   * The graph that provides the nodes to query on.
   */
  private final IGraph<ICoreNode, ICoreEdge<ICoreNode>> mQueryGraph;

  /**
   * The nodes to query on.
   */
  private ICoreNode[] mQueryNodes;

  /**
   * The random object to use for query node selection.
   */
  private final Random mRandom;
  /**
   * The seed to use for the random object.
   */
  private final long mSeed;

  /**
   * Creates a new benchmark suite which benchmarks the given model. A random
   * seed is used.
   *
   * @param model The model to benchmark.
   */
  public BenchmarkSuite(final RoutingModel model) {
    this(model, System.currentTimeMillis());
  }

  /**
   * Creates a new benchmark suite which benchmarks the given model. The given
   * seed is used.
   *
   * @param model The model to benchmark.
   * @param seed  The seed to use
   */
  public BenchmarkSuite(final RoutingModel model, final long seed) {
    mModel = model;
    mQueryGraph = mModel.getQueryGraph();
    mRandom = new Random(seed);
    mSeed = seed;
    mLinesBuffer = new ArrayList<>(LINES_BUFFER_SIZE);
  }

  @Override
  public ICoreNode getQueryNode() {
    return mQueryNodes[mRandom.nextInt(mQueryNodes.length)];
  }

  /**
   * Gets the seed used by this suite.
   *
   * @return The used seed
   */
  public long getSeed() {
    return mSeed;
  }

  /**
   * Starts the benchmark suite. Will execute various benchmarks and save their
   * results.
   */
  public void start() {
    LOGGER.info("Starting benchmarks");
    // Write a header with the seed
    try {
      final List<String> headerLines = new ArrayList<>();
      headerLines.add("#Benchmark with seed: " + mSeed);
      headerLines.add("#Created at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
      Files.write(RESULTS_PATH, headerLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE);
    } catch (final IOException e) {
      e.printStackTrace();
      return;
    }

    mQueryNodes = mQueryGraph.getNodes().toArray(new ICoreNode[0]);
    mFactory = mModel.createShortestPathComputationFactory();

    // Start benchmarks
    try {
      prepareBenchmarkMeasurement();
      nearestNeighborBenchmark();

      prepareBenchmarkMeasurement();
      uniModalTimeIndependent();

      prepareBenchmarkMeasurement();
      uniModalTimeDependent();
    } catch (final IOException e) {
      e.printStackTrace();
    } finally {
      try {
        flushLinesBuffer();
      } catch (final IOException e) {
        e.printStackTrace();
      }
      LOGGER.info("Finished benchmarks");
    }
  }

  /**
   * Flushes the buffered lines to the resulting file.
   *
   * @throws IOException If an I/O-Exception occurred while saving the results
   */
  private void flushLinesBuffer() throws IOException {
    Files.write(RESULTS_PATH, mLinesBuffer, StandardOpenOption.CREATE, StandardOpenOption.APPEND,
        StandardOpenOption.WRITE);
    mLinesBuffer.clear();
  }

  /**
   * Executes the benchmark for the nearest neighbor computation.
   *
   * @throws IOException If an I/O-Exception occurred while saving the results
   */
  private void nearestNeighborBenchmark() throws IOException {
    LOGGER.info("Starting nearest neighbor, size: " + mQueryNodes.length);

    writeLine("#Nearest neighbor, size: " + mQueryNodes.length);
    writeLine("Size\tTime(ns)");
    final CoverTree<ICoreNode> tree = new CoverTree<>(new AsTheCrowFliesMetric<>());

    // Insert first node
    List<ICoreNode> nodes = Arrays.asList(mQueryNodes);
    Collections.shuffle(nodes, mRandom);
    final Queue<ICoreNode> notContainedNodes = new ArrayDeque<>(nodes);
    nodes = null;
    final List<ICoreNode> containedNodes = new ArrayList<>();
    final ICoreNode firstNode = notContainedNodes.poll();
    tree.insert(firstNode);
    containedNodes.add(firstNode);

    BenchmarkSuite.cleanup();

    // Measurements, periodically increase tree size
    final long[] durationsNanos = new long[NEAREST_NEIGHBOR_AVERAGING];
    int stepCounter = 0;
    while (!notContainedNodes.isEmpty()) {
      // Measure configuration with this size
      final int size = containedNodes.size();
      for (int j = 0; j < NEAREST_NEIGHBOR_AVERAGING; j++) {
        // Select a random node
        final ICoreNode queryNode = containedNodes.get(mRandom.nextInt(size));

        final long startTime = System.nanoTime();
        tree.getNearestNeighbor(queryNode);
        final long endTime = System.nanoTime();

        final long duration = endTime - startTime;
        durationsNanos[j] = duration;
      }
      final long durationNanosAverage = (long) Arrays.stream(durationsNanos).average().getAsDouble();
      writeLine(size + "\t" + durationNanosAverage);

      // Prepare next round, add nodes
      int j;
      if (stepCounter == 0) {
        j = 1;
      } else {
        j = 0;
      }
      for (; j < NEAREST_NEIGHBOR_STEPS && !notContainedNodes.isEmpty(); j++) {
        final ICoreNode node = notContainedNodes.poll();
        tree.insert(node);
        containedNodes.add(node);
      }

      if (stepCounter % 15 == 0) {
        LOGGER.info("Steps to go: " + notContainedNodes.size());
      }
      stepCounter++;
    }
  }

  /**
   * Prepares a measurement for a benchmark.
   *
   * @throws IOException If an I/O-Exception occurred while saving the results
   */
  private void prepareBenchmarkMeasurement() throws IOException {
    writeSeparator();
    BenchmarkSuite.warmup();
  }

  /**
   * Executes the benchmark for the uni-modal time-dependent shortest path
   * computation.
   *
   * @throws IOException If an I/O-Exception occurred while saving the results
   */
  private void uniModalTimeDependent() throws IOException {
    LOGGER.info("Starting uni modal time dependent");
    writeLine("#Uni-modal, time-dependent, measuring for date: "
        + DATE_TO_BENCHMARK.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

    // Departure time in seconds since midnight
    final int startDepTime = LocalTime.of(0, 0).toSecondOfDay();
    final int endDepTime = LocalTime.of(23, 59).toSecondOfDay();

    final int amountOfSteps = (int) ((endDepTime - startDepTime) / UNI_MODAL_TIME_DEPENDENT_DEP_TIME_STEPS) + 1;

    // Select random queries
    final List<Pair<ICoreNode, ICoreNode>> queries = new ArrayList<>(UNI_MODAL_TIME_DEPENDENT_AVERAGING);
    for (int i = 0; i < UNI_MODAL_TIME_DEPENDENT_AVERAGING; i++) {
      queries.add(new Pair<>(getQueryNode(), getQueryNode()));
    }

    // Measure CSA
    LOGGER.info("Measuring CSA");
    writeLine("#CSA");
    writeLine("DepTime(HH:mm)\tTime(ns)");
    final IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> computation = mFactory.createAlgorithmCsa();
    final IAccessNodeComputation<ICoreNode, ICoreNode> accessNodeComputation = mFactory.getAccessNodeComputation();
    // For every departure time point
    int stepCounter = 0;
    for (int depTime = startDepTime; depTime <= endDepTime; depTime += UNI_MODAL_TIME_DEPENDENT_DEP_TIME_STEPS) {
      // Average over selected queries
      final long[] durationsNanos = new long[queries.size()];
      int averagingCounter = 0;
      for (final Pair<ICoreNode, ICoreNode> query : queries) {
        final ICoreNode sourceRoad = query.getFirst();
        final ICoreNode destinationRoad = query.getSecond();
        final ICoreNode sourceAccess = accessNodeComputation.computeAccessNodes(sourceRoad).iterator().next();
        final ICoreNode destinationAccess = accessNodeComputation.computeAccessNodes(destinationRoad).iterator().next();
        final TransitNode sourceAccessQuery =
            new TransitNode(sourceAccess.getId(), sourceAccess.getLatitude(), sourceAccess.getLongitude(), depTime);

        // Measure the query
        final long startTime = System.nanoTime();
        computation.computeShortestPath(sourceAccessQuery, destinationAccess);
        final long endTime = System.nanoTime();
        final long duration = endTime - startTime;
        durationsNanos[averagingCounter] = duration;
        averagingCounter++;
      }

      final long durationNanosAverage = (long) Arrays.stream(durationsNanos).average().getAsDouble();
      final String formattedDepTime = LocalTime.ofSecondOfDay(depTime).format(DateTimeFormatter.ofPattern("HH:mm"));
      writeLine(formattedDepTime + "\t" + durationNanosAverage);

      if (stepCounter % 8 == 0) {
        LOGGER.info("Steps to go: " + (amountOfSteps - stepCounter));
      }
      stepCounter++;
    }
  }

  /**
   * Executes the benchmark for the uni-modal time-independent shortest path
   * computation.
   *
   * @throws IOException If an I/O-Exception occurred while saving the results
   */
  private void uniModalTimeIndependent() throws IOException {
    LOGGER.info("Starting uni modal time independent");

    LOGGER.info("Computing Dijkstra ranks");
    // Determine the query pairs by computing Dijkstra ranks
    final Map<ICoreNode, List<ICoreNode>> querySourceToDestination = new HashMap<>();
    final Dijkstra<ICoreNode, ICoreEdge<ICoreNode>> dijkstraRankComputation = new Dijkstra<>(mQueryGraph);
    int greatestCommonExponent = Integer.MAX_VALUE;

    for (int i = 0; i < UNI_MODAL_TIME_INDEPENDENT_AVERAGING; i++) {
      // Pick a random source
      final ICoreNode source = getQueryNode();
      final List<ICoreNode> destinations = new ArrayList<>();

      final Map<ICoreNode, ? extends IHasPathCost> nodeToDistance =
          dijkstraRankComputation.computeShortestPathCostsReachable(source);
      // Sort the destinations according to their distance
      final ICoreNode[] destinationsOrdered = nodeToDistance.entrySet().stream()
          .map(entry -> new CostContainer<>(entry.getKey(), entry.getValue().getPathCost())).sorted()
          .map(CostContainer::getElement).toArray(ICoreNode[]::new);

      // Pick destinations at 2^j positions
      final int lastExponent = (int) Math.floor(Math.log(destinationsOrdered.length - 1) / Math.log(2));
      // Reject node and repeat if it has a very bad connectivity
      if (lastExponent < UNI_MODAL_TIME_MINIMAL_END_RANK) {
        LOGGER.info("Rejecting node with bad connectivity");
        i--;
        continue;
      }
      if (lastExponent < greatestCommonExponent) {
        greatestCommonExponent = lastExponent;
      }
      for (int j = UNI_MODAL_TIME_INDEPENDENT_STARTING_RANK; j <= lastExponent; j++) {
        final ICoreNode destination = destinationsOrdered[(int) Math.pow(2, j)];
        destinations.add(destination);
      }

      querySourceToDestination.put(source, destinations);

      if (i % 5 == 0) {
        LOGGER.info("Steps to go: " + (UNI_MODAL_TIME_INDEPENDENT_AVERAGING - i));
      }
    }

    // Start measurements
    LOGGER.info("Starting measurements");
    writeLine("#Uni-modal, time-independent, size: " + mQueryGraph.size());

    // Algorithms to measure
    final List<Pair<IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>>, String>> algorithmsWithName =
        new ArrayList<>();
    algorithmsWithName.add(new Pair<>(mFactory.createAlgorithmDijkstra(), "Dijkstra"));
    algorithmsWithName.add(new Pair<>(mFactory.createAlgorithmAStarAsTheCrowFlies(), "A-star (as-the-crow-flies)"));
    algorithmsWithName.add(new Pair<>(mFactory.createAlgorithmAlt(), "ALT"));

    for (final Pair<IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>>,
        String> algorithmWithName : algorithmsWithName) {
      BenchmarkSuite.cleanup();

      LOGGER.info("Measuring: " + algorithmWithName.getSecond());
      writeLine("#" + algorithmWithName.getSecond());
      writeLine("DijkstraRank(2^i)\tTime(ns)");

      final IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> computation = algorithmWithName.getFirst();
      // Measure for all exponents
      for (int i = UNI_MODAL_TIME_INDEPENDENT_STARTING_RANK; i <= greatestCommonExponent; i++) {
        final int exponentIndex = i - UNI_MODAL_TIME_INDEPENDENT_STARTING_RANK;
        // Get source-destination queries with this Dijkstra rank
        final long[] durationsNanos = new long[querySourceToDestination.size()];
        int averagingCounter = 0;
        for (final Entry<ICoreNode, List<ICoreNode>> sourceToDestinations : querySourceToDestination.entrySet()) {
          final ICoreNode source = sourceToDestinations.getKey();
          final ICoreNode destination = sourceToDestinations.getValue().get(exponentIndex);

          // Measure this query
          final long startTime = System.nanoTime();
          computation.computeShortestPath(source, destination);
          final long endTime = System.nanoTime();
          final long duration = endTime - startTime;
          durationsNanos[averagingCounter] = duration;
          averagingCounter++;
        }
        final long durationNanosAverage = (long) Arrays.stream(durationsNanos).average().getAsDouble();
        writeLine(i + "\t" + durationNanosAverage);

        if (exponentIndex % 3 == 0) {
          LOGGER.info("Steps to go: " + (greatestCommonExponent - i));
        }
      }
    }
  }

  /**
   * Writes the given line to the resulting file. Lines are buffered and
   * automatically flushed.
   *
   * @param line The line to write
   * @throws IOException If an I/O-Exception occurred while saving the results
   */
  private void writeLine(final String line) throws IOException {
    mLinesBuffer.add(line);

    if (mLinesBuffer.size() >= LINES_BUFFER_SIZE) {
      flushLinesBuffer();
    }
  }

  /**
   * Writes a separating line to the resulting file.
   *
   * @throws IOException If an I/O-Exception occurred while saving the results
   */
  private void writeSeparator() throws IOException {
    writeLine("#-----------------------------------------------------------------");
  }
}
