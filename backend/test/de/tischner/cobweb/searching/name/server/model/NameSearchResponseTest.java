package de.tischner.cobweb.searching.name.server.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link NameSearchResponse}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class NameSearchResponseTest {

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.server.model.NameSearchResponse#getMatches()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetMatches() {
    final NameSearchResponse response =
        new NameSearchResponse(10L, Arrays.asList(new Match(1L, "a"), new Match(2L, "b")));
    final Set<String> names = response.getMatches().stream().map(Match::getName).collect(Collectors.toSet());
    Assert.assertEquals(2, names.size());
    Assert.assertTrue(names.contains("a"));
    Assert.assertTrue(names.contains("b"));
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.server.model.NameSearchResponse#getTime()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetTime() {
    final NameSearchResponse response =
        new NameSearchResponse(10L, Arrays.asList(new Match(1L, "a"), new Match(2L, "b")));
    Assert.assertEquals(10L, response.getTime());
  }

  /**
   * Test method for
   * {@link de.tischner.cobweb.searching.name.server.model.NameSearchResponse#NameSearchResponse(long, java.util.List)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testNameSearchResponse() {
    try {
      new NameSearchResponse(10L, Collections.emptyList());
      new NameSearchResponse(0L, Collections.singletonList(new Match(1L, "a")));
    } catch (final Exception e) {
      Assert.fail();
    }
  }

}
