package de.tischner.cobweb.routing.model.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;

import de.tischner.cobweb.routing.model.graph.road.IGetNodeById;

/**
 * Basic implementation of a graph which operates on {@link BasicNode}s and
 * {@link BasicEdge}s.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class BasicGraph extends AGraph<BasicNode, BasicEdge<BasicNode>> implements IGetNodeById<BasicNode> {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * A map connecting unique IDs to nodes.
   */
  private final MutableIntObjectMap<BasicNode> mNodes;

  /**
   * Creates a new initially empty graph.
   */
  public BasicGraph() {
    mNodes = IntObjectMaps.mutable.empty();
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.routing.model.graph.IGraph#addNode(de.tischner.cobweb.
   * routing.model.graph.INode)
   */
  @Override
  public boolean addNode(final BasicNode node) {
    if (mNodes.containsKey(node.getId())) {
      return false;
    }
    mNodes.put(node.getId(), node);
    return true;
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById#
   * containsNodeWithId(int)
   */
  @Override
  public boolean containsNodeWithId(final int id) {
    return mNodes.containsKey(id);
  }

  /*
   * (non-Javadoc)
   * @see
   * de.tischner.cobweb.routing.model.graph.road.ICanGetNodeById#getNodeById(
   * int)
   */
  @Override
  public Optional<BasicNode> getNodeById(final int id) {
    return Optional.ofNullable(mNodes.get(id));
  }

  /**
   * Gets a collection of all nodes that the graph contains.<br>
   * <br>
   * The collection is backed by the graph, changes will be reflected in the
   * graph. Do only change the collection directly if you know the consequences.
   * Else the graph can easily get into a corrupted state. In many situations it
   * is best to use the given methods like {@link #addNode(BasicNode)} instead.
   */
  @Override
  public Collection<BasicNode> getNodes() {
    return mNodes.values();
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.IGraph#removeNode(de.tischner.
   * cobweb. routing.model.graph.INode)
   */
  @Override
  public boolean removeNode(final BasicNode node) {
    if (!mNodes.containsKey(node.getId())) {
      return false;
    }
    mNodes.remove(node.getId());
    return true;
  }

  /**
   * Reverses the graph. That is, all directed edges switch source with
   * destination.<br>
   * <br>
   * The implementation runs in <tt>O(|E|)</tt>, that is in the amount of edges.
   * Edge reversal is made explicit by replacing all previous edges with new
   * edges that have the same ID but are reversed.
   */
  @Override
  public void reverse() {
    final List<BasicEdge<BasicNode>> currentEdges = getEdges().collect(Collectors.toList());
    final List<BasicEdge<BasicNode>> reversedEdges = new ArrayList<>();
    currentEdges.forEach(edge -> reversedEdges
        .add(new BasicEdge<>(edge.getId(), edge.getDestination(), edge.getSource(), edge.getCost())));

    currentEdges.forEach(this::removeEdge);
    reversedEdges.forEach(this::addEdge);
  }

}
