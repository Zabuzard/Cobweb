package de.tischner.cobweb.routing.algorithms.nearestneighbor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.eclipse.collections.impl.list.mutable.FastList;

import de.tischner.cobweb.routing.algorithms.metrics.IMetric;
import de.tischner.cobweb.routing.model.graph.ISpatial;

/**
 * Implementation of a Cover-Tree (see
 * <a href="https://en.wikipedia.org/wiki/Cover_tree">Wikipedia</a>) which
 * solves nearest neighbor computation queries.<br>
 * <br>
 * The implementation is based on the paper:
 * <ul>
 * <li><a href="https://dl.acm.org/citation.cfm?id=1143857">Cover Trees for
 * Nearest Neighbor</a> - Beygelzimer et al. in <tt>ICML '06</tt></li>
 * </ul>
 * Modified version from
 * <a href="https://github.com/loehndorf/covertree">GitHub: Loehndorf -
 * CoverTree</a>.
 *
 * @author Nils Loehndorf
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <E> Type of the objects contained in the tree which must offer spatial
 *        data
 */
public final class CoverTree<E extends ISpatial> implements INearestNeighborComputation<E> {
  /**
   * The default base to use by the tree.
   */
  private static final double DEFAULT_BASE = 1.2;
  /**
   * The default maximal numbers of levels.
   */
  private static final int DEFAULT_MAX_NUM_LEVELS = 500;
  /**
   * The default minimum number of levels.
   */
  private static final int DEFAULT_MIN_NUM_LEVELS = -500;

  /**
   * Utility method to create a new list instance. Can be used to exchange the
   * list type used by the tree.
   *
   * @param <T> Type of the elements contained in the list
   * @return The created list instance
   */
  private static <T> List<T> createList() {
    return FastList.newList();
  }

  /**
   * The base of the tree.
   */
  private final double mBase;
  /**
   * If the tree uses bounds. If set to <tt>true</tt> the fields
   * {@link #mMinLat}, {@link #mMinLong}, {@link #mMaxLat} and {@link #mMaxLong}
   * are respected.
   */
  private boolean mHasBounds;
  /**
   * The maximal latitude, only effective if {@link #mHasBounds} is set to
   * <tt>true</tt>.
   */
  private float mMaxLat;
  /**
   * The current maximal level of the tree.
   */
  private int mMaxLevel;
  /**
   * The maximal longitude, only effective if {@link #mHasBounds} is set to
   * <tt>true</tt>.
   */
  private float mMaxLong;
  /**
   * The maximal minimum level of the tree.
   */
  private int mMaxMinLevel;
  /**
   * The maximal amount of levels of the tree.
   */
  private int mMaxNumLevels = DEFAULT_MAX_NUM_LEVELS;
  /**
   * The metric to use for determining distance between elements.
   */
  private final IMetric<E> mMetric;
  /**
   * The minimal latitude, only effective if {@link #mHasBounds} is set to
   * <tt>true</tt>.
   */
  private float mMinLat;
  /**
   * The current minimal level of the tree.
   */
  private int mMinLevel;
  /**
   * The minimal longitude, only effective if {@link #mHasBounds} is set to
   * <tt>true</tt>.
   */
  private float mMinLong;
  /**
   * The minimum number of levels of the tree.
   */
  private int mMinNumLevels = DEFAULT_MIN_NUM_LEVELS;
  /**
   * The current number of levels of the tree.
   */
  private final int[] mNumLevels;
  /**
   * The root node.
   */
  private Node<E> mRootNode;

  /**
   * Create an initially empty cover tree which stops increasing the minimum
   * level as soon as the given number of nodes is reached.
   *
   * @param base        The base of the tree
   * @param maxMinLevel The maximal minimum level of the tree
   * @param metric      The metric to use for determining distance between
   *                    elements
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
   * Create an initially empty cover tree at level <tt>0</tt> which
   * automatically expands above and below.
   *
   * @param metric The metric to use for determining distance between elements
   */
  public CoverTree(final IMetric<E> metric) {
    mMetric = metric;
    mMaxMinLevel = Integer.MIN_VALUE;
    mNumLevels = new int[mMaxNumLevels - mMinNumLevels];
    mBase = DEFAULT_BASE;
  }

