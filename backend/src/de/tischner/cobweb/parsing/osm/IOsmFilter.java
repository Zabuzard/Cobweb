package de.tischner.cobweb.parsing.osm;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

/**
 * Filter to use for filtering OSM entities.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public interface IOsmFilter {
  /**
   * Whether or not the given OSM node is accepted by the filter.
   *
   * @param node The node in question
   * @return <tt>True</tt> if the node is accepted, <tt>false</tt> otherwise
   */
  boolean filter(OsmNode node);

  /**
   * Whether or not the given OSM relation is accepted by the filter.
   *
   * @param relation The relation in question
   * @return <tt>True</tt> if the relation is accepted, <tt>false</tt> otherwise
   */
  boolean filter(OsmRelation relation);

  /**
   * Whether or not the given OSM way is accepted by the filter.
   *
   * @param way The way in question
   * @return <tt>True</tt> if the way is accepted, <tt>false</tt> otherwise
   */
  boolean filter(OsmWay way);
}
