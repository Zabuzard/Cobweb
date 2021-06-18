package de.unifreiburg.informatik.cobweb.routing.algorithms.scc.tarjan;

import java.util.ArrayDeque;
import java.util.Deque;

import de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IGraph;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;

/**
 * Iterative implementation of Tarjans algorithm for computing SCCs.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the node
 * @param <E> The type of the edge
 * @param <G> The type of the graph
 */
public final class TarjanIterative<N extends INode, E extends IEdge<N>, G extends IGraph<N, E>>
    extends ATarjan<N, E, G> {
  /**
   * The deque of task elements.
   */
  private final Deque<TarjanTaskElement<N>> mTaskDeque;

  /**
   * Creates an iterative Tarjan instance.
   *
   * @param graph The graph to compute SCCs on
   */
  public TarjanIterative(final G graph) {
    super(graph);
    mTaskDeque = new ArrayDeque<>();
  }

  /**
   * Processes the successors of the given node.
   *
   * @param node The node to process successors of
   */
  private void doGetSuccessorsTask(final N node) {
    getOutgoingEdges(node).forEach(edge -> {
      final N successor = edge.getDestination();
      if (containsIndexNode(successor)) {
        // Update the low link value if not visited the first time
        if (isInDeque(successor)) {
          updateLowLink(node, getIndex(successor));
        }
      } else {
        // Register successor if visited the first time
        mTaskDeque.push(new TarjanTaskElement<>(successor, node));
      }
    });
  }

  /**
   * Registers the given node to be processed.
   *
   * @param node The node to register
   */
  private void doIndexTask(final N node) {
    putIndex(node, getCurrentIndex());
    putLowLink(node, getCurrentIndex());
    incrementCurrentIndex();

    pushToDeque(node);
  }

  /**
   * Finishes the given node by updating its low link value or establishing a
   * new SCC.
   *
   * @param node        The node to update
   * @param predecessor The predecessor of the node or <code>null</code> if not
   *                    present
   */
  private void doSetLowLinkTask(final N node, final N predecessor) {
    // If the low link value is equal to the index, the node is the root of the
    // SCC
    if (getIndex(node) == getLowLink(node)) {
      establishScc(node);
    }

    // Update predecessors low link value if present
    if (predecessor != null) {
      updateLowLink(predecessor, getLowLink(node));
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * de.unifreiburg.informatik.cobweb.routing.algorithms.scc.tarjan.ATarjan#
   * strongConnect(de. tischner.cobweb.routing.model.graph.INode)
   */
  @Override
  protected void strongConnect(final N node) {
    mTaskDeque.push(new TarjanTaskElement<>(node, null));

    // Process all task elements
    while (!mTaskDeque.isEmpty()) {
      final TarjanTaskElement<N> task = mTaskDeque.pop();
      final N currentNode = task.getNode();
      switch (task.getCurrentTask()) {
        case INDEX:
          // Register node if visited the first time
          if (containsIndexNode(currentNode)) {
            continue;
          }

          doIndexTask(currentNode);

          // Push the next task
          task.reportTaskAccomplished();
          mTaskDeque.push(task);
          break;
        case GET_SUCCESSORS:
          // Push the next task
          task.reportTaskAccomplished();
          mTaskDeque.push(task);

          doGetSuccessorsTask(currentNode);
          break;
        case SET_LOWLINK:
          doSetLowLinkTask(currentNode, task.getPredecessor());

          // All tasks of this element have been completed
          task.reportTaskAccomplished();
          break;
        default:
          throw new AssertionError();
      }
    }
  }
}
