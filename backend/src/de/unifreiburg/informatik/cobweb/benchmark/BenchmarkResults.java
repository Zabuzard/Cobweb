package de.unifreiburg.informatik.cobweb.benchmark;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.unifreiburg.informatik.cobweb.routing.model.graph.ETransportationMode;
import de.unifreiburg.informatik.cobweb.util.collections.Pair;

/**
 * Collects and contains results of a benchmark.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class BenchmarkResults {
  /**
   * Returns a formatted string representing the date of the given timestamp.
   *
   * @param timestamp The timestamp to format in milliseconds since epoch
   * @return The formatted date
   */
  private static String timestampToFormattedDate(final long timestamp) {
    final LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    return dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
  }

  /**
   * Returns a formatted string representing the time of the given timestamp.
   *
   * @param timestamp The timestamp to format in milliseconds since epoch
   * @return The formatted time
   */
  private static String timestampToFormattedTime(final long timestamp) {
    final LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
  }

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
    lines.add("Departure\tDate\tTime\tDuration");
    mMeasurements.forEach(pair -> {
      final long departure = pair.getFirst();
      final String date = BenchmarkResults.timestampToFormattedDate(departure);
      final String time = BenchmarkResults.timestampToFormattedTime(departure);
      final long duration = pair.getSecond();

      final List<String> entries = new ArrayList<>();
      entries.add(String.valueOf(departure));
      entries.add(date);
      entries.add(time);
      entries.add(String.valueOf(duration));

      lines.add(entries.stream().collect(Collectors.joining("\t")));
    });
    return lines;
  }
}
