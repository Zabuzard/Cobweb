package de.tischner.cobweb.routing.algorithms.nearestneighbor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.eclipse.collections.impl.list.mutable.FastList;

import de.tischner.cobweb.routing.algorithms.metrics.IMetric;
import de.tischner.cobweb.routing.model.graph.ISpatial;

public final class CoverTree<E extends ISpatial> implements INearestNeighborComputation<E> {
  private static final double DEFAULT_BASE = 1.2;
  private static final int DEFAULT_MAX_NUM_LEVELS = 500;
  private static final int DEFAULT_MIN_NUM_LEVELS = -500;

  private static <T> List<T> createList() {
    return FastList.newList();
  }

  private final double mBase;
  private boolean mHasBounds;
  private float mMaxLat;
  private int mMaxLevel;
  private float mMaxLong;
  private int mMaxMinLevel;
  private int mMaxNumLevels = DEFAULT_MAX_NUM_LEVELS;
  private final IMetric<E> mMetric;
  private float mMinLat;
  private int mMinLevel;
  private float mMinLong;
  private int mMinNumLevels = DEFAULT_MIN_NUM_LEVELS;
  private final int[] mNumLevels;

  private Node<E> mRootNode;

  /**
   * Create a cover tree which stops increasing the minimumLevel as soon as the
   * given number of nodes is reached.
   */
  public CoverTree(final double base, final int maxMinLevel, final IMetric<E> metric) {
    mMetric = metric;
    mBase = base;
    mMaxMinLevel = maxMinLevel;
    if (maxMinLevel > 0) {
      mMaxLevel = maxMinLevel;
      mMinLevel = maxMinLevel;
    }
    mNumLevels = new int[mMaxNumLevels - mMinNumLevels];
  }

  /**
   * Create a cover tree at level zero which automatically expands above and
   * below.
   */
  public CoverTree(final IMetric<E> metric) {
    mMetric = metric;
    mMaxMinLevel = Integer.MIN_VALUE;
    mNumLevels = new int[mMaxNumLevels - mMinNumLevels];
    mBase = DEFAULT_BASE;
  }

  /**
   * Get the cover of the given level. All points at this level are guaranteed
   * to be 2^i apart from one another.
   *
   * @param level
   * @return
   */
  public List<E> getCover(final int level) {
    List<Node<E>> coverset = CoverTree.createList();
    coverset.add(mRootNode);

    for (int currentLevel = mMaxLevel; currentLevel > level; currentLevel--) {
      final List<Node<E>> nextCoverset = CoverTree.createList();
      for (final Node<E> n : coverset) {
        nextCoverset.addAll(n.getChildren());
      }
      coverset = nextCoverset;
    }

    final List<E> cover = CoverTree.createList();
    for (final Node<E> node : coverset) {
      cover.add(node.getElement());
    }

    return cover;
  }

  /**
   * Gets at least k centers which are maximally apart from each other. All
   * remaining centers are removed from the tree. This function only works as
   * designed when the function insert(point,k) has been used before to add
   * points to the tree. Otherwise, it will return the cover one level above the
   * bottom most level of the tree.
   *
   * @param number of centers
   * @return
   */
  public List<E> getKCenters(final int numCenters) {
    final List<Node<E>> coverset = removeNodes(numCenters);
    // create cover
    final List<E> cover = CoverTree.createList();
    for (final Node<E> n : coverset) {
      cover.add(n.getElement());
    }
    return cover;

  }

  @Override
  public Optional<E> getNearestNeighbor(final E point) {
    final List<Node<E>> candidates = CoverTree.createList();
    candidates.add(mRootNode);
    double minDist = distance(mRootNode, point);
    mRootNode.setDistance(minDist);
    for (int level = mMaxLevel; level > mMinLevel; level--) {
      final List<Node<E>> nextCandidates = CoverTree.createList();
      for (final Node<E> candidate : candidates) {
        for (final Node<E> child : candidate.getChildren()) {
          // do not compute distances twice
          if (!areAtSameLocation(candidate, child)) {
            child.setDistance(distance(child, point));
            // minimum distance can be recorded here
            if (child.getDistance() < minDist) {
              minDist = child.getDistance();
            }
          } else {
            child.setDistance(candidate.getDistance());
          }
          nextCandidates.add(child);
        }
      }

      candidates.clear();

      // create a set of candidate nearest neighbors
      for (final Node<E> nextCandidate : nextCandidates) {
        if (nextCandidate.getDistance() < minDist + Math.pow(mBase, level)) {
          candidates.add(nextCandidate);
        }
      }
    }

    for (final Node<E> candidate : candidates) {
      if (candidate.getDistance() == minDist) {
        return Optional.of(candidate.getElement());
      }
    }

    return Optional.empty();
  }

