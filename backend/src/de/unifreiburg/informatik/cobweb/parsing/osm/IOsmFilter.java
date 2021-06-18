package de.unifreiburg.informatik.cobweb.parsing.osm;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

/**
 * Filter to use for filtering OSM entities.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IOsmFilter {
  /**
   * Whether or not the given OSM node is accepted by the filter.
   *
   * @param node The node in question
   * @return <code>True</code> if the node is accepted, <code>false</code> otherwise
   */
  boolean filter(OsmNode node);

  /**
   * Whether or not the given OSM relation is accepted by the filter.
   *
   * @param relation The relation in question
   * @return <code>True</code> if the relation is accepted, <code>false</code> otherwise
   */
  boolean filter(OsmRelation relation);

  /**
   * Whether or not the given OSM way is accepted by the filter.
   *
   * @param way The way in question
   * @return <code>True</code> if the way is accepted, <code>false</code> otherwise
   */
  boolean filter(OsmWay way);
}
