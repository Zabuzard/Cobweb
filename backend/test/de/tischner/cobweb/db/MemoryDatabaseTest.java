package de.tischner.cobweb.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.slimjars.dist.gnu.trove.list.TLongList;
import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.tischner.cobweb.parsing.osm.EHighwayType;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Tag;
import de.topobyte.osm4j.core.model.impl.Way;

/**
 * Test for the class {@link MemoryDatabase}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class MemoryDatabaseTest {
  /**
   * The memory database used for testing.
   */
  private MemoryDatabase mMemoryDatabase;

  /**
   * Setups a memory database instance for testing.
   */
  @Before
  public void setUp() {
    mMemoryDatabase = new MemoryDatabase();
    final ArrayList<OsmEntity> entities = new ArrayList<>();

    final ArrayList<OsmTag> nodeTags = new ArrayList<>();
    nodeTags.add(new Tag("name", "Wall street 2"));

    final TLongList wayNodes = new TLongArrayList(new long[] { 2L, 3L, 4L });
    final ArrayList<OsmTag> wayTags = new ArrayList<>();
    wayTags.add(new Tag("highway", "motorway"));
    wayTags.add(new Tag("maxspeed", "100"));
    wayTags.add(new Tag("name", "Main street"));

    entities.add(new Node(1, 10, 10, nodeTags));
    entities.add(new Node(2, 20, 20));
    entities.add(new Node(3, 30, 30));
    entities.add(new Way(1, wayNodes, wayTags));
    entities.add(new Node(4, 40, 40));
    entities.add(new Node(5, 50, 50));

    mMemoryDatabase.offerOsmEntities(entities, 6);
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.db.MemoryDatabase#getAllNodeNameData()}.
   */
  @Test
  public final void testGetAllNodeNameData() {
    final Collection<NodeNameData> nodeNameData = mMemoryDatabase.getAllNodeNameData();
    Assert.assertEquals(1, nodeNameData.size());

    final NodeNameData data = nodeNameData.iterator().next();
    Assert.assertEquals(1L, data.getId());
    Assert.assertEquals("Wall street 2", data.getName());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.db.MemoryDatabase#getHighwayData(java.util.stream.LongStream, int)}.
   */
  @Test
  public final void testGetHighwayDataLongStreamInt() {
    final Collection<HighwayData> allHighwayData = mMemoryDatabase.getHighwayData(LongStream.of(1L, 5L), 1);
    Assert.assertEquals(1, allHighwayData.size());
    final HighwayData highwayData = allHighwayData.iterator().next();
    Assert.assertEquals(1L, highwayData.getWayId());
    Assert.assertEquals(EHighwayType.MOTORWAY, highwayData.getType());
    Assert.assertEquals(100, highwayData.getMaxSpeed());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.db.MemoryDatabase#getNodeByName(java.lang.String)}.
   */
  @Test
  public final void testGetNodeByName() {
    final Optional<Long> node = mMemoryDatabase.getNodeByName("Wall street 2");
    Assert.assertTrue(node.isPresent());
    Assert.assertEquals(1L, node.get().longValue());

    Assert.assertFalse(mMemoryDatabase.getNodeByName("Wall street").isPresent());
    Assert.assertFalse(mMemoryDatabase.getNodeByName("Main street").isPresent());
    Assert.assertFalse(mMemoryDatabase.getNodeByName("").isPresent());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.db.MemoryDatabase#getNodeName(long)}.
   */
  @Test
  public final void testGetNodeName() {
    final Optional<String> name = mMemoryDatabase.getNodeName(1L);
    Assert.assertTrue(name.isPresent());
    Assert.assertEquals("Wall street 2", name.get());

    Assert.assertFalse(mMemoryDatabase.getNodeName(2L).isPresent());
    Assert.assertFalse(mMemoryDatabase.getNodeName(-100L).isPresent());
    Assert.assertFalse(mMemoryDatabase.getNodeName(0L).isPresent());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.db.MemoryDatabase#getSpatialNodeData(java.util.stream.LongStream, int)}.
   */
  @Test
  public final void testGetSpatialNodeDataLongStreamInt() {
    final Collection<SpatialNodeData> allSpatialNodeData =
        mMemoryDatabase.getSpatialNodeData(LongStream.of(1L, 2L, 10L, 3L), 3);
    Assert.assertEquals(3, allSpatialNodeData.size());

    final Set<Long> nodeIds = allSpatialNodeData.stream().map(SpatialNodeData::getId).collect(Collectors.toSet());
    Assert.assertEquals(3, nodeIds.size());
    Assert.assertTrue(nodeIds.contains(1L));
    Assert.assertTrue(nodeIds.contains(2L));
    Assert.assertFalse(nodeIds.contains(10L));
    Assert.assertTrue(nodeIds.contains(3L));

    final Set<Float> latitudes =
        allSpatialNodeData.stream().map(SpatialNodeData::getLatitude).collect(Collectors.toSet());
    Assert.assertEquals(3, latitudes.size());
    Assert.assertTrue(latitudes.contains(10.0F));
    Assert.assertTrue(latitudes.contains(20.0F));
    Assert.assertTrue(latitudes.contains(30.0F));

    final Set<Float> longitudes =
        allSpatialNodeData.stream().map(SpatialNodeData::getLongitude).collect(Collectors.toSet());
    Assert.assertEquals(3, longitudes.size());
    Assert.assertTrue(longitudes.contains(10.0F));
    Assert.assertTrue(longitudes.contains(20.0F));
    Assert.assertTrue(longitudes.contains(30.0F));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.db.MemoryDatabase#getWayByName(java.lang.String)}.
   */
  @Test
  public final void testGetWayByName() {
    final Optional<Long> way = mMemoryDatabase.getWayByName("Main street");
    Assert.assertTrue(way.isPresent());
    Assert.assertEquals(1L, way.get().longValue());

    Assert.assertFalse(mMemoryDatabase.getNodeByName("Wall street").isPresent());
    Assert.assertFalse(mMemoryDatabase.getNodeByName("Main street 2").isPresent());
    Assert.assertFalse(mMemoryDatabase.getNodeByName("").isPresent());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.db.MemoryDatabase#getWayName(long)}.
   */
  @Test
  public final void testGetWayName() {
    final Optional<String> name = mMemoryDatabase.getWayName(1L);
    Assert.assertTrue(name.isPresent());
    Assert.assertEquals("Main street", name.get());

    Assert.assertFalse(mMemoryDatabase.getWayName(2L).isPresent());
    Assert.assertFalse(mMemoryDatabase.getWayName(-100L).isPresent());
    Assert.assertFalse(mMemoryDatabase.getWayName(0L).isPresent());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.db.MemoryDatabase#MemoryDatabase()}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public final void testMemoryDatabase() {
    try {
      new MemoryDatabase();
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.db.MemoryDatabase#offerOsmEntities(java.util.stream.Stream, int)}.
   */
  @Test
  public final void testOfferOsmEntitiesStreamOfOsmEntityInt() {
    try {
      mMemoryDatabase.offerOsmEntities(Collections.emptyList(), 0);
      mMemoryDatabase.offerOsmEntities(Collections.singletonList(new Node(1, 10, 10)), 1);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
