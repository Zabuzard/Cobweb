package de.tischner.cobweb.routing.model.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public abstract class AGraph<N extends INode, E extends IEdge<N>> implements IGraph<N, E> {
  private long mAmountOfEdges;
  private final Map<N, Set<E>> mNodeToIncomingEdges;
  private final Map<N, Set<E>> mNodeToOutgoingEdges;

  public AGraph() {
    mNodeToIncomingEdges = new HashMap<>();
    mNodeToOutgoingEdges = new HashMap<>();
    mAmountOfEdges = 0L;
  }

  @Override
  public boolean addEdge(final E edge) {
    boolean wasAdded = getNodeToOutgoingEdges().computeIfAbsent(edge.getSource(), k -> new HashSet<>()).add(edge);
    wasAdded |= getNodeToIncomingEdges().computeIfAbsent(edge.getDestination(), k -> new HashSet<>()).add(edge);
    if (wasAdded) {
      mAmountOfEdges++;
    }
    return wasAdded;
  }

  @Override
  public boolean containsEdge(final E edge) {
    // We don't check the other direction, unit tests should cover this
    final Set<E> outgoingEdges = getNodeToOutgoingEdges().get(edge.getSource());
    return outgoingEdges != null && outgoingEdges.contains(edge);
  }

  @Override
  public long getAmountOfEdges() {
    return mAmountOfEdges;
  }

  @Override
  public Set<E> getIncomingEdges(final N destination) {
    final Set<E> edges = getNodeToIncomingEdges().get(destination);
    if (edges == null) {
      return Collections.emptySet();
    }
    return edges;
  }

  @Override
  public Set<E> getOutgoingEdges(final N source) {
    final Set<E> edges = getNodeToOutgoingEdges().get(source);
    if (edges == null) {
      return Collections.emptySet();
    }
    return edges;
  }

  public String getSizeInformation() {
    return toString();
  }

  @Override
  public boolean removeEdge(final E edge) {
    boolean wasRemoved = removeEdgeFromMap(edge, edge.getSource(), getNodeToOutgoingEdges());
    wasRemoved |= removeEdgeFromMap(edge, edge.getDestination(), getNodeToIncomingEdges());
    if (wasRemoved) {
      mAmountOfEdges--;
    }
    return wasRemoved;
  }

  @Override
  public long size() {
    return getNodes().size();
  }

  @Override
  public String toString() {
    final StringJoiner sj = new StringJoiner(", ", getClass().getSimpleName() + "[", "]");
    sj.add("nodes=" + size());
    sj.add("edges=" + getAmountOfEdges());
    return sj.toString();
  }

  private boolean removeEdgeFromMap(final E edge, final N keyNode, final Map<N, Set<E>> nodeToEdges) {
    final Set<E> edges = nodeToEdges.get(keyNode);
    if (edges != null) {
      final boolean wasRemoved = edges.remove(edge);
      if (edges.isEmpty()) {
        nodeToEdges.remove(keyNode);
      }
      return wasRemoved;
    }
    return false;
  }

  protected Map<N, Set<E>> getNodeToIncomingEdges() {
    return mNodeToIncomingEdges;
  }

  protected Map<N, Set<E>> getNodeToOutgoingEdges() {
    return mNodeToOutgoingEdges;
  }
}
