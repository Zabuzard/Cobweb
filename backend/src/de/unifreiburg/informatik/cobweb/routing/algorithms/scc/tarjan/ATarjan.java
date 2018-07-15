package de.unifreiburg.informatik.cobweb.routing.algorithms.scc.tarjan;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;

import de.unifreiburg.informatik.cobweb.routing.algorithms.scc.ISccComputation;
import de.unifreiburg.informatik.cobweb.routing.algorithms.scc.StronglyConnectedComponent;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IGraph;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;

/**
 * Abstract class for implementations of {@link ISccComputation} that use
 * Tarjans algorithm. Provides all necessary components of the algorithm like
 * indices and low link values for nodes, as well as a deque of nodes.<br>
 * <br>
 * Subclasses need to implement the core method {@link #strongConnect(INode)}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the nodes
 * @param <E> Type of the egdes
 * @param <G> Type of the graph
 */
public abstract class ATarjan<N extends INode, E extends IEdge<N>, G extends IGraph<N, E>>
    implements ISccComputation<N> {
  /**
   * The current node index.
   */
  private int mCurrentIndex;
  /**
   * A deque containing the nodes to process.
   */
  private final Deque<N> mDeque;
  /**
   * The graph to operate on.
   */
  private final G mGraph;
  /**
   * Whether or not the SCCs where computed already.
   */
  private boolean mHasComputed;
  /**
   * The nodes currently contained in the deque.
   */
  private final Set<N> mInDeque;
  /**
   * The largest SCC in the graph, if computed already. Else <tt>null</tt>.
   */
  private StronglyConnectedComponent<N> mLargestScc;
  /**
   * A map connecting nodes to their index.
   */
  private final MutableObjectIntMap<N> mNodeToIndex;
  /**
   * A map connecting nodes to their low link value.
   */
  private final ObjectIntHashMap<N> mNodeToLowLink;
  /**
   * A collection containing all SCCs of the graph, if computed already. Else
   * empty.
   */
  private final Collection<StronglyConnectedComponent<N>> mSccs;

  /**
   * Creates a new Tarjan instance which operates on the given graph.
   *
   * @param graph The graph to compute SCCs on
   */
  public ATarjan(final G graph) {
    mGraph = graph;
    mNodeToIndex = new ObjectIntHashMap<>(graph.size());
    mNodeToLowLink = new ObjectIntHashMap<>(graph.size());
    mDeque = new ArrayDeque<>();
    mInDeque = new HashSet<>();
    mSccs = new ArrayList<>();
  }

  /**
   * Gets the largest SCC in the graph. The first call to this method will
   * trigger a computation of all SCCs.
   */
  @Override
  public StronglyConnectedComponent<N> getLargestScc() {
    if (!mHasComputed) {
      computeSccs();
    }
    return mLargestScc;
  }

  /**
   * Gets a collection of all SCCs in the graph. The first call to this method
   * will trigger a computation of all SCCs.
   */
  @Override
  public Collection<StronglyConnectedComponent<N>> getSccs() {
    if (!mHasComputed) {
      computeSccs();
    }
    return mSccs;
  }

  /**
   * Computes all SCCs of the given graph. Calls {@link #strongConnect(INode)}
   * on all nodes that have no index already.
   */
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

  /**
   * Whether or not the given node is registered as index node.
   *
   * @param node The node in question
   * @return <tt>True</tt> if the node is registered as index node,
   *         <tt>false</tt> otherwise
   */
  protected boolean containsIndexNode(final N node) {
    return mNodeToIndex.containsKey(node);
  }

  /**
   * Establishes the SCC belonging to the given root node by popping nodes from
   * the deque.
   *
   * @param rootNode The root node of the SCC
   */
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
    if (mLargestScc == null || scc.size() > mLargestScc.size()) {
      mLargestScc = scc;
    }
  }

  /**
   * Gets the current index of the algorithm.
   *
   * @return The current index
   */
  protected int getCurrentIndex() {
    return mCurrentIndex;
  }

  /**
   * Gets the index of the given node. The node must be registered as index node
   * already.
   *
   * @param node The node to get the index for
   * @return The index of the given node
   */
  protected int getIndex(final N node) {
    return mNodeToIndex.get(node);
  }

  /**
   * Gets the low link value of the given node. The node must have a low link
   * value already.
   *
   * @param node The node to get the value for
   * @return The low link value of the given node
   */
  protected int getLowLink(final N node) {
    return mNodeToLowLink.get(node);
  }

  /**
   * Gets a stream of all outgoing edges of the given node.
   *
   * @param node The node to get edges from
   * @return A stream of all outgoing edges of the given node
   */
  protected Stream<E> getOutgoingEdges(final N node) {
    return mGraph.getOutgoingEdges(node);
  }

  /**
   * Increments the current index by one.
   */
  protected void incrementCurrentIndex() {
    mCurrentIndex++;
  }

  /**
   * Whether or not the given node is in the deque. This operations runs in
   * <tt>O(1)</tt>.
   *
   * @param node The node in question
   * @return <tt>True</tt> if the given node is contained in the deque,
   *         <tt>false</tt> otherwise
   */
  protected boolean isInDeque(final N node) {
    return mInDeque.contains(node);
  }

  /**
   * Pushes the given node to the deque.
   *
   * @param node The node to push
   */
  protected void pushToDeque(final N node) {
    mDeque.push(node);
    mInDeque.add(node);
  }

  /**
   * Puts the given index for the node. Registers the node as index node if it
   * was not already.
   *
   * @param node  The node to put the index for
   * @param index The index to put
   */
  protected void putIndex(final N node, final int index) {
    mNodeToIndex.put(node, index);
  }

  /**
   * Puts the given low link value for the node.
   *
   * @param node    The node to put the value for
   * @param lowLink The low link value to put
   */
  protected void putLowLink(final N node, final int lowLink) {
    mNodeToLowLink.put(node, lowLink);
  }

  /**
   * The main logic of the algorithm which finds nodes that are strongly
   * connected to the given node.
   *
   * @param node The node to connect
   */
  protected abstract void strongConnect(N node);

  /**
   * Updates the low link value of the given node with the given value. The
   * minimum between the given and the previous low link value is used.
   *
   * @param node  The node to update the low link value for
   * @param value The value to update with
   */
  protected void updateLowLink(final N node, final int value) {
    final int lowLink = Math.min(mNodeToLowLink.get(node), value);
    mNodeToLowLink.put(node, lowLink);
  }

}
