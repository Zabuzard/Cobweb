package de.tischner.cobweb;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmTag;
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
    // Main.osmTest();
//    Main.manualTest();
    Main.manualTestCompressed();
  }

  private static void manualTest() throws IOException {
    // TODO Remove tests
    final AtomicLong counter = new AtomicLong();
    final long startTime = System.currentTimeMillis();
    Files.lines(Paths.get("backend", "res", "osm", "freiburg-regbez-latest.osm")).forEach(line -> {
      // Consume some time with O(line)
      if (!line.contains("?!?")) {
        counter.incrementAndGet();
      }
    });
    final long endTime = System.currentTimeMillis();
    System.out.println("Lines: " + counter.get());
    System.out.println("Time: " + (endTime - startTime));
  }

  private static void manualTestCompressed() throws IOException {
    // TODO Remove tests
    final AtomicLong counter = new AtomicLong();
    final long startTime = System.currentTimeMillis();

    final InputStream fin = Files.newInputStream(Paths.get("backend", "res", "osm", "freiburg-regbez-latest.osm.bz2"));
    final BufferedInputStream in = new BufferedInputStream(fin);
    final BZip2CompressorInputStream input = new BZip2CompressorInputStream(in);
    final BufferedReader br = new BufferedReader(new InputStreamReader(input));

    while (true) {
      final String line = br.readLine();
      if (line == null) {
        break;
      }
      // Consume some time with O(line)
      if (!line.contains("?!?")) {
        counter.incrementAndGet();
      }
    }
    final long endTime = System.currentTimeMillis();
    System.out.println("Lines: " + counter.get());
    System.out.println("Time: " + (endTime - startTime));
  }

  @SuppressWarnings("resource")
  private static void osmTest() throws MalformedURLException, IOException {
    // TODO Remove tests
    // Get the input
    final boolean testCompressed = false;
    final InputStream input;
    if (testCompressed) {
      final InputStream fin = Files
          .newInputStream(Paths.get("backend", "res", "osm", "freiburg-regbez-latest.osm.bz2"));
      final BufferedInputStream in = new BufferedInputStream(fin);
      input = new BZip2CompressorInputStream(in);
    } else {
      input = Files.newInputStream(Paths.get("backend", "res", "osm", "freiburg-regbez-latest.osm"));
    }

    // Create an iterator for XML data
    final OsmIterator iterator = new OsmXmlIterator(input, false);

    // Initialize some counters
    int numNodes = 0;
    int numWays = 0;
    int numRelations = 0;

    final long startTime = System.currentTimeMillis();

    // Iterate elements and increment our counters
    // depending on the type of element
    for (final EntityContainer container : iterator) {
      if (container.getType() == EntityType.Node) {
        numNodes++;
        final OsmNode node = (OsmNode) container.getEntity();
        if (node.getNumberOfTags() != 0) {
          final OsmTag tag = node.getTag(0);
          System.out.println(tag.getKey() + " : " + tag.getValue());
        }
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
        System.out.println(container);
      }
      if (numRelations != 0 && numRelations % 100000 == 0) {
        System.out.println("Relations: " + numRelations);
        System.out.println(container);
      }
    }
    final long endTime = System.currentTimeMillis();

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
