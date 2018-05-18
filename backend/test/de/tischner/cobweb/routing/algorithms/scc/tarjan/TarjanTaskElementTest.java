package de.tischner.cobweb.routing.algorithms.scc.tarjan;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tischner.cobweb.routing.model.graph.BasicNode;
import de.tischner.cobweb.routing.model.graph.road.RoadNode;

/**
 * Test for the class {@link TarjanTaskElement}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class TarjanTaskElementTest {

  /**
   * The Tarjan task element used for testing.
   */
  private TarjanTaskElement<BasicNode> mElement;

  /**
   * Setups a Tarjan task element instance for testing.
   */
  @Before
  public void setUp() {
    mElement = new TarjanTaskElement<>(new BasicNode(1), new BasicNode(2));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.scc.tarjan.TarjanTaskElement#getCurrentTask()}.
   */
  @Test
  public void testGetCurrentTask() {
    Assert.assertEquals(ETarjanTask.INDEX, mElement.getCurrentTask());
    mElement.reportTaskAccomplished();
    Assert.assertEquals(ETarjanTask.GET_SUCCESSORS, mElement.getCurrentTask());
    mElement.reportTaskAccomplished();
    Assert.assertEquals(ETarjanTask.SET_LOWLINK, mElement.getCurrentTask());
    mElement.reportTaskAccomplished();
    Assert.assertNull(mElement.getCurrentTask());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.scc.tarjan.TarjanTaskElement#getNode()}.
   */
  @Test
  public void testGetNode() {
    Assert.assertEquals(1L, mElement.getNode().getId());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.scc.tarjan.TarjanTaskElement#getPredecessor()}.
   */
  @Test
  public void testGetPredecessor() {
    Assert.assertEquals(2L, mElement.getPredecessor().getId());
    Assert.assertNull(new TarjanTaskElement<>(new RoadNode(1, 1.0F, 1.0F), null).getPredecessor());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.scc.tarjan.TarjanTaskElement#reportTaskAccomplished()}.
   */
  @Test
  public void testReportTaskAccomplished() {
    Assert.assertEquals(ETarjanTask.INDEX, mElement.getCurrentTask());
    mElement.reportTaskAccomplished();
    Assert.assertEquals(ETarjanTask.GET_SUCCESSORS, mElement.getCurrentTask());
    mElement.reportTaskAccomplished();
    Assert.assertEquals(ETarjanTask.SET_LOWLINK, mElement.getCurrentTask());
    mElement.reportTaskAccomplished();
    Assert.assertNull(mElement.getCurrentTask());

    mElement.reportTaskAccomplished();
    Assert.assertNull(mElement.getCurrentTask());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.routing.algorithms.scc.tarjan.TarjanTaskElement#TarjanTaskElement(java.lang.Object, java.lang.Object)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testTarjanTaskElement() {
    try {
      new TarjanTaskElement<>(new RoadNode(1, 1.0F, 1.0F), new RoadNode(2, 2.0F, 2.0F));
      new TarjanTaskElement<>(new RoadNode(1, 1.0F, 1.0F), null);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
