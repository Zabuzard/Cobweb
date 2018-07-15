package de.unifreiburg.informatik.cobweb.commands;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link CommandData}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class CommandDataTest {

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.commands.CommandData#CommandData(de.unifreiburg.informatik.cobweb.commands.ECommand)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testCommandDataECommand() {
    try {
      new CommandData(ECommand.START);
      new CommandData(ECommand.REDUCE);
      new CommandData(ECommand.CLEAN);
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.commands.CommandData#CommandData(de.unifreiburg.informatik.cobweb.commands.ECommand, java.util.Collection)}.
   */
  @SuppressWarnings({ "unused", "static-method" })
  @Test
  public void testCommandDataECommandCollectionOfPath() {
    try {
      new CommandData(ECommand.START, Arrays.asList(Paths.get("foo", "bar"), Paths.get("hello")));
      new CommandData(ECommand.REDUCE, Arrays.asList(Paths.get("foo", "bar")));
      new CommandData(ECommand.CLEAN, Collections.emptyList());
    } catch (final Exception e) {
      Assert.fail();
    }
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.commands.CommandData#getCommand()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetCommand() {
    Assert.assertEquals(ECommand.START, new CommandData(ECommand.START).getCommand());
    Assert.assertEquals(ECommand.REDUCE, new CommandData(ECommand.REDUCE).getCommand());
    Assert.assertEquals(ECommand.CLEAN, new CommandData(ECommand.CLEAN).getCommand());
  }

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.commands.CommandData#getPaths()}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testGetPaths() {
    Assert.assertEquals(Collections.emptyList(), new CommandData(ECommand.START).getPaths());
    Assert.assertEquals(Arrays.asList(Paths.get("foo", "bar"), Paths.get("hello")),
        new CommandData(ECommand.REDUCE, Arrays.asList(Paths.get("foo", "bar"), Paths.get("hello"))).getPaths());
    Assert.assertEquals(Collections.emptyList(), new CommandData(ECommand.CLEAN, Collections.emptyList()).getPaths());
  }

}
