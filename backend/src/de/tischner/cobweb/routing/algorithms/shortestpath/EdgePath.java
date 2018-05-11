package de.tischner.cobweb.routing.algorithms.shortestpath;

import java.util.ArrayList;
import java.util.Iterator;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.IPath;
import de.tischner.cobweb.util.ReverseIterator;

/**
 * Implementation of a {@link IPath} which connects edges.<br>
 * <br>
 * Does not support empty paths, i.e. paths without any edges, use
 * {@link EmptyPath} instead. It can be build reversely without additional
 * overhead when iterating.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 */
public final class EdgePath<N extends INode, E extends IEdge<N>> implements IPath<N, E> {
  /**
   * Whether or not the path is build reversely.
   */
  private final boolean mBuildReversely;
  /**
   * The edges this path consists of.
   */
  private final ArrayList<E> mEdges;
  /**
   * The total cost of the path, i.e. the sum of all edges cost.
   */
  private double mTotalCost;

  /**
   * Creates a new initially empty edge path that is not built reversely.
   */
  public EdgePath() {
    this(false);
  }

  /**
   * Creates a new initially empty edge path that can be build reversely
   *
   * @param buildReversely Whether or not the path is build reversely. If
   *                       <tt>true</tt> calls to {@link #addEdge(IEdge)} are
   *                       interpreted to start from the end of the path. So the
   *                       destination of the first added edge is the
   *                       destination of the path and the source of the last
   *                       added edge is the source of the path.
   */
  public EdgePath(final boolean buildReversely) {
    mBuildReversely = buildReversely;
    mEdges = new ArrayList<>();
  }

  /**
   * Adds the given edge to this path.<br>
   * <br>
   * If the path is to be build reversely the edges are interpreted to start
   * from the end of the path. So the destination of the first added edge is the
   * destination of the path and the source of the last added edge is the source
   * of the path.
   *
   * @param edge The edge to add
   */
  public void addEdge(final E edge) {
    mEdges.add(edge);
    mTotalCost += edge.getCost();
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.IPath#getDestination()
   */
  @Override
  public N getDestination() {
    int destinationIndex;
    if (mBuildReversely) {
      // Destination is the first entry
      destinationIndex = 0;
    } else {
      // Destination is the last entry
      destinationIndex = mEdges.size() - 1;
    }
    return mEdges.get(destinationIndex).getDestination();
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.IPath#getSource()
   */
  @Override
  public N getSource() {
    int sourceIndex;
    if (mBuildReversely) {
      // Source is the last entry
      sourceIndex = mEdges.size() - 1;
    } else {
      // Source is the first entry
      sourceIndex = 0;
    }
    return mEdges.get(sourceIndex).getSource();
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.IPath#getTotalCost()
   */
  @Override
  public double getTotalCost() {
    return mTotalCost;
  }

  /**
   * Whether or not the path is build reversely.
   *
   * @return <tt>True</tt> if the path is build reversely, <tt>false</tt> if not
   */
  public boolean isBuildReversely() {
    return mBuildReversely;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<E> iterator() {
    if (mBuildReversely) {
      return new ReverseIterator<>(mEdges);
    }
    return mEdges.iterator();
  }

  /*
   * (non-Javadoc)
   * @see de.tischner.cobweb.routing.model.graph.IPath#length()
   */
  @Override
  public int length() {
    return mEdges.size();
  }
}
