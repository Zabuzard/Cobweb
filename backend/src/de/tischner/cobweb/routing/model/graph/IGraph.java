package de.tischner.cobweb.routing.model.graph;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public interface IGraph<N extends INode, E extends IEdge<N>> {
  boolean addEdge(E edge);

  boolean addNode(N node);

  boolean containsEdge(E edge);

  long getAmountOfEdges();

  Stream<E> getEdges();

  Set<E> getIncomingEdges(N destination);

  Collection<N> getNodes();

  Set<E> getOutgoingEdges(N source);

  boolean removeEdge(E edge);

  boolean removeNode(N node);

  void reverse();

  int size();
}
