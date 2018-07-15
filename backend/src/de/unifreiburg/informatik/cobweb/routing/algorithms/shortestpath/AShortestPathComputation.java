package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import de.unifreiburg.informatik.cobweb.routing.model.graph.IEdge;
import de.unifreiburg.informatik.cobweb.routing.model.graph.INode;
import de.unifreiburg.informatik.cobweb.routing.model.graph.IPath;

/**
 * Abstract class for implementations of {@link IShortestPathComputation}.
 * Implements some of the overloaded methods by using the core variant of the
 * corresponding method.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node
 * @param <E> Type of the edge
 */
public abstract class AShortestPathComputation<N extends INode, E extends IEdge<N>>
    implements IShortestPathComputation<N, E> {

  /*
   * (non-Javadoc)
   * @see de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.
   * IShortestPathComputation#
   * computeSearchSpace(de.unifreiburg.informatik.cobweb.routing.model.graph.
   * INode, de.unifreiburg.informatik.cobweb.routing.model.graph.INode)
   */
  @Override
  public Collection<N> computeSearchSpace(final N source, final N destination) {
    return computeSearchSpace(Collections.singletonList(source), destination);
  }

  /*
   * (non-Javadoc)
   * @see de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.
   * IShortestPathComputation#
   * computeShortestPath(de.unifreiburg.informatik.cobweb.routing.model.graph.
   * INode, de.unifreiburg.informatik.cobweb.routing.model.graph.INode)
   */
  @Override
  public Optional<IPath<N, E>> computeShortestPath(final N source, final N destination) {
    return computeShortestPath(Collections.singletonList(source), destination);
  }

  /*
   * (non-Javadoc)
   * @see de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.
   * IShortestPathComputation#
   * computeShortestPathCost(de.unifreiburg.informatik.cobweb.routing.model.
   * graph.INode, de.unifreiburg.informatik.cobweb.routing.model.graph.INode)
   */
  @Override
  public Optional<Double> computeShortestPathCost(final N source, final N destination) {
    return computeShortestPathCost(Collections.singletonList(source), destination);
  }

  /*
   * (non-Javadoc)
   * @see de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.
   * IShortestPathComputation#
   * computeShortestPathCostsReachable(de.unifreiburg.informatik.cobweb.routing
   * .model.graph. INode)
   */
  @Override
  public Map<N, ? extends IHasPathCost> computeShortestPathCostsReachable(final N source) {
    return computeShortestPathCostsReachable(Collections.singletonList(source));
  }

}
