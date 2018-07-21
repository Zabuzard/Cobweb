package de.unifreiburg.informatik.cobweb.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.unifreiburg.informatik.cobweb.routing.model.graph.ETransportationMode;
import de.unifreiburg.informatik.cobweb.util.collections.Pair;

/**
 * Collects and contains results of a benchmark.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class BenchmarkResults {
  /**
   * The comment of the benchmark.
   */
  private final String mComment;
  /**
   * The measurements of this benchmark. As list of departure time, in
   * milliseconds since epoch, and the average computation duration, in
   * milliseconds.
   */
  private final List<Pair<Long, Long>> mMeasurements;
  /**
   * The modes used in this benchmark.
   */
  private final Set<ETransportationMode> mModes;

  /**
   * Creates a new empty benchmark results with the given modes and comment.
   *
   * @param modes   The modes used in this benchmark
   * @param comment The comment of the benchmark
   */
  public BenchmarkResults(final Set<ETransportationMode> modes, final String comment) {
    mModes = modes;
    mComment = comment;
    mMeasurements = new ArrayList<>();
  }

  /**
   * Adds a measurement to the results.
   *
   * @param depTime         The departure time of the benchmark measurement, in
   *                        milliseconds since epoch
   * @param averageDuration The average computation duration of the measurement,
   *                        in milliseconds
   */
  public void addMeasurement(final long depTime, final long averageDuration) {
    mMeasurements.add(new Pair<>(depTime, averageDuration));
  }

  /**
   * Gets the comment of the benchmark.
   *
   * @return The comment of the benchmark
   */
  public String getComment() {
    return mComment;
  }

  /**
   * Gets all recorded measurements of the benchmark. As list of departure time,
   * in milliseconds since epoch, and the average computation duration, in
   * milliseconds.
   *
   * @return All recorded measurements of the benchmark
   */
  public List<Pair<Long, Long>> getMeasurements() {
    return mMeasurements;
  }

  /**
   * Converts the results in a human readable list of lines.
   *
   * @return The results as readable list of lines
   */
  public List<String> toStringLines() {
    final List<String> lines = new ArrayList<>();
    lines.add("# " + mComment);
    lines.add("# " + mModes);
    lines.add("Departure\tDuration");
    mMeasurements.forEach(pair -> lines.add(pair.getFirst() + "\t" + pair.getSecond()));
    return lines;
  }
}
