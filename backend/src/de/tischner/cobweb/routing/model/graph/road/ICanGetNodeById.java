package de.tischner.cobweb.routing.model.graph.road;

import java.util.Optional;

import de.tischner.cobweb.routing.model.graph.INode;

public interface ICanGetNodeById<N extends INode & IHasId> {
  boolean containsNodeWithId(long id);

  Optional<N> getNodeById(long id);
}
