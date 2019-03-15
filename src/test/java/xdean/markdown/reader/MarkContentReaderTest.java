package xdean.markdown.reader;

import static xdean.markdown.TestUtil.assertWithDefultJson;
import static xdean.markdown.TestUtil.getInputFile;

import org.junit.Test;

public class MarkContentReaderTest {
  MarkNodeReader nodeReader = new MarkNodeReader();
  MarkContentReader contentReader = new MarkContentReader();

  @Test
  public void testRead() throws Exception {
    assertWithDefultJson(contentReader.read(nodeReader.read(getInputFile("single.md"))), "single");
    assertWithDefultJson(contentReader.read(nodeReader.read(getInputFile("folder"))), "folder");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNoContent() throws Exception {
    contentReader.read(nodeReader.read(getInputFile("no-readme")));
  }
}
