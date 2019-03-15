package xdean.markdown.feature;

import static xdean.jex.util.lang.ExceptionUtil.uncheck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.CheckForNull;

import xdean.markdown.model.MarkConstants;
import xdean.markdown.model.MarkNode;
import xdean.markdown.util.MarkDownUtil;

public class MarkPageTableCreator implements MarkConstants {

  public void createPageTableToFile(MarkNode node) throws IOException {
    node.getContentFile().ifPresent(p -> uncheck(() -> Files.write(p, createPageTable(node))));
  }

  public List<String> createPageTable(MarkNode node) throws IOException {
    Optional<Path> contentFile = node.getContentFile();
    if (contentFile.isPresent()) {
      Path path = contentFile.get();
      List<String> lines = createPageTableLines(node);
      return insertPageTable(path, lines);
    }
    return Collections.emptyList();
  }

  private List<String> createPageTableLines(MarkNode node) {
    MarkNode left = getPrevious(node);
    MarkNode right = getNext(node);
    String leftLink = getLink(node, left);
    String rightLnk = getLink(node, right);
    return Arrays.asList(String.format(PAGE_TABLE_PATTERN, leftLink, rightLnk).split("\\R"));
  }

  protected MarkNode getPrevious(MarkNode node) {
    MarkNode parent = node.getParent();
    if (parent == null) {
      return null;
    }
    int index = parent.getChildren().indexOf(node);
    if (index == 0) {
      return parent;
    } else {
      return parent.getChildren().get(index - 1);
    }
  }

  protected MarkNode getNext(MarkNode node) {
    MarkNode parent = node.getParent();
    if (parent == null) {
      return null;
    }
    int index = parent.getChildren().indexOf(node);
    if (index == parent.getChildren().size() - 1) {
      return getNext(parent);
    } else {
      return parent.getChildren().get(index + 1);
    }
  }

  protected String getLink(MarkNode from, @CheckForNull MarkNode to) {
    if (to == null) {
      return "";
    } else {
      return String.format(LINK_PATTERN, to.getTitle(), from.getPath().relativize(to.getPath()));
    }
  }

  protected List<String> insertPageTable(Path file, List<String> contents) throws IOException {
    return MarkDownUtil.insertContent("PAGE TABLE", file, contents);
  }
}
