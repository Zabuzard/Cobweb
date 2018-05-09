package de.tischner.cobweb.routing.model.graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 * Abstract implementation of the {@link IGraph} model. Implements some utility
 * methods and everything related to edges.<br>
 * <br>
 * The core methods that deal with nodes like {@link #addNode(INode)},
 * {@link #removeNode(INode)} and {@link #getNodes()} as well as
 * {@link #reverse()} are not implemented.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 * @param <N> The type of nodes
 * @param <E> The type of edges
 */
public abstract class AGraph<N extends INode & Serializable, E extends IEdge<N> & Serializable>
    implements IGraph<N, E>, Serializable {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The amount of edges in this graph.
   */
  private int mAmountOfEdges;
  /**
   * A map that connects nodes to their incoming edges.
   */
  private final Map<N, Set<E>> mNodeToIncomingEdges;
  /**
   * A map that connects nodes to their outgoing edges.
   */
  private final Map<N, Set<E>> mNodeToOutgoingEdges;

  /**
   * Creates a new initially empty graph.
   */
  public AGraph() {
    mNodeToIncomingEdges = new HashMap<>();
    mNodeToOutgoingEdges = new HashMap<>();
    mAmountOfEdges = 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.routing.model.graph.IGraph#addEdge(de.tischner.cobweb.
   * routing.model.graph.IEdge)
   */
  @Override
  public boolean addEdge(final E edge) {
    boolean wasAdded = getNodeToOutgoingEdges().computeIfAbsent(edge.getSource(), k -> new HashSet<>()).add(edge);
    wasAdded |= getNodeToIncomingEdges().computeIfAbsent(edge.getDestination(), k -> new HashSet<>()).add(edge);
    if (wasAdded) {
      mAmountOfEdges++;
    }
    return wasAdded;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.routing.model.graph.IGraph#containsEdge(de.tischner.cobweb
   * .routing.model.graph.IEdge)
   */
  @Override
  public boolean containsEdge(final E edge) {
    // We don't check the other direction, unit tests should cover this
    final Set<E> outgoingEdges = getNodeToOutgoingEdges().get(edge.getSource());
    return outgoingEdges != null && outgoingEdges.contains(edge);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.tischner.cobweb.routing.model.graph.IGraph#getAmountOfEdges()
   */
  @Override
  public int getAmountOfEdges() {
    return mAmountOfEdges;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.tischner.cobweb.routing.model.graph.IGraph#getEdges()
   */
  @Override
  public Stream<E> getEdges() {
    return mNodeToOutgoingEdges.values().stream().flatMap(Collection::stream);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.routing.model.graph.IGraph#getIncomingEdges(de.tischner.
   * cobweb.routing.model.graph.INode)
   */
  @Override
  public Set<E> getIncomingEdges(final N destination) {
    final Set<E> edges = getNodeToIncomingEdges().get(destination);
    if (edges == null) {
      return Collections.emptySet();
    }
    return edges;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.routing.model.graph.IGraph#getOutgoingEdges(de.tischner.
   * cobweb.routing.model.graph.INode)
   */
  @Override
  public Set<E> getOutgoingEdges(final N source) {
    final Set<E> edges = getNodeToOutgoingEdges().get(source);
    if (edges == null) {
      return Collections.emptySet();
    }
    return edges;
  }

  /**
   * Gets a human readable string that contains size information of the graph,
   * i.e. the amount of nodes and edges.
   *
   * @return A human readable string containing size information
   */
  public String getSizeInformation() {
    return toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.routing.model.graph.IGraph#removeEdge(de.tischner.cobweb.
   * routing.model.graph.IEdge)
   */
  @Override
  public boolean removeEdge(final E edge) {
    boolean wasRemoved = removeEdgeFromMap(edge, edge.getSource(), getNodeToOutgoingEdges());
    wasRemoved |= removeEdgeFromMap(edge, edge.getDestination(), getNodeToIncomingEdges());
    if (wasRemoved) {
      mAmountOfEdges--;
    }
    return wasRemoved;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.tischner.cobweb.routing.model.graph.IGraph#size()
   */
  @Override
  public int size() {
    return getNodes().size();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringJoiner sj = new StringJoiner(", ", getClass().getSimpleName() + "[", "]");
    sj.add("nodes=" + size());
    sj.add("edges=" + getAmountOfEdges());
    return sj.toString();
  }

  /**
   * Removes the given edge from the given map by using the given key.<br>
   * <br>
   * If the edge set is empty after removal, the key is removed from the map too.
   *
   * @param edge        The edge to remove
   * @param keyNode     The key of the set where the edge is to be removed from
   * @param nodeToEdges The map that connects nodes to a set of edges
   * @return <tt>True</tt> if the edge was found and thus removed, <tt>false</tt>
   *         otherwise
   */
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

  /**
   * Gets a map that connects nodes to their incoming edges. The map is backed by
   * the graph, changes will be reflected in the graph.<br>
   * <br>
   * Do only change the map directly if you know the consequences. Else the graph
   * can easily get into a corrupted state. In many situations it is best to use
   * the given methods like {@link #addEdge(IEdge)} instead.
   *
   * @return A map connecting nodes to their incoming edges
   */
  protected Map<N, Set<E>> getNodeToIncomingEdges() {
    return mNodeToIncomingEdges;
  }

  /**
   * Gets a map that connects nodes to their outgoing edges. The map is backed by
   * the graph, changes will be reflected in the graph.<br>
   * <br>
   * Do only change the map directly if you know the consequences. Else the graph
   * can easily get into a corrupted state. In many situations it is best to use
   * the given methods like {@link #addEdge(IEdge)} instead.
   *
   * @return A map connecting nodes to their outgoing edges
   */
  protected Map<N, Set<E>> getNodeToOutgoingEdges() {
    return mNodeToOutgoingEdges;
  }
}
