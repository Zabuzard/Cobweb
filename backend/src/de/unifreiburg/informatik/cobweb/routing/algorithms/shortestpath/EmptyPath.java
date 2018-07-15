package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath;

import java.util.Collections;
import java.util.Iterator;

import de.unifreiburg.informatik.cobweb.routing.model.graph.EdgeCost;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IPath;

/**
 * Implementation of {@link IPath} which represent an empty path. That is a path
 * with no edges and only one node.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 */
public final class EmptyPath<N extends INode, E extends IEdge<N>> implements IPath<N, E> {
  /**
   * The node this path consists of.
   */
  private final N mNode;

  /**
   * Creates a new empty path which consists only of the given node.
   *
   * @param node The node to add
   */
  public EmptyPath(final N node) {
    mNode = node;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.unifreiburg.informatik.cobweb.routing.model.graph.IPath#getDestination(
   * )
   */
  @Override
  public N getDestination() {
    return mNode;
  }

  /*
   * (non-Javadoc)
   * @see de.unifreiburg.informatik.cobweb.routing.model.graph.IPath#getSource()
   */
  @Override
  public N getSource() {
    return mNode;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.unifreiburg.informatik.cobweb.routing.model.graph.IPath#getTotalCost()
   */
  @Override
  public double getTotalCost() {
    return 0.0;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<EdgeCost<N, E>> iterator() {
    return Collections.emptyListIterator();
  }

  /*
   * (non-Javadoc)
   * @see de.unifreiburg.informatik.cobweb.routing.model.graph.IPath#length()
   */
  @Override
  public int length() {
    return 0;
  }

}
