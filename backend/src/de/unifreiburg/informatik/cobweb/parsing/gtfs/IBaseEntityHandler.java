package de.unifreiburg.informatik.cobweb.parsing.gtfs;

import java.io.IOException;

import org.onebusaway.csv_entities.EntityHandler;

/**
 * Basic interface for classes that handle entities.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public interface IBaseEntityHandler extends EntityHandler {
  /**
   * Callback used when all entities were handled.
   *
   * @throws IOException If an I/O Exception occurred
   */
  void complete() throws IOException;
}
