package de.tischner.cobweb;

import java.io.IOException;
import java.net.MalformedURLException;

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
   * @throws IOException
   * @throws MalformedURLException
   */
  public static void main(final String[] args) throws MalformedURLException, IOException {
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

    // TODO Make sure shutdown is always called, shut be a shutdown hook
  }

  /**
   * Utility class. No implementation.
   */
  private Main() {

  }
}
