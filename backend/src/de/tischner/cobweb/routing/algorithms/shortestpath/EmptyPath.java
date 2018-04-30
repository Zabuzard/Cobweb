package de.tischner.cobweb.routing.algorithms.shortestpath;

import java.util.Collections;
import java.util.Iterator;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.INode;
import de.tischner.cobweb.routing.model.graph.IPath;

public final class EmptyPath<N extends INode, E extends IEdge<N>> implements IPath<N, E> {

  private final N mNode;

  public EmptyPath(final N node) {
    mNode = node;
  }

  @Override
  public N getDestination() {
    return mNode;
  }

  @Override
  public N getSource() {
    return mNode;
  }

  @Override
  public double getTotalCost() {
    return 0.0;
  }

  @Override
  public Iterator<E> iterator() {
    return Collections.emptyListIterator();
  }

  @Override
  public int length() {
    return 0;
  }

}
