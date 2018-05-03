package de.tischner.cobweb.routing.parsing.osm;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.road.IHasId;
import de.tischner.cobweb.routing.model.graph.road.ISpatial;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;

public interface IOsmRoadBuilder<N extends INode & IHasId & ISpatial, E extends IEdge<N> & IHasId> {
  E buildEdge(OsmWay way, long sourceId, long destinationId);

  N buildNode(OsmNode node);

  void complete();
}
