package de.tischner.cobweb.routing.algorithms.metrics.landmark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

import de.tischner.cobweb.routing.model.graph.IEdge;
import de.tischner.cobweb.routing.model.graph.IGraph;
import de.tischner.cobweb.routing.model.graph.INode;

public final class RandomLandmarks<N extends INode, G extends IGraph<N, ? extends IEdge<N>>>
    implements ILandmarkProvider<N> {

  private final G mGraph;
  private final Random mRandom;

  public RandomLandmarks(final G graph) {
    mGraph = graph;
    mRandom = new Random();
  }

  @Override
  public Collection<N> getLandmarks(final int amount) {
    int amountToUse = amount;
    if (amount > mGraph.size()) {
      amountToUse = (int) mGraph.size();
    }

    final Collection<N> landmarks = new ArrayList<>(amountToUse);
    final Collection<N> nodes = mGraph.getNodes();
    final int amountOfNodes = nodes.size();

    // If the nodes support RandomAccess, fetch them directly
    if (nodes instanceof RandomAccess && nodes instanceof List) {
      final List<N> nodesAsList = (List<N>) nodes;
      for (int i = 0; i < amountToUse; i++) {
        landmarks.add(nodesAsList.get(mRandom.nextInt(amountOfNodes)));
      }
      return landmarks;
    }

    // Pick random indices and sort them
    final int[] indices = new int[amountToUse];
    for (int i = 0; i < amountToUse; i++) {
      indices[i] = mRandom.nextInt(amountOfNodes);
    }
    Arrays.sort(indices);

    // Iterate to each index and collect the node
    // This loop is optimized and faster than a straightforward approach because it
    // has no conditional checks
    final Iterator<N> nodeIter = nodes.iterator();
    int indexBefore = 0;
    for (final int index : indices) {
      // Throw away the values
      for (int j = indexBefore; j < index; j++) {
        nodeIter.next();
      }

      // Collect the value, it is at the desired index
      landmarks.add(nodeIter.next());
      // Prepare the next round
      indexBefore = index;
    }

    return landmarks;
  }

}
