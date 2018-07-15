package de.unifreiburg.informatik.cobweb.parsing;

/**
 * Exception to be thrown when a problem while parsing data occurred. For
 * example if the data is in a wrong format or a file does not exist.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class ParseException extends RuntimeException {
  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new parse exception.
   */
  public ParseException() {
    super();
  }

  /**
   * Creates a new parse exception with a given cause.
   *
   * @param cause The cause of the parse exception
   */
  public ParseException(final Throwable cause) {
    super(cause);
  }
}