  /**
   * Insert a point into the tree.
   *
   * @param point
   */
  public boolean insert(final E element) {
    if (mHasBounds) {
      // points outside of the bounding box will not be added to the tree
      final float latitude = element.getLatitude();
      final float longitude = element.getLongitude();
      if (latitude > mMaxLat || latitude < mMinLat || longitude > mMaxLong || longitude < mMinLong) {
        return false;
      }
    }

    // if this is the first node make it the root node
    if (mRootNode == null) {
      mRootNode = new Node<>(null, element);
      incNodes(mMaxLevel);
      return true;
    }

    // do not add if the new node is identical to the root node
    mRootNode.setDistance(distance(mRootNode, element));
    if (mRootNode.getDistance() == 0.0) {
      return false;
    }

    // if the node lies outside the cover of the root node and its descendants
    // then insert the node above the root node
    if (mRootNode.getDistance() > Math.pow(mBase, mMaxLevel + 1)) {
      insertAtRoot(element);
      return true;
    }

    // usually insertion begins here
    List<Node<E>> coverset = CoverTree.createList();
    // the initial coverset contains only the root node
    coverset.add(mRootNode);
    int level = mMaxLevel;
    Node<E> parent = null; // the root node does not have a parent
    int parentLevel = mMaxLevel;
    while (true) {
      boolean parentFound = true;
      final List<Node<E>> candidates = CoverTree.createList();
      for (final Node<E> node : coverset) {
        for (final Node<E> child : node.getChildren()) {
          if (!areAtSameLocation(node, child)) {
            // do not compute distance twice
            child.setDistance(distance(child, element));
            // do not add if node is already contained in the tree
            if (child.getDistance() == 0.0) {
              return false;
            }
          } else {
            child.setDistance(node.getDistance());
          }

          if (child.getDistance() < Math.pow(mBase, level)) {
            candidates.add(child);
            parentFound = false;
          }
        }
      }

      // if the children of the coverset are further away the 2^level then an
      // element of the coverset is the parent of the new node
      if (parentFound) {
        break;
      }

      // select one node of the coverset as the parent of the node
      for (final Node<E> node : coverset) {
        if (node.getDistance() < Math.pow(mBase, level)) {
          parent = node;
          parentLevel = level;
          break;
        }
      }
      // set all nodes as the new coverset
      level--;
      coverset = candidates;
    }

    // if the point is a sibling of the root node, then the cover of the root
    // node is increased
    if (parent == null) {
      insertAtRoot(element);
      return true;
    }

    if (parentLevel - 1 < mMinLevel) {
      // if the maximum size is reached and this would only increase the depth
      // of the tree then stop
      if (parentLevel - 1 < mMaxMinLevel) {
        return false;
      }
      mMinLevel = parentLevel - 1;
    }

    // otherwise add child to the tree
    final Node<E> newNode = new Node<>(parent, element);
    parent.addChild(newNode);
    // record distance to parent node and add to the sorted set of nodes where
    // distance is used for sorting (needed for removal)
    incNodes(parentLevel - 1);
    return true;
  }

  /**
   * Insert a point into the tree. If the tree size is greater than k the lowest
   * cover will be removed as long as it does not decrease tree size below k.
   *
   * @param point
   */
  public boolean insert(final E element, final int level) {
    final boolean inserted = insert(element);
    // only do this if there are more than two levels
    if (mMaxLevel - mMinLevel > 2) {
      // remove lowest cover if the cover before has a sufficient number of
      // nodes
      if (size(mMinLevel + 1) >= level) {
        removeLowestCover();
        // do not accept new nodes at the minimum level
        mMaxMinLevel = mMinLevel + 1;
      }
      // remove redundant nodes from the minimum level
      if (size(mMinLevel) >= 2 * level) {
        removeNodes(level);
      }
    }
    return inserted;
  }

  /**
   * Returns the maximum level of this tree.
   *
   * @return
   */
  public int maxLevel() {
    return mMaxLevel;
  }

  /**
   * Returns the minimum level of this tree.
   *
   * @return
   */
  public int minLevel() {
    return mMinLevel;
  }

  /**
   * Points outside of the bounding box, will not be included. This allows for
   * easy truncation.
   *
   * @param min
   * @param max
   */
  public void setBounds(final float minLat, final float minLong, final float maxLat, final float maxLong) {
    mHasBounds = true;
    mMinLat = minLat;
    mMinLong = minLong;
    mMaxLat = maxLat;
    mMaxLong = maxLong;
  }

  /**
   * Set the minimum levels of the cover tree by defining the maximum exponent
   * of the base.
   */
  public void setMaxNumLevels(final int max) {
    mMaxNumLevels = max;
  }

  /**
   * Set the minimum levels of the cover tree by defining the minimum exponent
   * of the base.
   */
  public void setMinNumLevels(final int min) {
    mMinNumLevels = min;
  }

  /**
   * Returns the size of the cover tree
   *
   * @return
   */
  public int size() {
    return size(mMinLevel);
  }

