package de.tischner.cobweb.parsing.osm;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

public interface IOsmFilter {

  boolean filter(OsmNode node);

  boolean filter(OsmRelation relation);

  boolean filter(OsmWay way);
}
