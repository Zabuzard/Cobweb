package de.tischner.cobweb.routing.algorithms.scc.tarjan;

import java.util.ArrayDeque;
import java.util.Deque;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;

public final class TarjanIterative<N extends INode, E extends IEdge<N>, G extends IGraph<N, E>>
    extends ATarjan<N, E, G> {

  private final Deque<TarjanTaskElement<N>> mTaskDeque;

  public TarjanIterative(final G graph) {
    super(graph);
    mTaskDeque = new ArrayDeque<>();
  }

  private void doGetSuccessorsTask(final N node) {
    for (final E edge : getOutgoingEdges(node)) {
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
    }
  }

  private void doIndexTask(final N node) {
    putIndex(node, getCurrentIndex());
    putLowLink(node, getCurrentIndex());
    incrementCurrentIndex();

    pushToDeque(node);
  }

  private void doSetLowLinkTask(final N node, final N predecessor) {
    // If the low link value is equal to the index, the node is the root of the SCC
    if (getIndex(node) == getLowLink(node)) {
      establishScc(node);
    }

    // Update predecessors low link value if present
    if (predecessor != null) {
      updateLowLink(predecessor, getLowLink(node));
    }
  }

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
