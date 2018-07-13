package de.tischner.cobweb.routing.algorithms.shortestpath;

/**
 * POJO that contains cost for a path.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class PathCost implements IHasPathCost {
  /**
   * The cost of the path.
   */
  private final double mCost;

  /**
   * Creates a new instance with the given cost.
   *
   * @param cost The cost of the path
   */
  public PathCost(final double cost) {
    mCost = cost;
  }

  @Override
  public double getPathCost() {
    return mCost;
  }

}
