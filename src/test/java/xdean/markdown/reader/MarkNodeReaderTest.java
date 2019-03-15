package xdean.markdown.reader;

import static xdean.markdown.TestUtil.assertWithDefultJson;
import static xdean.markdown.TestUtil.getInputFile;

import org.junit.Test;

public class MarkNodeReaderTest {
  MarkNodeReader reader = new MarkNodeReader();

  @Test
  public void testRead() throws Exception {
    assertWithDefultJson(reader.read(getInputFile("single.md")), "single");
    assertWithDefultJson(reader.read(getInputFile("multiple")), "multiple");
  }
}
