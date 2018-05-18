package de.tischner.cobweb.routing.model.graph.road;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import de.tischner.cobweb.routing.model.graph.AGraph;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;

/**
 * Implementation of a {@link IGraph} model which consists of road nodes and
 * edges.<br>
 * <br>
 * It offers access to the nodes by their unique ID and is capable of implicitly
 * reversing nodes in constant time. The class is fully serializable.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the nodes which must have an ID and be spatial
 * @param <E> Type of the edges which must have an ID and be able to consume a
 *        {@link IReversedProvider}
 */
public final class RoadGraph<N extends INode & IHasId & ISpatial & Serializable,
    E extends IEdge<N> & IHasId & IReversedConsumer & Serializable> extends AGraph<N, E>
    implements IGetNodeById<N>, IReversedProvider, IUniqueIdGenerator {
  /**
   * The unique ID for the last possible entity. Is used to determine when the
   * generator is out of IDs.
   */
  private static final int LAST_ID = -1;
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * A map connecting node IDs to their corresponding nodes.
   */
  private final Map<Integer, N> mIdToNode;
  /**
   * Whether or not the graph is currently reversed.
   */
  private boolean mIsReversed;
  /**
   * The unique ID used for the last node.
   */
  private int mLastNodeId;
  /**
   * The unique ID used for the last way.
   */
  private int mLastWayId;

  /**
   * Creates a new initially empty road graph.
   */
  public RoadGraph() {
    mIdToNode = new HashMap<>();
    mLastNodeId = LAST_ID;
    mLastWayId = LAST_ID;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.routing.model.graph.AGraph#addEdge(de.tischner.cobweb.
   * routing.model.graph.IEdge)
   */
  @Override
  public boolean addEdge(final E edge) {
    edge.setReversedProvider(this);
    return super.addEdge(edge);
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.routing.model.graph.IGraph#addNode(de.tischner.cobweb.
   * routing.model.graph.INode)
   */
  @Override
  public boolean addNode(final N node) {
    final int id = node.getId();
    if (mIdToNode.containsKey(id)) {
      return false;
    }
    mIdToNode.put(id, node);
    return true;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById#
   * containsNodeWithId(int)
   */
  @Override
  public boolean containsNodeWithId(final int id) {
    return mIdToNode.containsKey(id);
  }

  @Override
  public int generateUniqueNodeId() throws NoSuchElementException {
    mLastNodeId++;
    if (mLastNodeId == LAST_ID) {
      throw new NoSuchElementException();
    }
    return mLastNodeId;
  }

  @Override
  public int generateUniqueWayId() throws NoSuchElementException {
    mLastWayId++;
    if (mLastWayId == LAST_ID) {
      throw new NoSuchElementException();
    }
    return mLastWayId;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById#getNodeById(
   * int)
   */
  @Override
  public Optional<N> getNodeById(final int id) {
    return Optional.ofNullable(mIdToNode.get(id));
  }

  /**
   * Gets a collection of all nodes that the graph contains.<br>
   * <br>
   * The collection is backed by the graph, changes will be reflected in the
   * graph. Do only change the collection directly if you know the consequences.
   * Else the graph can easily get into a corrupted state. In many situations it
   * is best to use the given methods like {@link #addNode(INode)} instead.
   */
  @Override
  public Collection<N> getNodes() {
    return mIdToNode.values();
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.routing.model.graph.road.IReversedProvider#isReversed()
   */
  @Override
  public boolean isReversed() {
    return mIsReversed;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.IGraph#removeNode(de.tischner.
   * cobweb. routing.model.graph.INode)
   */
  @Override
  public boolean removeNode(final N node) {
    final int id = node.getId();
    if (!mIdToNode.containsKey(id)) {
      return false;
    }

    // Remove all incoming and outgoing edges
    getIncomingEdges(node).forEach(this::removeEdge);
    getOutgoingEdges(node).forEach(this::removeEdge);

    mIdToNode.remove(id);
    return true;
  }

  /**
   * Reverses the graph. That is, all directed edges switch source with
   * destination.<br>
   * <br>
   * The implementation runs in constant time, edge reversal is only made
   * implicit.
   */
  @Override
  public void reverse() {
    mIsReversed = !mIsReversed;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.AGraph#getNodeToIncomingEdges()
   */
  @Override
  protected Map<N, Set<E>> getNodeToIncomingEdges() {
    if (mIsReversed) {
      return super.getNodeToOutgoingEdges();
    }
    return super.getNodeToIncomingEdges();
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.AGraph#getNodeToOutgoingEdges()
   */
  @Override
  protected Map<N, Set<E>> getNodeToOutgoingEdges() {
    if (mIsReversed) {
      return super.getNodeToIncomingEdges();
    }
    return super.getNodeToOutgoingEdges();
  }

}
