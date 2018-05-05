package de.tischner.cobweb.routing.algorithms.scc.tarjan;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;

/**
 * Recursive implementation of Tarjans algorithm for computing SCCs.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 * @param <N> The type of the node
 * @param <E> The type of the edge
 * @param <G> The type of the graph
 */
public final class TarjanRecursive<N extends INode, E extends IEdge<N>, G extends IGraph<N, E>>
    extends ATarjan<N, E, G> {

  /**
   * Creates a recursive Tarjan instance.
   *
   * @param graph The graph to compute SCCs on
   */
  public TarjanRecursive(final G graph) {
    super(graph);
  }

  /**
   * Processes the given successor of the given node.
   *
   * @param node      The node to process the successor
   * @param successor The successor to process
   */
  private void processSuccessor(final N node, final N successor) {
    if (!containsIndexNode(successor)) {
      strongConnect(successor);
      updateLowLink(node, getLowLink(successor));
    } else if (isInDeque(successor)) {
      updateLowLink(node, getIndex(successor));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.tischner.cobweb.routing.algorithms.scc.tarjan.ATarjan#strongConnect(de.
   * tischner.cobweb.routing.model.graph.INode)
   */
  @Override
  protected void strongConnect(final N node) {
    putIndex(node, getCurrentIndex());
    putLowLink(node, getCurrentIndex());
    incrementCurrentIndex();

    pushToDeque(node);

    // Start a depth-first-search over all successors
    for (final E edge : getOutgoingEdges(node)) {
      final N successor = edge.getDestination();
      processSuccessor(node, successor);
    }

    // All reachable nodes where visited.
    // If this nodes low link value is equals to its index, then it is the root of
    // the SCC.
    if (getIndex(node) == getLowLink(node)) {
      establishScc(node);
    }
  }

}