  /**
   * Get the cover of the given level. All points at this level are guaranteed
   * to be <tt>2^level</tt> apart from one another.
   *
   * @param level The level to get the cover of
   * @return The cover at the given level
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
   * Gets at least <tt>numCenters</tt> centers which are maximally apart from
   * each other. All remaining centers are removed from the tree.<br>
   * <br>
   * This function only works as designed when the function
   * {@link #insert(ISpatial, int)} has been used before to add elements to the
   * tree. Otherwise, it will return the cover one level above the bottom most
   * level of the tree.
   *
   * @param numCenters The number of centers to get
   * @return At least <tt>numCenters</tt> centers which are maximally apart from
   *         each other
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
          // Do not compute distances twice
          if (!areAtSameLocation(candidate, child)) {
            child.setDistance(distance(child, point));
            // The minimum distance can be recorded here
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

      // Create a set of nearest neighbor candidates
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
   * Insert the given element into the tree.
   *
   * @param element The element to insert
   * @return If the element was inserted
   */
  public boolean insert(final E element) {
    if (mHasBounds) {
      // Elements outside of the bounding box will not be added to the tree
      final float latitude = element.getLatitude();
      final float longitude = element.getLongitude();
      if (latitude > mMaxLat || latitude < mMinLat || longitude > mMaxLong || longitude < mMinLong) {
        return false;
      }
    }

    // If this is the first node make it the root node
    if (mRootNode == null) {
      mRootNode = new Node<>(null, element);
      incNodes(mMaxLevel);
      return true;
    }

    // Do not add if the new node is identical to the root node
    mRootNode.setDistance(distance(mRootNode, element));
    if (mRootNode.getDistance() == 0.0) {
      return false;
    }

    // If the node lies outside the cover of the root node and its descendants
    // then insert the node above the root node
    if (mRootNode.getDistance() > Math.pow(mBase, mMaxLevel + 1)) {
      insertAtRoot(element);
      return true;
    }

    // Usually insertion begins here
    List<Node<E>> coverset = CoverTree.createList();
    // The initial cover-set contains only the root node
    coverset.add(mRootNode);
    int level = mMaxLevel;
    // The root node does not have a parent
    Node<E> parent = null;
    int parentLevel = mMaxLevel;
    while (true) {
      boolean parentFound = true;
      final List<Node<E>> candidates = CoverTree.createList();
      for (final Node<E> node : coverset) {
        for (final Node<E> child : node.getChildren()) {
          if (!areAtSameLocation(node, child)) {
            // Do not compute distance twice
            child.setDistance(distance(child, element));
            // Do not add if node is already contained in the tree
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

      // If the children of the cover-set are further away the 2^level then an
      // element of the cover-set is the parent of the new node
      if (parentFound) {
        break;
      }

      // Select one node of the cover-set as the parent of the node
      for (final Node<E> node : coverset) {
        if (node.getDistance() < Math.pow(mBase, level)) {
          parent = node;
          parentLevel = level;
          break;
        }
      }
      // Set all nodes as the new cover-set
      level--;
      coverset = candidates;
    }

    // If the point is a sibling of the root node, then the cover of the root
    // node is increased
    if (parent == null) {
      insertAtRoot(element);
      return true;
    }

    if (parentLevel - 1 < mMinLevel) {
      // If the maximum size is reached and this would only increase the depth
      // of the tree then stop
      if (parentLevel - 1 < mMaxMinLevel) {
        return false;
      }
      mMinLevel = parentLevel - 1;
    }

    // Otherwise add child to the tree
    final Node<E> newNode = new Node<>(parent, element);
    parent.addChild(newNode);
    // Record distance to parent node and add to the sorted set of nodes where
    // distance is used for sorting (needed for removal)
    incNodes(parentLevel - 1);
    return true;
  }

  /**
   * Insert the given element into the tree.<br>
   * <br>
   * If the tree size is greater than <tt>level</tt> the lowest cover will be
   * removed as long as it does not decrease tree size below <tt>level</tt>.
   *
   * @param element The element to insert
   * @param level   The level
   * @return If the element was added
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
   * @return The maximum level
   */
  public int maxLevel() {
    return mMaxLevel;
  }

  /**
   * Returns the minimum level of this tree.
   *
   * @return The minimum level
   */
  public int minLevel() {
    return mMinLevel;
  }

  /**
   * Sets bounds for the tree.<br>
   * <br>
   * Elements outside of the bounding box, will not be included. This allows for
   * easy truncation.
   *
   * @param minLat  The minimum latitude
   * @param minLong The minimum longitude
   * @param maxLat  The maximal latitude
   * @param maxLong The maximal longitude
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
   *
   * @param max The maximum exponent to set
   */
  public void setMaxNumLevels(final int max) {
    mMaxNumLevels = max;
  }

  /**
   * Set the minimum levels of the cover tree by defining the minimum exponent
   * of the base.
   *
   * @param min The minimum exponent to set
   */
  public void setMinNumLevels(final int min) {
    mMinNumLevels = min;
  }

  /**
   * Returns the size of the cover tree, i.e. the amount of elements contained.
   *
   * @return The size of the tree
   */
  public int size() {
    return size(mMinLevel);
  }

  /**
   * Returns the size of the cover tree up to the given level (inclusive).
   *
   * @param level The level to get the size to
   * @return The size of the tree up to the given level (inclusive)
   */
  public int size(final int level) {
    int sum = 0;
    for (int i = mMaxLevel; i >= level; i--) {
      sum += mNumLevels[i - mMinNumLevels];
    }
    return sum;
  }

  /**
   * Returns whether two elements are at the same location.
   *
   * @param first  The first element
   * @param second The second element
   * @return <tt>True/<tt> if both elements are at the same location, <tt>false</tt>
   *         otherwise
   */
  private boolean areAtSameLocation(final E first, final E second) {
    return first.getLatitude() == second.getLatitude() && first.getLongitude() == second.getLongitude();
  }

  /**
   * Returns whether the elements contained in the two given nodes are at the
   * same location.
   *
   * @param first  The node containing the first element
   * @param second The node containing the first element
   * @return <tt>True/<tt> if both elements are at the same location, <tt>false</tt>
   *         otherwise
   */
  private boolean areAtSameLocation(final Node<E> first, final Node<E> second) {
    return areAtSameLocation(first.getElement(), second.getElement());
  }

  /**
   * Decreases the number of nodes at the given level.
   *
   * @param level The level to decrease nodes at
   */
  private void decNodes(final int level) {
    mNumLevels[level - mMinNumLevels]--;
  }

  /**
   * Computes the distance between the given elements using the set metric.
   *
   * @param first  The first element
   * @param second The second element
   * @return The distance between the given elements according to the set metric
   */
  private double distance(final E first, final E second) {
    return mMetric.distance(first, second);
  }

  /**
   * Computes the distance between the given elements using the set metric.
   *
   * @param first  The node containing the first element
   * @param second The second element
   * @return The distance between the given elements according to the set metric
   */
  private double distance(final Node<E> first, final E second) {
    return distance(first.getElement(), second);
  }

  /**
   * Computes the distance between the elements contained in the given nodes
   * using the set metric.
   *
   * @param first  The node containing the first element
   * @param second The node copntaining the second element
   * @return The distance between the given elements according to the set metric
   */
  private double distance(final Node<E> first, final Node<E> second) {
    return distance(first.getElement(), second.getElement());
  }

  /**
   * Increases the number of nodes at the given level.
   *
   * @param level The level to increase nodes at
   */
  private void incNodes(final int level) {
    mNumLevels[level - mMinNumLevels]++;
  }

  /**
   * Inserts the given element at the root node.
   *
   * @param element The element to insert
   */
  private void insertAtRoot(final E element) {
    // Inserts the point above the root by successively increasing the cover of
    // the root node until it contains the new point, the old root is added as
    // child of the new root
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
   * Removes all but <tt>numCenters</tt> elements.
   *
   * @param numCenters The amount of elements to keep
   * @return The cover-set
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

    // Successively pick the node with the largest distance to the cover-set and
    // add it to the cover-set
    final LinkedList<Node<E>> candidates = new LinkedList<>();
    for (final Node<E> node : coverset) {
      for (final Node<E> child : node.getChildren()) {
        if (!areAtSameLocation(node, child)) {
          candidates.add(child);
        }
      }
    }

    // Only add candidates when the cover-set is yet smaller then the number of
    // desired centers
    if (coverset.size() < numCenters) {
      // Compute the distance of all candidates to their parents and uncles
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
        // Update the distance of all candidates in the neighborhood of
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

    // Finally remove all nodes that have not been selected from the tree to
    // avoid confusing the nearest neighbor computation
    for (final Node<E> node : candidates) {
      node.getParent().removeChild(node);
      decNodes(mMinLevel);
    }

    return coverset;
  }

}