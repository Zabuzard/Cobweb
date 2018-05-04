package de.tischner.cobweb.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SerializationUtil<T extends Serializable> {
  @SuppressWarnings("unchecked")
  public T deserialize(final Path path) throws IOException, ClassNotFoundException, ClassCastException {
    try (InputStream is = Files.newInputStream(path)) {
      final ObjectInputStream in = new ObjectInputStream(is);
      return (T) in.readObject();
    }
  }

  public void serialize(final T object, final Path path) throws IOException {
    try (OutputStream os = Files.newOutputStream(path)) {
      final ObjectOutputStream out = new ObjectOutputStream(os);
      out.writeObject(object);
    }
  }
}
