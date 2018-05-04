package de.tischner.cobweb.routing.algorithms.scc.tarjan;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tischner.cobweb.routing.algorithms.scc.ISccComputation;
import de.tischner.cobweb.routing.algorithms.scc.StronglyConnectedComponent;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;

public abstract class ATarjan<N extends INode, E extends IEdge<N>, G extends IGraph<N, E>>
    implements ISccComputation<N> {

  private int mCurrentIndex;
  private final Deque<N> mDeque;
  private final G mGraph;
  private boolean mHasComputed;
  private final Set<N> mInDeque;
  private StronglyConnectedComponent<N> mLargestScc;
  private final Map<N, Integer> mNodeToIndex;
  private final Map<N, Integer> mNodeToLowLink;
  private final Collection<StronglyConnectedComponent<N>> mSccs;

  public ATarjan(final G graph) {
    mGraph = graph;
    mNodeToIndex = new HashMap<>(graph.size());
    mNodeToLowLink = new HashMap<>(graph.size());
    mDeque = new ArrayDeque<>();
    mInDeque = new HashSet<>();
    mSccs = new ArrayList<>();
  }

  @Override
  public StronglyConnectedComponent<N> getLargestScc() {
    if (!mHasComputed) {
      computeSccs();
    }
    return mLargestScc;
  }

  @Override
  public Collection<StronglyConnectedComponent<N>> getSccs() {
    if (!mHasComputed) {
      computeSccs();
    }
    return mSccs;
  }

  protected void computeSccs() {
    for (final N node : mGraph.getNodes()) {
      if (!mNodeToIndex.containsKey(node)) {
        strongConnect(node);
      }
    }

    if (!mHasComputed) {
      mHasComputed = true;
    }
  }

  protected boolean containsIndexNode(final N node) {
    return mNodeToIndex.containsKey(node);
  }

  protected void establishScc(final N rootNode) {
    // Construct the SCC
    final StronglyConnectedComponent<N> scc = new StronglyConnectedComponent<>();
    N node;
    do {
      node = mDeque.pop();
      mInDeque.remove(node);
      scc.addNode(node);
    } while (!rootNode.equals(node));

    scc.setRootNode(rootNode);
    mSccs.add(scc);

    // Update the largest SCC
    if (scc.getSize() > mLargestScc.getSize()) {
      mLargestScc = scc;
    }
  }

  protected int getCurrentIndex() {
    return mCurrentIndex;
  }

  protected int getIndex(final N node) {
    return mNodeToIndex.get(node);
  }

  protected int getLowLink(final N node) {
    return mNodeToLowLink.get(node);
  }

  protected Set<E> getOutgoingEdges(final N node) {
    return mGraph.getOutgoingEdges(node);
  }

  protected void incrementCurrentIndex() {
    mCurrentIndex++;
  }

  protected boolean isInDeque(final N node) {
    return mInDeque.contains(node);
  }

  protected void pushToDeque(final N node) {
    mDeque.push(node);
    mInDeque.add(node);
  }

  protected void putIndex(final N node, final int index) {
    mNodeToIndex.put(node, index);
  }

  protected void putLowLink(final N node, final int lowLink) {
    mNodeToLowLink.put(node, lowLink);
  }

  protected abstract void strongConnect(N node);

  protected void updateLowLink(final N node, final int value) {
    final int lowLink = Math.min(mNodeToLowLink.get(node), value);
    mNodeToLowLink.put(node, lowLink);
  }

}
