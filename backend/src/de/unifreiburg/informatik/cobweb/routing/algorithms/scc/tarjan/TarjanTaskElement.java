package de.unifreiburg.informatik.cobweb.routing.algorithms.scc.tarjan;

/**
 * Task element that wraps a node adding the current task for Tarjans algorithm.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> The type of the node
 */
public final class TarjanTaskElement<N> {
  /**
   * The current task to execute for the node.
   */
  private ETarjanTask mCurrentTask;
  /**
   * The node wrapped by this element.
   */
  private final N mNode;
  /**
   * The predecessor of this node or <code>null</code> if not present
   */
  private final N mPredecessor;

  /**
   * Creates a new Tarjan task element for the given node and predecessor.
   *
   * @param node        The node to wrap
   * @param predecessor The predecessor of the node or <code>null</code> if not
   *                    present
   */
  public TarjanTaskElement(final N node, final N predecessor) {
    mNode = node;
    mPredecessor = predecessor;
    mCurrentTask = ETarjanTask.INDEX;
  }

  /**
   * Gets the current task for this element or <code>null</code> if all tasks have
   * been accomplished.
   *
   * @return The current task for this element or <code>null</code>
   */
  public ETarjanTask getCurrentTask() {
    return mCurrentTask;
  }

  /**
   * Gets the node wrapped by this element.
   *
   * @return The node wrapped by this element
   */
  public N getNode() {
    return mNode;
  }

  /**
   * The predecessor of this element or <code>null</code> if not present
   *
   * @return The predecessor or <code>null</code>
   */
  public N getPredecessor() {
    return mPredecessor;
  }

  /**
   * Reports the current task as accomplished and moves over to the next task.
   * If all tasks have been executed, the task is set to <code>null</code>.
   */
  public void reportTaskAccomplished() {
    if (mCurrentTask == null) {
      return;
    }

    switch (mCurrentTask) {
      case INDEX:
        mCurrentTask = ETarjanTask.GET_SUCCESSORS;
        return;
      case GET_SUCCESSORS:
        mCurrentTask = ETarjanTask.SET_LOWLINK;
        return;
      case SET_LOWLINK:
        mCurrentTask = null;
        return;
      default:
        throw new AssertionError();
    }
  }
}
