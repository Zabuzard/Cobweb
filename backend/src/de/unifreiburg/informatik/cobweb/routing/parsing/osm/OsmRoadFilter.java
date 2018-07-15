package de.unifreiburg.informatik.cobweb.routing.parsing.osm;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.unifreiburg.informatik.cobweb.config.IRoutingConfigProvider;
import de.unifreiburg.informatik.cobweb.parsing.ParseException;
import de.unifreiburg.informatik.cobweb.parsing.osm.IOsmFilter;

/**
 * Implementation of a {@link IOsmFilter} which only accepts OSM ways that
 * represent a road. Nodes and relations are rejected completely.<br>
 * <br>
 * The filter is configured using a configuration file. The format of the file
 * allows two filtering modes:
 * <ul>
 * <li><tt>--KEEP</tt>: The following entries contain key-value pairs which
 * every way must contain at least one of to be kept.</li>
 * <li><tt>--DROP</tt>: The following entries contain key-value pairs which are
 * forbidden for a way. If a way contains at least one of them, it is
 * rejected.</li>
 * </ul>
 * A mode is indicated by a line containing only the above mode indicator. Modes
 * can be switched at any time. Empty lines are ignored and lines preceded by a
 * <tt>#</tt> are interpreted as comment and thus ignored too.<br>
 * <br>
 * An entry is a regular key-value pair with the key being interpreted as OSM
 * tag and the value as its OSM tag value. A valid configuration file might look
 * like
 *
 * <pre>
 * --KEEP
 * #highways
 * highway=motorway
 * highway=trunk
 *
 * --DROP
 * area=yes
 * train=yes
 * </pre>
 * <p>
 * The filter will accept only OSM ways that have a <tt>highway</tt> tag value
 * of either <tt>motorway</tt> or <tt>trunk</tt> and not a value <tt>yes</tt>
 * for the tag <tt>area</tt> or <tt>train</tt>.
 * </p>
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class OsmRoadFilter implements IOsmFilter {
  /**
   * Prefix for a line that represents a comment.
   */
  private static final String COMMENT_PREFIX = "#";
  /**
   * Indicator to start drop mode.
   */
  private static final String DROP_MODE = "--DROP";
  /**
   * Indicator to start keep mode.
   */
  private static final String KEEP_MODE = "--KEEP";
  /**
   * Logger to use for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(OsmRoadFilter.class);
  /**
   * Symbol which separates tag keys from their values.
   */
  private static final String TAG_VALUE_SEPARATOR = "=";

  /**
   * Whether or not the given OSM tag and its value are contained in the given
   * map.
   *
   * @param tag         The tag in question
   * @param tagToValues A map connecting tag names to their values
   * @return <tt>True</tt> if the tag and its value are contained in the given
   *         map, <tt>false</tt> otherwise
   */
  private static boolean isTagInMap(final OsmTag tag, final Map<String, Set<String>> tagToValues) {
    final Set<String> values = tagToValues.get(tag.getKey());
    if (values == null) {
      return false;
    }
    return values.contains(tag.getValue());
  }

  /**
   * Configuration provider which provides the path to the filter configuration.
   */
  private final IRoutingConfigProvider mConfig;
  /**
   * A map connecting all in drop mode registered OSM tags to their values.
   */
  private final Map<String, Set<String>> mTagToValuesDrop;

  /**
   * A map connecting all in keep mode registered OSM tags to their values.
   */
  private final Map<String, Set<String>> mTagToValuesKeep;

  /**
   * Creates a new OSM road filter which filters OSM entities based on the
   * filter configuration provided by the given configuration provider.
   *
   * @param config The configuration provider that provides the filter
   *               configuration
   * @throws ParseException If an error occurred while parsing the filter
   *                        configuration. For example if the file could not be
   *                        found or if its syntax is invalid.
   */
  public OsmRoadFilter(final IRoutingConfigProvider config) throws ParseException {
    mConfig = config;
    mTagToValuesKeep = new HashMap<>();
    mTagToValuesDrop = new HashMap<>();
    initialize();
  }

  /**
   * OSM nodes are never accepted by this filter. Nodes should be read from ways
   * instead.
   *
   * @return Always <tt>false</tt>
   */
  @Override
  public boolean filter(final OsmNode node) {
    // Never accept, we read nodes from ways instead since spatial data is not
    // needed
    return false;
  }

  /**
   * OSM relations are never accepted by this filter.
   *
   * @return Always <tt>false</tt>
   */
  @Override
  public boolean filter(final OsmRelation relation) {
    // Never accept, we are only interested in ways
    return false;
  }

  /**
   * Whether or not the given OSM way is accepted by the filter. This is
   * determined by the given filter configuration file.
   */
  @Override
  public boolean filter(final OsmWay way) {
    // Iterate tags
    boolean hasOneKeepTag = false;
    for (int i = 0; i < way.getNumberOfTags(); i++) {
      final OsmTag tag = way.getTag(i);
      // Reject way if it contains a single drop tag
      if (isDropTag(tag)) {
        return false;
      }
      // Register the first keep tag
      if (!hasOneKeepTag && isKeepTag(tag)) {
        hasOneKeepTag = true;
      }
    }
    // Accept only if at least one keep tag was found
    return hasOneKeepTag;
  }

  /**
   * Initializes this OSM road filter. Therefore, it reads and parses the given
   * filter configuration file
   *
   * @throws ParseException If an error occurred while parsing the filter
   *                        configuration. For example if the file could not be
   *                        found or if its syntax is invalid.
   */
  private void initialize() throws ParseException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Initializing OSM road filter");
    }
    final Path filter = mConfig.getOsmRoadFilter();
    try (BufferedReader br = Files.newBufferedReader(filter)) {
      boolean keepMode = true;
      while (true) {
        final String line = br.readLine();
        if (line == null) {
          break;
        }
        // Line is empty or comment
        if (line.isEmpty() || line.startsWith(COMMENT_PREFIX)) {
          continue;
        }
        // Line is mode changer
        if (line.equals(KEEP_MODE)) {
          keepMode = true;
          continue;
        } else if (line.equals(DROP_MODE)) {
          keepMode = false;
          continue;
        }

        // Line is regular tag-entry
        final String[] data = line.split(TAG_VALUE_SEPARATOR, 2);
        final String tag = data[0];
        final String value = data[1];
        // Get the correct map to insert into
        Map<String, Set<String>> tagToValues;
        if (keepMode) {
          tagToValues = mTagToValuesKeep;
        } else {
          tagToValues = mTagToValuesDrop;
        }
        tagToValues.computeIfAbsent(tag, k -> new HashSet<>()).add(value);
      }
    } catch (final IOException e) {
      throw new ParseException(e);
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("OSM road filter data keep: {}", mTagToValuesKeep);
      LOGGER.debug("OSM road filter data drop: {}", mTagToValuesDrop);
    }
  }

  /**
   * Whether or not the given OSM tag and its value were registered in drop
   * mode.
   *
   * @param tag The tag in question
   * @return <tt>True</tt> if the tag and its value were registered in drop
   *         mode, <tt>false</tt> otherwise
   */
  private boolean isDropTag(final OsmTag tag) {
    return OsmRoadFilter.isTagInMap(tag, mTagToValuesDrop);
  }

  /**
   * Whether or not the given OSM tag and its value were registered in keep
   * mode.
   *
   * @param tag The tag in question
   * @return <tt>True</tt> if the tag and its value were registered in keep
   *         mode, <tt>false</tt> otherwise
   */
  private boolean isKeepTag(final OsmTag tag) {
    return OsmRoadFilter.isTagInMap(tag, mTagToValuesKeep);
  }

}
