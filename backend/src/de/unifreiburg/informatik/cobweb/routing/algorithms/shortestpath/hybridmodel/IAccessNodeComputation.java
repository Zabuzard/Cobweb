package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.hybridmodel;

import java.util.Collection;

import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;

/**
 * Interface for algorithms that are able to compute access nodes to a given
 * node.<br>
 * <br>
 * For example, given a node in a road network, to compute frequently used stops
 * of a transit network in the vicinity of the node.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <A> The type of the node to get access nodes for
 * @param <B> The type of the access nodes
 */
public interface IAccessNodeComputation<A extends INode, B extends INode> {
  /**
   * Computes and returns the access nodes for the given node.
   *
   * @param node The node to compute access nodes for
   * @return The access nodes for the given node
   */
  Collection<B> computeAccessNodes(A node);
}