  /**
   * Returns the size of the cover tree up to the given level (inclusive)
   *
   * @param level
   * @return
   */
  public int size(final int level) {
    int sum = 0;
    for (int i = mMaxLevel; i >= level; i--) {
      sum += mNumLevels[i - mMinNumLevels];
    }
    return sum;
  }

  private boolean areAtSameLocation(final E first, final E second) {
    return first.getLatitude() == second.getLatitude() && first.getLongitude() == second.getLongitude();
  }

  private boolean areAtSameLocation(final Node<E> first, final Node<E> second) {
    return areAtSameLocation(first.getElement(), second.getElement());
  }

  private void decNodes(final int level) {
    mNumLevels[level - mMinNumLevels]--;
  }

  private double distance(final E first, final E second) {
    return mMetric.distance(first, second);
  }

  private double distance(final Node<E> first, final E second) {
    return distance(first.getElement(), second);
  }

  private double distance(final Node<E> first, final Node<E> second) {
    return distance(first.getElement(), second.getElement());
  }

  private void incNodes(final int level) {
    mNumLevels[level - mMinNumLevels]++;
  }

  private void insertAtRoot(final E element) {
    // inserts the point above the root by successively increasing the cover of
    // the root node until it
    // contains the new point, the old root is added as child of the new root
    final Node<E> oldRoot = mRootNode;
    final double dist = distance(oldRoot, element);
    while (dist > Math.pow(mBase, mMaxLevel)) {
      final Node<E> nextRoot = new Node<>(null, mRootNode.getElement());
      mRootNode.setParent(nextRoot);
      nextRoot.addChild(mRootNode);
      mRootNode = nextRoot;
      decNodes(mMaxLevel);
      mMaxLevel++;
      incNodes(mMaxLevel);
    }
    final Node<E> nextNode = new Node<>(mRootNode, element);
    mRootNode.addChild(nextNode);
    incNodes(mMaxLevel - 1);
  }

  /**
   * Removes the the cover at the lowest level of the tree.
   */
  private void removeLowestCover() {
    List<Node<E>> coverset = CoverTree.createList();
    coverset.add(mRootNode);
    for (int level = mMaxLevel; level > mMinLevel + 1; level--) {
      final List<Node<E>> nextCoverset = CoverTree.createList();
      for (final Node<E> node : coverset) {
        nextCoverset.addAll(node.getChildren());
      }
      coverset = nextCoverset;
    }
    for (final Node<E> node : coverset) {
      node.removeChildren();
    }

    mMinLevel++;
  }

  /**
   * Removes all but k points.
   */
  private List<Node<E>> removeNodes(final int numCenters) {
    List<Node<E>> coverset = CoverTree.createList();
    coverset.add(mRootNode);
    for (int level = mMaxLevel; level > mMinLevel + 1; level--) {
      final List<Node<E>> nextCoverset = CoverTree.createList();
      for (final Node<E> node : coverset) {
        nextCoverset.addAll(node.getChildren());
      }
      coverset = nextCoverset;
    }

    final int missing = numCenters - coverset.size();
    if (missing < 0) {
      throw new AssertionError("Negative missing=" + missing + " in coverset");
    }

    // Successively pick the node with the largest distance to the coverset and
    // add it to the coverset
    final LinkedList<Node<E>> candidates = new LinkedList<>();
    for (final Node<E> node : coverset) {
      for (final Node<E> child : node.getChildren()) {
        if (!areAtSameLocation(node, child)) {
          candidates.add(child);
        }
      }
    }

    // only add candidates when the coverset is yet smaller then the number of
    // desired centers
    if (coverset.size() < numCenters) {
      // compute the distance of all candidates to their parents and uncles
      for (final Node<E> node : candidates) {
        double minDist = Double.POSITIVE_INFINITY;
        for (final Node<E> uncle : node.getParent().getParent().getChildren()) {
          final double dist = distance(node, uncle);
          if (dist < minDist) {
            minDist = dist;
          }
        }
        node.setDistance(minDist);
        if (minDist == Double.POSITIVE_INFINITY) {
          throw new AssertionError("Infinite distance in k centers computation");
        }
      }

      do {
        Collections.sort(candidates);
        final Node<E> nextNode = candidates.removeLast();
        coverset.add(nextNode);
        // update the distance of all candidates in the neighborhood of
        // the new node
        for (final Node<E> uncle : nextNode.getParent().getParent().getChildren()) {
          if (uncle != nextNode) {
            final double dist = distance(nextNode, uncle);
            if (dist < nextNode.getDistance()) {
              nextNode.setDistance(dist);
            }
          }
        }
      } while (coverset.size() < numCenters);
    }

    // finally remove all nodes that have not been selected from the tree to
    // avoid confusing the nearest neighbor computation
    for (final Node<E> node : candidates) {
      node.getParent().removeChild(node);
      decNodes(mMinLevel);
    }

    return coverset;
  }

}