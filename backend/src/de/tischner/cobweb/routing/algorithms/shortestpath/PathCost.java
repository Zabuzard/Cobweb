package de.tischner.cobweb.routing.algorithms.shortestpath;

public final class PathCost implements IHasPathCost {

  private final double mCost;

  public PathCost(final double cost) {
    mCost = cost;
  }

  @Override
  public double getPathCost() {
    return mCost;
  }

}
