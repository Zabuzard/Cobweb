package de.tischner.cobweb.routing.model.graph;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Interface for a graph model. A graph consists of nodes and edges connecting
 * the nodes.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 */
public interface IGraph<N extends INode, E extends IEdge<N>> {
  /**
   * Adds the given edge to the graph if not already contained.
   *
   * @param edge The edge to add
   * @return <tt>True</tt> if the edge was not already contained and thus added,
   *         <tt>false</tt> otherwise
   */
  boolean addEdge(E edge);

  /**
   * Adds the given node to the graph if not already contained.
   *
   * @param node The node to add
   * @return <tt>True</tt> if the node was not already contained and thus added,
   *         <tt>false</tt> otherwise
   */
  boolean addNode(N node);

  /**
   * Whether or not the given edge is contained in the graph.
   *
   * @param edge The edge in question
   * @return <tt>True</tt> if the edge is contained in the graph, <tt>false</tt>
   *         otherwise
   */
  boolean containsEdge(E edge);

  /**
   * Gets the amount of edges contained in the graph. This operation should run
   * in <tt>O(1)</tt>.
   *
   * @return The amount of edges contained in the graph
   */
  int getAmountOfEdges();

  /**
   * A stream over all edges this graph contains. The construction of the stream
   * should run in <tt>O(1)</tt>.
   *
   * @return A stream over all edges this graph contains
   */
  Stream<E> getEdges();

  /**
   * Gets a set of all edges that have the given node as destination.<br>
   * <br>
   * There are no guarantees made on if the set is backed by the graph or not.
   *
   * @param destination The destination to get incoming edges for
   * @return A set of all incoming edges
   */
  Set<E> getIncomingEdges(N destination);

  /**
   * Gets a collection of all nodes that the graph contains.<br>
   * <br>
   * There are no guarantees made on if the collection is backed by the graph or
   * not.
   *
   * @return A collection of all contained nodes
   */
  Collection<N> getNodes();

  /**
   * Gets a set of all edges that have the given node as source.<br>
   * <br>
   * There are no guarantees made on if the set is backed by the graph or not.
   *
   * @param source The source to get outgoing edges for
   * @return A set of all outgoing edges
   */
  Set<E> getOutgoingEdges(N source);

  /**
   * Removes the given edge if it is contained in the graph.
   *
   * @param edge The edge to remove
   * @return <tt>True</tt> if the edge was contained and thus removed,
   *         <tt>false</tt> otherwise
   */
  boolean removeEdge(E edge);

  /**
   * Removes the given node if it is contained in the graph.
   *
   * @param node The node to remove
   * @return <tt>True</tt> if the node was contained and thus removed,
   *         <tt>false</tt> otherwise
   */
  boolean removeNode(N node);

  /**
   * Reverses the graph. That is, all directed edges switch source with
   * destination.<br>
   * <br>
   * There are no requirements made on the time complexity. It is up to the
   * implementing class if this method runs fast or if it explicitly reverses
   * each edge. However, edges retrieved before the reversal still need to
   * remain equal to the edges after the reversal, according to their
   * <tt>equals</tt> method.
   */
  void reverse();

  /**
   * Gets the amount of nodes contained in the graph. This operation should run
   * in <tt>O(1)</tt>.
   *
   * @return The amount of nodes contained in the graph
   */
  int size();
}
