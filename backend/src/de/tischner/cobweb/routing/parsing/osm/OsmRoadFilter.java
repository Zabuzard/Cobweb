package de.tischner.cobweb.routing.parsing.osm;

import de.tischner.cobweb.parsing.osm.IOsmFilter;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

public final class OsmRoadFilter implements IOsmFilter {

  public OsmRoadFilter() {
    // TODO Implement, use some config
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.parsing.osm.IOsmFilter#filter(de.topobyte.osm4j.core.model
   * .iface.OsmNode)
   */
  @Override
  public boolean filter(final OsmNode node) {
    // Never accept, we read nodes from ways instead since spatial data is not
    // needed
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.parsing.osm.IOsmFilter#filter(de.topobyte.osm4j.core.model
   * .iface.OsmRelation)
   */
  @Override
  public boolean filter(final OsmRelation relation) {
    // Never accept, we are only interested on ways
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.parsing.osm.IOsmFilter#filter(de.topobyte.osm4j.core.model
   * .iface.OsmWay)
   */
  @Override
  public boolean filter(final OsmWay way) {
    // TODO Implement
    return true;
  }

}
