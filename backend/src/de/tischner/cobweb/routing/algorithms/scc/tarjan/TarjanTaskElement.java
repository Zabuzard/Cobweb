package de.tischner.cobweb.routing.algorithms.scc.tarjan;

import de.tischner.cobweb.routing.model.graph.INode;

public final class TarjanTaskElement<N extends INode> {
  private ETarjanTask mCurrentTask;
  private final N mNode;
  private final N mPredecessor;

  public TarjanTaskElement(final N node, final N predecessor) {
    mNode = node;
    mPredecessor = predecessor;
    mCurrentTask = ETarjanTask.INDEX;
  }

  public ETarjanTask getCurrentTask() {
    return mCurrentTask;
  }

  public N getNode() {
    return mNode;
  }

  public N getPredecessor() {
    return mPredecessor;
  }

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
