package de.tischner.cobweb.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmRelationMember;
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
    reducer.reduceToRoadOnly(0.1, true);
    reducer.writeToPath(output);
  }

  private OsmBounds mBounds;
  private LinkedHashMap<Long, OsmNode> mIdToNode;
  private LinkedHashMap<Long, OsmRelation> mIdToRelation;
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
      if (iterator.hasBounds()) {
        mBounds = iterator.getBounds();
      }

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
        } else {
          throw new AssertionError();
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

  public void reduceToRoadOnly(final double reduceWaysToPerc, final boolean skipShortWays) {
    System.out.println("Starting reduction");
    final int shortWayThreshold = 100;
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
      // Skip short ways if set
      if (skipShortWays && way.getNumberOfNodes() < shortWayThreshold) {
        continue;
      }
      roadWayIdToWay.put(wayId, way);
    }
    // Overwrite old ways with road ways
    mIdToWay = roadWayIdToWay;

    // Further reduce roads to given percentage
    final LinkedHashMap<Long, OsmWay> reducedRoadWayIdToWay = new LinkedHashMap<>();
    final int targetSize = (int) (mIdToWay.size() * reduceWaysToPerc);
    for (final Entry<Long, OsmWay> entry : mIdToWay.entrySet()) {
      reducedRoadWayIdToWay.put(entry.getKey(), entry.getValue());
      if (reducedRoadWayIdToWay.size() >= targetSize) {
        break;
      }
    }
    // Overwrite road ways with reduced road ways
    mIdToWay = reducedRoadWayIdToWay;

    System.out.println("Finished ways");
    counter = 0;

    // Collect all nodes that are part of a road way
    final LinkedHashMap<Long, OsmNode> roadNodeIdToNode = new LinkedHashMap<>();
    for (final OsmWay way : mIdToWay.values()) {
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

    // Collect all relations that are part of a road ways or nodes
    final LinkedHashMap<Long, OsmRelation> roadRelationIdToRelation = new LinkedHashMap<>();
    for (final OsmRelation relation : mIdToRelation.values()) {
      boolean containsNonRoadMember = false;
      final int amountOfMember = relation.getNumberOfMembers();
      for (int i = 0; i < amountOfMember; i++) {
        final OsmRelationMember member = relation.getMember(i);
        final long memberId = member.getId();
        final EntityType memberType = member.getType();
        boolean isRoadElement = true;
        if (memberType == EntityType.Node) {
          isRoadElement = mIdToNode.containsKey(memberId);
        } else if (memberType == EntityType.Way) {
          isRoadElement = mIdToWay.containsKey(memberId);
        }
        if (!isRoadElement) {
          containsNonRoadMember = true;
          break;
        }
      }
      // Keep relations that contain road member only
      if (!containsNonRoadMember) {
        roadRelationIdToRelation.put(relation.getId(), relation);
      }
    }
    // Overwrite old relations with road relations
    mIdToRelation = roadRelationIdToRelation;

    // Remove all relations that refer to dead relations.
    // Removal needs to be re-checked after each removal since a removal could
    // transitively make other relations dead too.
    // Loop can be stopped once no relations get selected for removal anymore.
    List<OsmRelation> relationsToRemove = new ArrayList<>();
    while (true) {
      for (final OsmRelation relation : mIdToRelation.values()) {
        final int amountOfMember = relation.getNumberOfMembers();
        boolean containsDeadRelationMember = false;
        for (int i = 0; i < amountOfMember; i++) {
          final OsmRelationMember member = relation.getMember(i);
          if (member.getType() == EntityType.Relation) {
            final long memberId = member.getId();
            if (!mIdToRelation.containsKey(memberId)) {
              containsDeadRelationMember = true;
              break;
            }
          }
        }
        // Select relation for removal if it contains at least one dead relation member
        if (containsDeadRelationMember) {
          relationsToRemove.add(relation);
        }
      }

      // There is nothing to remove anymore
      if (relationsToRemove.isEmpty()) {
        break;
      }

      // Remove selected relations
      System.out.println("Removing relations: " + relationsToRemove.size());
      relationsToRemove.stream().map(OsmRelation::getId).forEach(mIdToRelation::remove);
      relationsToRemove = new ArrayList<>();
    }

    System.out.println("Finished reduction:");
    System.out.println("Nodes: " + mIdToNode.size());
    System.out.println("Ways: " + mIdToWay.size());
    System.out.println("Relation: " + mIdToRelation.size());
  }

  public void writeToPath(final Path outputPath) {
    System.out.println("Start writing data");
    // Make sure everything is written in the expected format since the library
    // doesn't handle it by itself
    Locale.setDefault(Locale.US);

    final int printEvery = 100_000;
    long counter = 0;

    try (OutputStream output = Files.newOutputStream(outputPath)) {
      final OsmOutputStream writer = new OsmXmlOutputStream(output, true);
      // Bounds
      if (mBounds != null) {
        writer.write(mBounds);
      }

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
