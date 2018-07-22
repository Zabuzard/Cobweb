package de.unifreiburg.informatik.cobweb.benchmark;

import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.IShortestPathComputation;
import de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.ShortestPathComputationFactory;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ETransportationMode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.util.RoutingUtil;

/**
 * An executable benchmark that records perfomance of the given parameters.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class Benchmark implements Runnable {
  /**
   * The amount of how often a measurement is to be repeated for averaging.
   */
  private static final int AVERAGE_AMOUNT = 25;
  /**
   * The amount of steps after which to generate a log message.
   */
  private static final int LOG_EVERY = 3;
  /**
   * The amount of sub steps after which to generate a log message.
   */
  private static final int LOG_EVERY_SUB_STEP = 5;
  /**
   * Logger used for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(Benchmark.class);
  /**
   * The comment of this benchmark.
   */
  private final String mComment;
  /**
   * The departure time to stop at (inclusive), in milliseconds since epoch.
   */
  private final long mDepTimeEnd;
  /**
   * The departure time to start at, in milliseconds since epoch.
   */
  private final long mDepTimeStart;
  /**
   * The time in milliseconds to increase the departure time for a run.
   */
  private final long mDepTimeSteps;
  /**
   * The factory to use for shortest path computation generation.
   */
  private final ShortestPathComputationFactory mFactory;
  /**
   * The set of transportation modes to use.
   */
  private final Set<ETransportationMode> mModes;
  /**
   * The provider used for query node retrieval.
   */
  private final IQueryNodeProvider mProvider;
  /**
   * The results of this benchmark.
   */
  private final BenchmarkResults mResults;

  /**
   * Creates a new benchmark with the given parameters.
   *
   * @param factory      The factory to use for shortest path computation
   *                     generation
   * @param provider     The provider to use for query node retrieval
   * @param modes        The set of transportation modes to use
   * @param depTimeStart The departure time to start at, in milliseconds since
   *                     epoch
   * @param depTimeEnd   The departure time to stop at (inclusive), in
   *                     milliseconds since epoch
   * @param depTimeSteps The time in milliseconds to increase the departure time
   *                     for a run
   * @param comment      The comment of this benchmark
   */
  public Benchmark(final ShortestPathComputationFactory factory, final IQueryNodeProvider provider,
      final Set<ETransportationMode> modes, final long depTimeStart, final long depTimeEnd, final long depTimeSteps,
      final String comment) {
    mFactory = factory;
    mProvider = provider;
    mModes = modes;
    mDepTimeStart = depTimeStart;
    mDepTimeEnd = depTimeEnd;
    mDepTimeSteps = depTimeSteps;
    mComment = comment;
    mResults = new BenchmarkResults(mModes, mComment);
  }

  /**
   * Gets the comment of this benchmark.
   *
   * @return The comment of this benchmark
   */
  public String getComment() {
    return mComment;
  }

  /**
   * Gets the results of this benchmark.
   *
   * @return The results of this benchmark
   */
  public BenchmarkResults getResults() {
    return mResults;
  }

  @Override
  public void run() {
    final int amountOfSteps = (int) ((mDepTimeEnd - mDepTimeStart) / mDepTimeSteps) + 1;
    int stepCounter = 0;

    for (long depTime = mDepTimeStart; depTime <= mDepTimeEnd; depTime += mDepTimeSteps) {
      final IShortestPathComputation<ICoreNode, ICoreEdge<ICoreNode>> shortestPathComputation =
          mFactory.createAlgorithm(depTime, mModes);

      final long[] durationsMillis = new long[AVERAGE_AMOUNT];
      for (int i = 0; i < AVERAGE_AMOUNT; i++) {
        final ICoreNode source = mProvider.getQueryNode();
        final ICoreNode destination = mProvider.getQueryNode();

        final long startTime = System.nanoTime();
        shortestPathComputation.computeShortestPath(source, destination);
        final long endTime = System.nanoTime();
        final long duration = endTime - startTime;
        final long durationMillis = RoutingUtil.nanosToMillis(duration);
        durationsMillis[i] = durationMillis;

        if (i % LOG_EVERY_SUB_STEP == 0) {
          LOGGER.info("\t" + i + " of " + AVERAGE_AMOUNT);
        }
      }

      final long durationMillisAverage = (long) Arrays.stream(durationsMillis).average().getAsDouble();
      mResults.addMeasurement(depTime, durationMillisAverage);

      stepCounter++;
      if (stepCounter % LOG_EVERY == 0) {
        LOGGER.info(stepCounter + " of " + amountOfSteps);
      }
    }
  }
}
