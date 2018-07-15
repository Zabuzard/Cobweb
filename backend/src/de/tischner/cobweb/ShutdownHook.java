package de.tischner.cobweb;

/**
 * Thread that, once started, shuts the given application down.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ShutdownHook extends Thread {
  /**
   * Application to shutdown on execution.
   */
  private final Application mApplication;

  /**
   * Creates a new shutdown hook that will shutdown the given application once
   * started.
   *
   * @param application The application to shutdown on execution
   */
  public ShutdownHook(final Application application) {
    mApplication = application;
  }

  @Override
  public void run() {
    if (!mApplication.wasShutdownRequested()) {
      mApplication.shutdown();
    }
  }
}
