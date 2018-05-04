package de.tischner.cobweb.routing.algorithms.scc;

import java.util.Collection;

import de.tischner.cobweb.routing.model.graph.INode;

public interface ISccComputation<N extends INode> {
  StronglyConnectedComponent<N> getLargestScc();

  Collection<StronglyConnectedComponent<N>> getSccs();
}
