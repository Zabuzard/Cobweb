package de.unifreiburg.informatik.cobweb.parsing.gtfs;

import java.io.IOException;

/**
 * GTFS handler which forwards GTFS entities to a collection of given
 * handler.<br>
 * <br>
 * This is needed since the GTFS API does not support adding multiple handlers.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class GtfsHandlerForwarder implements IBaseEntityHandler {
  /**
   * The handler to forward GTFS entities to.
   */
  private final Iterable<? extends IBaseEntityHandler> mAllHandler;

  /**
   * Creates a new GTFS handler which forwards all GTFS entities to the given
   * handler.
   *
   * @param allHandler The handler to forward entities to
   */
  public GtfsHandlerForwarder(final Iterable<? extends IBaseEntityHandler> allHandler) {
    mAllHandler = allHandler;
  }

  /**
   * Forwards the call to all given handler.
   */
  @Override
  public void complete() throws IOException {
    for (final IBaseEntityHandler handler : mAllHandler) {
      handler.complete();
    }
  }

  /**
   * Forwards the GTFS entity to all given handler.
   */
  @Override
  public void handleEntity(final Object entity) {
    for (final IBaseEntityHandler handler : mAllHandler) {
      handler.handleEntity(entity);
    }
  }

}
