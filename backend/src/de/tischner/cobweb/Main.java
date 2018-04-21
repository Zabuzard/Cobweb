package de.tischner.cobweb;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;

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
    System.out.println("Hello World!");
    Main.osmTest();
  }

  private static void osmTest() throws MalformedURLException, IOException {
    // Get the input
    InputStream fin = Files.newInputStream(Paths.get("backend", "res", "osm", "freiburg-regbez-latest.osm.bz2"));
    BufferedInputStream in = new BufferedInputStream(fin);
    BZip2CompressorInputStream input = new BZip2CompressorInputStream(in);

    // Create an iterator for XML data
    OsmIterator iterator = new OsmXmlIterator(input, false);

    // Initialize some counters
    int numNodes = 0;
    int numWays = 0;
    int numRelations = 0;

    long startTime = System.currentTimeMillis();

    // Iterate elements and increment our counters
    // depending on the type of element
    for (EntityContainer container : iterator) {
      if (container.getType() == EntityType.Node) {
        numNodes++;
      } else if (container.getType() == EntityType.Way) {
        numWays++;
      } else if (container.getType() == EntityType.Relation) {
        numRelations++;
      }

      if (numNodes % 1000000 == 0) {
        System.out.println("Nodes: " + numNodes);
      }
      if (numWays != 0 && numWays % 100000 == 0) {
        System.out.println("Ways: " + numWays);
      }
      if (numRelations != 0 && numRelations % 100000 == 0) {
        System.out.println("Relations: " + numRelations);
      }
    }
    long endTime = System.currentTimeMillis();

    // Print the results
    System.out.println("nodes: " + numNodes);
    System.out.println("ways: " + numWays);
    System.out.println("relations: " + numRelations);
    System.out.println("Time needed: " + (endTime - startTime));
  }

  /**
   * Utility class. No implementation.
   */
  private Main() {

  }
}
