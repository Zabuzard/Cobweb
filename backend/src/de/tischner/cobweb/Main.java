package de.tischner.cobweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The entry class of the application. Contains a {@link #main(String[])} method
 * which starts the {@link Application}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class Main {
  /**
   * Starts the {@link Application}.
   *
   * @param args Command line arguments to use. They are directly passed to
   *             {@link Application#Application(String[])}, see the
   *             {@link Application} class for details.
   */
  public static void main(final String[] args) {
    try {
      final Application app = new Application(args);
      app.initialize();
      app.start();
    } catch (final Throwable e) {
      final Logger logger = LoggerFactory.getLogger(Main.class);
      logger.error("An unknown error occurred", e);
      // Print to standard error in case error occurred before logger was
      // initialized
      System.err.println("An unknown error occurred");
      e.printStackTrace();
    }
  }

  /**
   * Utility class. No implementation.
   */
  private Main() {

  }
}
