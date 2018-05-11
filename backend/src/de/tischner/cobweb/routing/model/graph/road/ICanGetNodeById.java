package de.tischner.cobweb.routing.model.graph.road;

import java.util.Optional;

import de.tischner.cobweb.routing.model.graph.INode;

/**
 * Interface for classes that provide access to nodes by their unique ID.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <N> Type of the node which must has an ID
 */
public interface ICanGetNodeById<N extends INode & IHasId> {
  /**
   * Whether or not there is a node with the given ID.<br>
   * <br>
   * The implementation must be consistent with {@link #getNodeById(long)}.
   *
   * @param id The ID in question
   * @return <tt>True</tt> if there is a node with the given ID, <tt>false</tt>
   *         otherwise
   */
  boolean containsNodeWithId(long id);

  /**
   * Gets the node with the given ID if present.<br>
   * <br>
   * The implementation must be consistent with
   * {@link #containsNodeWithId(long)}.
   *
   * @param id The ID to get the node for
   * @return The node with the given ID if present, else empty. If there are
   *         multiple nodes with that ID, an arbitrary node is returned
   */
  Optional<N> getNodeById(long id);
}
