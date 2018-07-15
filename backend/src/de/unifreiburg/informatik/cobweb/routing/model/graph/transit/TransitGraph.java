package de.unifreiburg.informatik.cobweb.routing.model.graph.transit;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;

import de.unifreiburg.informatik.cobweb.routing.model.graph.AGraph;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IGetNodeById;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IGraph;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IHasId;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IReversedConsumer;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IReversedProvider;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ISpatial;
import de.unifreiburg.informatik.cobweb.routing.model.graph.UniqueIdGenerator;
import de.unifreiburg.informatik.cobweb.util.collections.HybridArrayHashSet;
import de.unifreiburg.informatik.cobweb.util.collections.IdMap;

/**
 * Implementation of a {@link IGraph} model which consists of transit nodes and
 * edges.<br>
 * <br>
 * It offers access to the nodes by their unique station ID together with a time
 * and is capable of implicitly reversing nodes in constant time. The class is
 * fully serializable.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the nodes which must have an ID and be spatial
 * @param <E> Type of the edges which must have an ID and be able to consume a
 *        {@link IReversedProvider}
 */
public final class TransitGraph<N extends INode & IHasId & ISpatial & Serializable,
    E extends IEdge<N> & IReversedConsumer & Serializable> extends AGraph<N, E>
    implements IGetNodeById<N>, IReversedProvider, ITransitIdGenerator, IHasTransitStops<N> {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The unique ID generator used for edges.
   */
  private final UniqueIdGenerator mEdgeIdGenerator;
  /**
   * A map connecting node IDs to their corresponding nodes.
   */
  private final MutableIntObjectMap<N> mIdToNode;
  /**
   * Whether or not the graph is currently reversed.
   */
  private boolean mIsReversed;
  /**
   * The unique ID generator used for nodes.
   */
  private final UniqueIdGenerator mNodeIdGenerator;
  /**
   * A map that connects nodes to their incoming edges.
   */
  private final Map<N, Set<E>> mNodeToIncomingEdges;
  /**
   * A map that connects nodes to their outgoing edges.
   */
  private final Map<N, Set<E>> mNodeToOutgoingEdges;
  /**
   * A list of all stops of this graph.
   */
  private final MutableSet<TransitStop<N>> mStops;

  /**
   * Creates a new initially empty transit graph.
   */
  public TransitGraph() {
    // TODO The map could be exchanged by an array. However, from a design-view
    // it is problematic that IDs could have gaps and thus methods like
    // getNodes() which return a Collection and not only a Stream get
    // problematic due to possible null values encoding gaps.
    mIdToNode = IntObjectMaps.mutable.empty();
    mNodeIdGenerator = new UniqueIdGenerator();
    mEdgeIdGenerator = new UniqueIdGenerator();

    // Assume node IDs are close to each other and have no, or only few, gaps.
    mNodeToIncomingEdges = new IdMap<>();
    mNodeToOutgoingEdges = new IdMap<>();
    mStops = Sets.mutable.empty();
  }

  @Override
  public boolean addEdge(final E edge) {
    edge.setReversedProvider(this);
    return super.addEdge(edge);
  }

  @Override
  public boolean addNode(final N node) {
    final int id = node.getId();
    if (mIdToNode.containsKey(id)) {
      return false;
    }
    mIdToNode.put(id, node);
    return true;
  }

  @Override
  public boolean addStop(final TransitStop<N> stop) {
    return mStops.add(stop);
  }

  @Override
  public boolean containsNodeWithId(final int id) {
    return mIdToNode.containsKey(id);
  }

  @Override
  public int generateUniqueEdgeId() throws NoSuchElementException {
    return mEdgeIdGenerator.generateUniqueId();
  }

  @Override
  public int generateUniqueNodeId() throws NoSuchElementException {
    return mNodeIdGenerator.generateUniqueId();
  }

  @Override
  public Stream<E> getEdges() {
    if (mNodeToOutgoingEdges instanceof IdMap) {
      final IdMap<N, Set<E>> asIdMap = (IdMap<N, Set<E>>) mNodeToOutgoingEdges;
      // Fall back to streamValues() since IdMap does not support values()
      return asIdMap.streamValues().flatMap(Collection::stream);
    }
    return super.getEdges();
  }

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

  @Override
  public Collection<TransitStop<N>> getStops() {
    return mStops;
  }

  @Override
  public boolean isReversed() {
    return mIsReversed;
  }

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

  @Override
  public boolean removeStop(final TransitStop<N> stop) {
    return mStops.remove(stop);
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

  @Override
  protected Set<E> constructEdgeSetWith(final E edge) {
    // Assume that edge sets only contain a very limited amount of edges.
    return new HybridArrayHashSet<>(edge);
  }

  @Override
  protected Map<N, Set<E>> getNodeToIncomingEdges() {
    if (mIsReversed) {
      return mNodeToOutgoingEdges;
    }
    return mNodeToIncomingEdges;
  }

  @Override
  protected Map<N, Set<E>> getNodeToOutgoingEdges() {
    if (mIsReversed) {
      return mNodeToIncomingEdges;
    }
    return mNodeToOutgoingEdges;
  }

}
