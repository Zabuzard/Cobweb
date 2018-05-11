package de.tischner.cobweb.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class used to serialize and deserialize objects.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <T> Type of the object to serialize or deserialize
 */
public final class SerializationUtil<T extends Serializable> {
  /**
   * Attempts to deserialize an object from the given path.
   *
   * @param path The path to deserialize from
   * @return The deserialized object
   * @throws IOException            If an I/O exception occurred while trying to
   *                                read the file
   * @throws ClassNotFoundException If the class to deserialize to could not be
   *                                found
   * @throws ClassCastException     If the deserialized object is not of the
   *                                type to deserialize to
   */
  @SuppressWarnings("unchecked")
  public T deserialize(final Path path) throws IOException, ClassNotFoundException, ClassCastException {
    try (InputStream is = Files.newInputStream(path)) {
      final ObjectInputStream in = new ObjectInputStream(is);
      return (T) in.readObject();
    }
  }

  /**
   * Serializes the given object to the given path.
   *
   * @param object The object to serialize
   * @param path   The path to serialize the object to
   * @throws IOException If an I/O exception occurred while writing to the file
   */
  public void serialize(final T object, final Path path) throws IOException {
    try (OutputStream os = Files.newOutputStream(path)) {
      final ObjectOutputStream out = new ObjectOutputStream(os);
      out.writeObject(object);
    }
  }
}
