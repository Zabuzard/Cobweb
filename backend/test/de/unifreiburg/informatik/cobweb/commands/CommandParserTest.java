package de.unifreiburg.informatik.cobweb.commands;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the class {@link CommandParser}.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class CommandParserTest {

  /**
   * Test method for
   * {@link de.unifreiburg.informatik.cobweb.commands.CommandParser#parseCommands(java.lang.String[])}.
   */
  @SuppressWarnings("static-method")
  @Test
  public void testParseCommands() {
    CommandData commandData = CommandParser.parseCommands(new String[] {});
    Assert.assertEquals(ECommand.START, commandData.getCommand());
    Assert.assertTrue(commandData.getPaths().isEmpty());

    commandData = CommandParser.parseCommands(new String[] { "reduce" });
    Assert.assertEquals(ECommand.REDUCE, commandData.getCommand());
    Assert.assertTrue(commandData.getPaths().isEmpty());

    commandData = CommandParser.parseCommands(new String[] { "clean", "foo/bar", "hello" });
    Assert.assertEquals(ECommand.CLEAN, commandData.getCommand());
    Assert.assertEquals(2, commandData.getPaths().size());
    final Iterator<Path> pathIter = commandData.getPaths().iterator();
    Assert.assertEquals(Paths.get("foo/bar"), pathIter.next());
    Assert.assertEquals(Paths.get("hello"), pathIter.next());
  }

}
