package xdean.markdown.feature;

import static xdean.jex.util.lang.ExceptionUtil.uncheck;
import static xdean.markdown.TestUtil.assertWithDefaultFile;
import static xdean.markdown.TestUtil.getInputFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import xdean.markdown.model.MarkNode;
import xdean.markdown.reader.MarkNodeReader;

public class MarkPageTableCreatorTest {

  MarkNodeReader nodeReader = new MarkNodeReader();
  MarkPageTableCreator creator = new MarkPageTableCreator();

  @Test
  public void testCreate() throws Exception {
    assertWithDefaultFile(creator.createPageTable(
        nodeReader.read(getInputFile("simple.md"))).stream().collect(Collectors.joining("\n")), "simple.md");
    assertWithDefaultFile(creator.createPageTable(
        nodeReader.read(getInputFile("has-stub.md"))).stream().collect(Collectors.joining("\n")), "has-stub.md");
    assertWithDefaultFile(creator.createPageTable(
        nodeReader.read(getInputFile("has-content.md"))).stream().collect(Collectors.joining("\n")), "has-content.md");

    Path rawFile = getInputFile("multiple");
    Path temp = Files.createTempDirectory("test").resolve("multiple");
    Files.walk(rawFile).forEach(source -> uncheck(
        () -> Files.copy(source, temp.resolve(rawFile.relativize(source)), StandardCopyOption.REPLACE_EXISTING)));
    MarkNode node = nodeReader.read(temp);
    creator.createPageTableToFileRecursively(node);
    List<Path> files = Files.walk(temp)
        .filter(p -> !Files.isDirectory(p))
        .collect(Collectors.toList());
    for (Path file : files) {
      assertWithDefaultFile(Files.readAllLines(file).stream().collect(Collectors.joining("\n")),
          temp.getParent().relativize(file).toString());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongStub() throws Exception {
    creator.createPageTable(nodeReader.read(getInputFile(""))).stream().collect(Collectors.joining("\n"));
  }
}
