package de.tischner.cobweb.routing.model.graph.link;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Stream;

import de.tischner.cobweb.routing.model.graph.ICoreEdge;
import de.tischner.cobweb.routing.model.graph.ICoreNode;
import de.tischner.cobweb.routing.model.graph.IGetNodeById;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.IReversedProvider;
import de.tischner.cobweb.routing.model.graph.road.RoadEdge;
import de.tischner.cobweb.routing.model.graph.road.RoadGraph;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;
import de.tischner.cobweb.routing.model.graph.transit.NodeTime;
import de.tischner.cobweb.routing.model.graph.transit.TransitEdge;
import de.tischner.cobweb.routing.model.graph.transit.TransitGraph;
import de.tischner.cobweb.routing.model.graph.transit.TransitNode;
import de.tischner.cobweb.routing.model.graph.transit.TransitStop;
import de.tischner.cobweb.util.collections.DoubletonCollection;

public final class LinkGraph
    implements IGetNodeById<ICoreNode>, IReversedProvider, IGraph<ICoreNode, ICoreEdge<ICoreNode>> {
  /**
   * Removes the given link edge from the given map by using the given key.<br>
   * <br>
   * If the link set is empty after removal, the key is removed from the map
   * too.
   *
   * @param link        The link edge to remove
   * @param keyNode     The key of the set where the link is to be removed from
   * @param nodeToLinks The map that connects nodes to a set of links
   * @return <tt>True</tt> if the link was found and thus removed,
   *         <tt>false</tt> otherwise
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

  private final Map<ICoreNode, Set<LinkEdge<ICoreNode>>> mNodeToIncomingLinks;
  private final Map<ICoreNode, Set<LinkEdge<ICoreNode>>> mNodeToOutgoingLinks;
  private final RoadGraph<ICoreNode, ICoreEdge<ICoreNode>> mRoadGraph;
  private final TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> mTransitGraph;

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

  public TransitGraph<ICoreNode, ICoreEdge<ICoreNode>> getTransitGraph() {
    return mTransitGraph;
  }

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

  private Map<ICoreNode, Set<LinkEdge<ICoreNode>>> getNodeToIncomingLinks() {
    if (mIsReversed) {
      return mNodeToOutgoingLinks;
    }
    return mNodeToIncomingLinks;
  }

  private Map<ICoreNode, Set<LinkEdge<ICoreNode>>> getNodeToOutgoingLinks() {
    if (mIsReversed) {
      return mNodeToIncomingLinks;
    }
    return mNodeToOutgoingLinks;
  }

}
