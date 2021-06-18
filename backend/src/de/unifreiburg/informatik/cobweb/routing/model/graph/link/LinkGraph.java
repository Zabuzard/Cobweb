package de.unifreiburg.informatik.cobweb.routing.model.graph.link;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Stream;

import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.ICoreNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IGetNodeById;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IGraph;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IReversedProvider;
import de.unifreiburg.informatik.cobweb.routing.model.graph.road.RoadEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.road.RoadGraph;
import de.unifreiburg.informatik.cobweb.routing.model.graph.road.RoadNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.NodeTime;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitGraph;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitNode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.transit.TransitStop;
import de.unifreiburg.informatik.cobweb.util.collections.DoubletonCollection;

/**
 * Graph implementation which links a given road and transit graph by
 * {@link LinkEdge}s.<br>
 * <br>
 * Use {@link #initializeHubConnections(Map)} after creation to determine how
 * the graphs are to be connected.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class LinkGraph
    implements IGetNodeById<ICoreNode>, IReversedProvider, IGraph<ICoreNode, ICoreEdge<ICoreNode>> {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Removes the given link edge from the given map by using the given key.<br>
   * <br>
   * If the link set is empty after removal, the key is removed from the map
   * too.
   *
   * @param link        The link edge to remove
   * @param keyNode     The key of the set where the link is to be removed from
   * @param nodeToLinks The map that connects nodes to a set of links
   * @return <code>True</code> if the link was found and thus removed,
   *         <code>false</code> otherwise
   */
  private static boolean removeLinkFromMap(final LinkEdge<ICoreNode> link, final ICoreNode keyNode,
      final Map<ICoreNode, Set<LinkEdge<ICoreNode>>> nodeToLinks) {
    final Set<LinkEdge<ICoreNode>> links = nodeToLinks.get(keyNode);
    if (links != null) {
      final boolean wasRemoved = links.remove(link);
      if (links.isEmpty()) {
        nodeToLinks.remove(keyNode);
      }
      return wasRemoved;
    }
    return false;
  }

  /**
   * The amount of link edges in this graph.
   */
  private int mAmountOfLinkEdges;

  /**
   * Whether or not the graph is currently reversed.
   */
  private boolean mIsReversed;
  /**
   * A map connecting nodes to their incoming link edges.
   */
  private final Map<ICoreNode, Set<LinkEdge<ICoreNode>>> mNodeToIncomingLinks;
  /**
   * A map connecting nodes to their outgoing link edges.
   */
  private final Map<ICoreNode, Set<LinkEdge<ICoreNode>>> mNodeToOutgoingLinks;
  /**
   * The road graph linked by this graph.
   */
  private final RoadGraph<ICoreNode, ICoreEdge<ICoreNode>> mRoadGraph;
  /**
   * The transit graph linked by this graph.
   */
  private final TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> mTransitGraph;

  /**
   * Creates a new link graph which links the given road and transit graph.<br>
   * <br>
   * Use {@link #initializeHubConnections(Map)} after creation to determine how
   * the graphs are to be linked.
   *
   * @param roadGraph    The road graph to link
   * @param transitGraph The transit graph to link
   */
  public LinkGraph(final RoadGraph<ICoreNode, ICoreEdge<ICoreNode>> roadGraph,
      final TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> transitGraph) {
    mRoadGraph = roadGraph;
    mTransitGraph = transitGraph;
    mNodeToIncomingLinks = new HashMap<>();
    mNodeToOutgoingLinks = new HashMap<>();
  }

  @Override
  public boolean addEdge(final ICoreEdge<ICoreNode> edge) {
    if (edge instanceof RoadEdge) {
      return mRoadGraph.addEdge(edge);
    } else if (edge instanceof TransitEdge) {
      return mTransitGraph.addEdge(edge);
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public boolean addNode(final ICoreNode node) {
    if (node instanceof RoadNode) {
      return mRoadGraph.addNode(node);
    } else if (node instanceof TransitNode) {
      return mTransitGraph.addNode(node);
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public boolean containsEdge(final ICoreEdge<ICoreNode> edge) {
    if (edge instanceof RoadEdge) {
      return mRoadGraph.containsEdge(edge);
    } else if (edge instanceof TransitEdge) {
      return mTransitGraph.containsEdge(edge);
    } else if (edge instanceof LinkEdge) {
      final Set<LinkEdge<ICoreNode>> outgoingLinkEdges = getNodeToOutgoingLinks().get(edge.getSource());
      return outgoingLinkEdges != null && outgoingLinkEdges.contains(edge);
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public boolean containsNodeWithId(final int id) {
    return mRoadGraph.containsNodeWithId(id);
  }

  @Override
  public int getAmountOfEdges() {
    return mRoadGraph.getAmountOfEdges() + mTransitGraph.getAmountOfEdges() + mAmountOfLinkEdges;
  }

  @Override
  public Stream<ICoreEdge<ICoreNode>> getEdges() {
    return Stream.concat(mRoadGraph.getEdges(), mTransitGraph.getEdges());
  }

  @Override
  public Stream<ICoreEdge<ICoreNode>> getIncomingEdges(final ICoreNode destination) {
    Stream<ICoreEdge<ICoreNode>> incomingEdges;
    if (destination instanceof RoadNode) {
      incomingEdges = mRoadGraph.getIncomingEdges(destination);
    } else if (destination instanceof TransitNode) {
      incomingEdges = mTransitGraph.getIncomingEdges(destination);
    } else {
      throw new IllegalArgumentException();
    }
    final Set<LinkEdge<ICoreNode>> incomingLinkEdges = getNodeToIncomingLinks().get(destination);
    if (incomingLinkEdges == null) {
      return incomingEdges;
    }
    return Stream.concat(incomingEdges, incomingLinkEdges.stream());
  }

  @Override
  public Optional<ICoreNode> getNodeById(final int id) {
    return mRoadGraph.getNodeById(id);
  }

  /**
   * Gets a collection of all nodes that the graph contains.<br>
   * <br>
   * The collection is backed by the graph, changes will be reflected in the
   * graph. Do only change the collection directly if you know the consequences.
   * Else the graph can easily get into a corrupted state. In many situations it
   * is best to use the given methods like {@link #addNode(ICoreNode)} instead.
   */
  @Override
  public Collection<ICoreNode> getNodes() {
    return new DoubletonCollection<>(mRoadGraph.getNodes(), mTransitGraph.getNodes());
  }

  @Override
  public Stream<ICoreEdge<ICoreNode>> getOutgoingEdges(final ICoreNode source) {
    Stream<ICoreEdge<ICoreNode>> outgoingEdges;
    if (source instanceof RoadNode) {
      outgoingEdges = mRoadGraph.getOutgoingEdges(source);
    } else if (source instanceof TransitNode) {
      outgoingEdges = mTransitGraph.getOutgoingEdges(source);
    } else {
      throw new IllegalArgumentException();
    }
    final Set<LinkEdge<ICoreNode>> putgoingLinkEdges = getNodeToOutgoingLinks().get(source);
    if (putgoingLinkEdges == null) {
      return outgoingEdges;
    }
    return Stream.concat(outgoingEdges, putgoingLinkEdges.stream());
  }

  /**
   * Gets the road graph linked by this graph.
   *
   * @return The road graph to get
   */
  public RoadGraph<ICoreNode, ICoreEdge<ICoreNode>> getRoadGraph() {
    return mRoadGraph;
  }

  /**
   * Gets a human readable string that contains size information of the graph,
   * i.e. the amount of nodes and edges.
   *
   * @return A human readable string containing size information
   */
  public String getSizeInformation() {
    return toString();
  }

  /**
   * Gets the transit graph linked by this graph.
   *
   * @return The transit graph to get
   */
  public TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> getTransitGraph() {
    return mTransitGraph;
  }

  /**
   * Initializes the hub connections of this graph. That is, it links hub nodes
   * of the road graph to a list of nodes of the transit graph.<br>
   * <br>
   * Each hub node will be connected by a link edge to each transit node of the
   * mapped transit stop, in both directions.
   *
   * @param hubConnections A map connecting hub nodes of the road graph to a
   *                       list of nodes of the transit graph
   */
  public void initializeHubConnections(final Map<ICoreNode, TransitStop<ICoreNode>> hubConnections) {
    // Create edges for all hub nodes from one to the other graph
    hubConnections.forEach((roadNode, transitNodes) -> {
      transitNodes.getArrivalNodes().stream().map(NodeTime::getNode).forEach(transitNode -> {
        final LinkEdge<ICoreNode> roadToTransitLink = new LinkEdge<>(roadNode, transitNode);
        final LinkEdge<ICoreNode> transitToRoadLink = new LinkEdge<>(transitNode, roadNode);
        roadToTransitLink.setReversedProvider(this);
        transitToRoadLink.setReversedProvider(this);
        mAmountOfLinkEdges += 2;

        mNodeToOutgoingLinks.computeIfAbsent(roadNode, k -> new HashSet<>()).add(roadToTransitLink);
        mNodeToOutgoingLinks.computeIfAbsent(transitNode, k -> new HashSet<>()).add(transitToRoadLink);
        mNodeToIncomingLinks.computeIfAbsent(transitNode, k -> new HashSet<>()).add(roadToTransitLink);
        mNodeToIncomingLinks.computeIfAbsent(roadNode, k -> new HashSet<>()).add(transitToRoadLink);
      });
    });
  }

  @Override
  public boolean isReversed() {
    return mIsReversed;
  }

  @Override
  public boolean removeEdge(final ICoreEdge<ICoreNode> edge) {
    if (edge instanceof RoadEdge) {
      return mRoadGraph.removeEdge(edge);
    } else if (edge instanceof TransitEdge) {
      return mTransitGraph.removeEdge(edge);
    } else if (edge instanceof LinkEdge) {
      boolean wasRemoved =
          LinkGraph.removeLinkFromMap((LinkEdge<ICoreNode>) edge, edge.getSource(), getNodeToOutgoingLinks());
      wasRemoved |=
          LinkGraph.removeLinkFromMap((LinkEdge<ICoreNode>) edge, edge.getDestination(), getNodeToIncomingLinks());
      if (wasRemoved) {
        mAmountOfLinkEdges--;
      }
      return wasRemoved;
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public boolean removeNode(final ICoreNode node) {
    if (node instanceof RoadNode) {
      return mRoadGraph.removeNode(node);
    } else if (node instanceof TransitNode) {
      return mTransitGraph.removeNode(node);
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public void reverse() {
    mIsReversed = !mIsReversed;
    mRoadGraph.reverse();
    mTransitGraph.reverse();
  }

  @Override
  public int size() {
    return mRoadGraph.size() + mTransitGraph.size();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringJoiner sj = new StringJoiner(", ", getClass().getSimpleName() + "[", "]");
    sj.add("nodes=" + size());
    sj.add("edges=" + getAmountOfEdges());
    return sj.toString();
  }

  /**
   * Gets a map connecting nodes to their incoming link edges.<br>
   * <br>
   * The map is backed by the graph, changes to it are reflected to the graph.
   *
   * @return A backed map connecting nodes to their incoming link edges
   */
  private Map<ICoreNode, Set<LinkEdge<ICoreNode>>> getNodeToIncomingLinks() {
    if (mIsReversed) {
      return mNodeToOutgoingLinks;
    }
    return mNodeToIncomingLinks;
  }

  /**
   * Gets a map connecting nodes to their outgoing link edges.<br>
   * <br>
   * The map is backed by the graph, changes to it are reflected to the graph.
   *
   * @return A backed map connecting nodes to their outgoing link edges
   */
  private Map<ICoreNode, Set<LinkEdge<ICoreNode>>> getNodeToOutgoingLinks() {
    if (mIsReversed) {
      return mNodeToIncomingLinks;
    }
    return mNodeToOutgoingLinks;
  }

}
