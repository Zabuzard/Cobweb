package de.unifreiburg.informatik.cobweb.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.ShortestPathComputationFactory;
import de.unifreiburg.informatik.cobweb.routing.model.RoutingModel;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ETransportationMode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IGraph;

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
   * The amount of milliseconds to increase the departure time with each
   * benchmark step.
   */
  private static final long DEP_TIME_STEPS = 10 * 60 * 1_000;
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkSuite.class);
  /**
   * The path to where the results are saved to.
   */
  private static final Path RESULTS_PATH = Paths.get("benchmarkResults.tsv");

  /**
   * Computes and returns the milliseconds since epoch for the given time at the
   * benchmark date.
   *
   * @param hour    The hours of the time
   * @param minutes The minutes of the time
   * @return The milliseconds since epoch for the given time at the benchmark
   *         date
   */
  private static long atTimeToMillis(final int hour, final int minutes) {
    final LocalDateTime dateTime = DATE_TO_BENCHMARK.atTime(hour, minutes);
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  /**
   * Records the given benchmark. Will execute it and save the results.
   *
   * @param benchmark The benchmark to record
   * @throws IOException If an I/O-Exception occurred while saving the results
   */
  private static void recordBenchmark(final Benchmark benchmark) throws IOException {
    LOGGER.info("Recording benchmark: " + benchmark.getComment());
    benchmark.run();
    final BenchmarkResults results = benchmark.getResults();
    LOGGER.info("Writing results to: " + RESULTS_PATH);
    Files.write(RESULTS_PATH, results.toStringLines(), StandardOpenOption.CREATE, StandardOpenOption.APPEND,
        StandardOpenOption.WRITE);
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
    System.gc();

    try {
      Thread.sleep(1_000);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
  }

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
      headerLines.add("#At: " + DATE_TO_BENCHMARK.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
      Files.write(RESULTS_PATH, headerLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE);
    } catch (final IOException e) {
      e.printStackTrace();
      return;
    }

    mQueryNodes = mQueryGraph.getNodes().toArray(new ICoreNode[0]);
    final ShortestPathComputationFactory factory = mModel.createShortestPathComputationFactory();

    BenchmarkSuite.warmup();

    try {
      // Road only
      Set<ETransportationMode> modes = EnumSet.of(ETransportationMode.CAR);
      BenchmarkSuite.recordBenchmark(new Benchmark(factory, this, modes, BenchmarkSuite.atTimeToMillis(0, 0),
          BenchmarkSuite.atTimeToMillis(23, 59), DEP_TIME_STEPS, "Road only, LALT"));

      // With tram
      modes = EnumSet.of(ETransportationMode.TRAM, ETransportationMode.FOOT);
      BenchmarkSuite.recordBenchmark(new Benchmark(factory, this, modes, BenchmarkSuite.atTimeToMillis(0, 0),
          BenchmarkSuite.atTimeToMillis(23, 59), DEP_TIME_STEPS,
          "Tram only, LALT, 3 nearest access nodes, connection scan, full footpaths"));
    } catch (final IOException e) {
      e.printStackTrace();
      return;
    } finally {
      LOGGER.info("Finished benchmarks");
    }
  }
}
