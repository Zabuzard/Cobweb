package de.tischner.cobweb.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

/**
 * 
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class OsmReducer {
  /**
   * 
   * @param args Not supported
   */
  public static void main(final String[] args) {
    final Path input = Paths.get("backend", "res", "osm", "freiburg-regbez-latest.osm");
    final Path output = Paths.get("backend", "res", "osm", "freiburg-regbez-latest-reduced.osm");

    final OsmReducer reducer = new OsmReducer(input);
    reducer.readData();
    reducer.reduceToRoadOnly();
    reducer.writeToPath(output);
  }

  private LinkedHashMap<Long, OsmNode> mIdToNode;
  private final LinkedHashMap<Long, OsmRelation> mIdToRelation;
  private LinkedHashMap<Long, OsmWay> mIdToWay;

  private final Path mInputPath;

  public OsmReducer(final Path inputPath) {
    mInputPath = inputPath;
    mIdToNode = new LinkedHashMap<>();
    mIdToWay = new LinkedHashMap<>();
    mIdToRelation = new LinkedHashMap<>();
  }

  public void readData() {
    System.out.println("Start reading data");
    final int printEvery = 100_000;
    long counter = 0;

    try (InputStream input = Files.newInputStream(mInputPath)) {
      final OsmIterator iterator = new OsmXmlIterator(input, true);

      for (final EntityContainer container : iterator) {
        if (container.getType() == EntityType.Node) {
          final OsmNode node = (OsmNode) container.getEntity();
          mIdToNode.put(node.getId(), node);
        } else if (container.getType() == EntityType.Way) {
          final OsmWay way = (OsmWay) container.getEntity();
          mIdToWay.put(way.getId(), way);
        } else if (container.getType() == EntityType.Relation) {
          final OsmRelation relation = (OsmRelation) container.getEntity();
          mIdToRelation.put(relation.getId(), relation);
        }

        counter++;
        if (counter % printEvery == 0) {
          System.out.println("Processed elements: " + counter);
        }
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }

    System.out.println("Finished reading data");
  }

  public void reduceToRoadOnly() {
    System.out.println("Starting reduction");
    final int printEvery = 100_000;
    long counter = 0;

    // Collect all road ways
    final LinkedHashMap<Long, OsmWay> roadWayIdToWay = new LinkedHashMap<>();
    for (final Long wayId : mIdToWay.keySet()) {
      counter++;
      if (counter % printEvery == 0) {
        System.out.println("Processed ways: " + counter);
      }

      final OsmWay way = mIdToWay.get(wayId);
      if (!OsmModelUtil.getTagsAsMap(way).containsKey("highway")) {
        continue;
      }
      roadWayIdToWay.put(wayId, way);
    }
    // Overwrite old ways with road ways
    mIdToWay = roadWayIdToWay;

    System.out.println("Finished ways");
    counter = 0;

    // Collect all nodes that are part of a road way
    final LinkedHashMap<Long, OsmNode> roadNodeIdToNode = new LinkedHashMap<>();
    for (final OsmWay way : roadWayIdToWay.values()) {
      final int amountOfNodes = way.getNumberOfNodes();
      for (int i = 0; i < amountOfNodes; i++) {
        final long nodeId = way.getNodeId(i);
        final OsmNode node = mIdToNode.get(nodeId);
        roadNodeIdToNode.put(nodeId, node);

        counter++;
        if (counter % printEvery == 0) {
          System.out.println("Processed nodes: " + counter);
        }
      }
    }
    // Overwrite old nodes with road nodes
    mIdToNode = roadNodeIdToNode;

    System.out.println("Finished nodes");

    System.out.println("Finished reduction");
  }

  public void writeToPath(final Path outputPath) {
    System.out.println("Start writing data");
    final int printEvery = 100_000;
    long counter = 0;

    try (OutputStream output = Files.newOutputStream(outputPath)) {
      final OsmOutputStream writer = new OsmXmlOutputStream(output, true);
      // Nodes
      for (final OsmNode node : mIdToNode.values()) {
        writer.write(node);

        counter++;
        if (counter % printEvery == 0) {
          System.out.println("Processed elements: " + counter);
        }
      }
      // Ways
      for (final OsmWay way : mIdToWay.values()) {
        writer.write(way);

        counter++;
        if (counter % printEvery == 0) {
          System.out.println("Processed elements: " + counter);
        }
      }
      // Relations
      for (final OsmRelation relation : mIdToRelation.values()) {
        writer.write(relation);

        counter++;
        if (counter % printEvery == 0) {
          System.out.println("Processed elements: " + counter);
        }
      }
      writer.complete();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    System.out.println("Finished writing data");
  }
}
