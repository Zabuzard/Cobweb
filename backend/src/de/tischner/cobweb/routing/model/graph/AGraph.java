package de.tischner.cobweb.routing.model.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AGraph<N extends INode, E extends IEdge<N>> implements IGraph<N, E> {
  private long mAmountOfEdges;
  private final Set<N> mNodes;
  private final Map<N, Set<E>> mNodeToIncomingEdges;
  private final Map<N, Set<E>> mNodeToOutgoingEdges;

  public AGraph() {
    mNodes = new HashSet<>();
    mNodeToIncomingEdges = new HashMap<>();
    mNodeToOutgoingEdges = new HashMap<>();
    mAmountOfEdges = 0L;
  }

  @Override
  public boolean addEdge(final E edge) {
    boolean wasAdded = mNodeToOutgoingEdges.computeIfAbsent(edge.getSource(), k -> new HashSet<>()).add(edge);
    wasAdded |= mNodeToIncomingEdges.computeIfAbsent(edge.getDesintation(), k -> new HashSet<>()).add(edge);
    if (wasAdded) {
      mAmountOfEdges++;
    }
    return wasAdded;
  }

  @Override
  public boolean addNode(final N node) {
    return mNodes.add(node);
  }

  @Override
  public boolean containsEdge(final E edge) {
    // We don't check the other direction, unit tests should cover this
    final Set<E> outgoingEdges = mNodeToOutgoingEdges.get(edge.getSource());
    return outgoingEdges != null && outgoingEdges.contains(edge);
  }

  @Override
  public long getAmountOfEdges() {
    return mAmountOfEdges;
  }

  @Override
  public Set<E> getIncomingEdges(final N destination) {
    final Set<E> edges = mNodeToIncomingEdges.get(destination);
    if (edges == null) {
      return Collections.emptySet();
    }
    return edges;
  }

  @Override
  public Collection<N> getNodes() {
    return mNodes;
  }

  @Override
  public Set<E> getOutgoingEdges(final N source) {
    final Set<E> edges = mNodeToOutgoingEdges.get(source);
    if (edges == null) {
      return Collections.emptySet();
    }
    return edges;
  }

  @Override
  public long getSize() {
    return mNodes.size();
  }

  @Override
  public boolean removeEdge(final E edge) {
    boolean wasRemoved = removeEdgeFromMap(edge, edge.getSource(), mNodeToOutgoingEdges);
    wasRemoved |= removeEdgeFromMap(edge, edge.getDesintation(), mNodeToIncomingEdges);
    if (wasRemoved) {
      mAmountOfEdges--;
    }
    return wasRemoved;
  }

  @Override
  public boolean removeNode(final N node) {
    if (!mNodes.contains(node)) {
      return false;
    }

    // Remove all incoming and outgoing edges
    mNodeToIncomingEdges.get(node).forEach(this::removeEdge);
    mNodeToOutgoingEdges.get(node).forEach(this::removeEdge);

    mNodes.remove(node);
    return true;
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
}
