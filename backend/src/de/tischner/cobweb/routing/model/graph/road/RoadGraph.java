package de.tischner.cobweb.routing.model.graph.road;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de.tischner.cobweb.routing.model.graph.AGraph;
import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;

public final class RoadGraph<N extends INode & IHasId & ISpatial & Serializable, E extends IEdge<N> & IHasId & IReversedConsumer & Serializable>
    extends AGraph<N, E> implements ICanGetNodeById<N>, IReversedProvider {
  private static final long serialVersionUID = 1L;
  private final Map<Long, N> mIdToNode;
  private boolean mIsReversed;

  public RoadGraph() {
    mIdToNode = new HashMap<>();
  }

  @Override
  public boolean addEdge(final E edge) {
    edge.setReversedProvider(this);
    return super.addEdge(edge);
  }

  @Override
  public boolean addNode(final N node) {
    final Long id = node.getId();
    if (mIdToNode.containsKey(id)) {
      return false;
    }
    mIdToNode.put(id, node);
    return true;
  }

  @Override
  public boolean containsNodeWithId(final long id) {
    return mIdToNode.containsKey(id);
  }

  @Override
  public Optional<N> getNodeById(final long id) {
    return Optional.ofNullable(mIdToNode.get(id));
  }

  @Override
  public Collection<N> getNodes() {
    return mIdToNode.values();
  }

  @Override
  public boolean isReversed() {
    return mIsReversed;
  }

  @Override
  public boolean removeNode(final N node) {
    final Long id = node.getId();
    if (!mIdToNode.containsKey(id)) {
      return false;
    }

    // Remove all incoming and outgoing edges
    getIncomingEdges(node).forEach(this::removeEdge);
    getOutgoingEdges(node).forEach(this::removeEdge);

    mIdToNode.remove(id);
    return true;
  }

  @Override
  public void reverse() {
    mIsReversed = !mIsReversed;
  }

  @Override
  protected Map<N, Set<E>> getNodeToIncomingEdges() {
    if (mIsReversed) {
      return super.getNodeToOutgoingEdges();
    }
    return super.getNodeToIncomingEdges();
  }

  @Override
  protected Map<N, Set<E>> getNodeToOutgoingEdges() {
    if (mIsReversed) {
      return super.getNodeToIncomingEdges();
    }
    return super.getNodeToOutgoingEdges();
  }

}
