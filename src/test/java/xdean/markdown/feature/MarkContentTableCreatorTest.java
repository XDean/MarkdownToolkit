package xdean.markdown.feature;

import static xdean.markdown.TestUtil.assertWithDefaultFile;
import static xdean.markdown.TestUtil.getInputFile;

import java.util.stream.Collectors;

import org.junit.Test;

import xdean.markdown.reader.MarkNodeReader;

public class MarkContentTableCreatorTest {

  MarkNodeReader nodeReader = new MarkNodeReader();
  MarkContentTableCreator creator = new MarkContentTableCreator();

  @Test
  public void testCreate() throws Exception {
    assertWithDefaultFile(creator.createContentTable(
        nodeReader.read(getInputFile("simple.md"))).stream().collect(Collectors.joining("\n")), "simple.md");
    assertWithDefaultFile(creator.createContentTable(
        nodeReader.read(getInputFile("has-stub.md"))).stream().collect(Collectors.joining("\n")), "has-stub.md");
    assertWithDefaultFile(creator.createContentTable(
        nodeReader.read(getInputFile("has-content.md"))).stream().collect(Collectors.joining("\n")), "has-content.md");
    assertWithDefaultFile(creator.createContentTable(
        nodeReader.read(getInputFile("multiple"))).stream().collect(Collectors.joining("\n")), "multiple.md");
    assertWithDefaultFile(creator.createContentTable(
        nodeReader.read(getInputFile("multiple/sub"))).stream().collect(Collectors.joining("\n")), "multiple-sub.md");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongStub() throws Exception {
    creator.createContentTable(nodeReader.read(getInputFile("wrong-stub.md"))).stream().collect(Collectors.joining("\n"));
  }
}
