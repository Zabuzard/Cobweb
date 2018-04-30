package de.tischner.cobweb.routing.model.graph;

public interface IEdge<N extends INode> {
  double getCost();

  N getDestination();

  N getSource();
}
