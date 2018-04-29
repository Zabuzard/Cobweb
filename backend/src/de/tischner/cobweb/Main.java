package de.tischner.cobweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The entry class of the application.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class Main {
  /**
   *
   * @param args Not supported
   */
  public static void main(final String[] args) {
    // TODO When answering the AJAX, don't forget to:
    // Access-Control-Allow-Origin: *

    try {
      final Application app = new Application(args);
      app.initialize();
      app.start();
    } catch (final Throwable e) {
      final Logger logger = LoggerFactory.getLogger(Main.class);
      logger.error("An unknown error occurred", e);
      // Print to standard error in case error occurred before logger was initialized
      System.err.println("An unknown error occurred");
      e.printStackTrace();
    }

    // TODO Make sure shutdown is always called, should be a shutdown hook
  }

  /**
   * Utility class. No implementation.
   */
  private Main() {

  }
}
