package de.tischner.cobweb.routing.algorithms.shortestpath;

import java.util.ArrayList;
import java.util.Iterator;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.IPath;
import de.tischner.cobweb.util.ReverseIterator;

public final class EdgePath<N extends INode, E extends IEdge<N>> implements IPath<N, E> {
  private final boolean mBuildReversely;
  private final ArrayList<E> mEdges;
  private double mTotalCost;

  public EdgePath() {
    this(false);
  }

  public EdgePath(final boolean buildReversely) {
    mBuildReversely = buildReversely;
    mEdges = new ArrayList<>();
  }

  public void addEdge(final E edge) {
    mEdges.add(edge);
    mTotalCost += edge.getCost();
  }

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

  @Override
  public double getTotalCost() {
    return mTotalCost;
  }

  public boolean isBuildReversely() {
    return mBuildReversely;
  }

  @Override
  public Iterator<E> iterator() {
    if (mBuildReversely) {
      return new ReverseIterator<>(mEdges);
    }
    return mEdges.iterator();
  }

  @Override
  public int length() {
    return mEdges.size();
  }
}
