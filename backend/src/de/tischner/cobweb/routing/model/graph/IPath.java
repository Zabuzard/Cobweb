package de.tischner.cobweb.routing.model.graph;

public interface IPath<N extends INode, E extends IEdge<N>> extends Iterable<E> {
  N getDestination();

  N getSource();

  double getTotalCost();

  int length();
}
