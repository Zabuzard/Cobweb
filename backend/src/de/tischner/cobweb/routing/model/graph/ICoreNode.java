package de.tischner.cobweb.routing.model.graph;

import java.io.Serializable;

/**
 * Interface for core nodes. This are nodes with additional common properties.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface ICoreNode extends INode, IHasId, ISpatial, Serializable {
  // Grouping interface, does not contain own methods at the moment
}
