package xdean.markdown.feature;

import static xdean.jex.util.function.Predicates.isEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import xdean.jex.util.string.StringUtil;
import xdean.markdown.handler.MarkContentReader;
import xdean.markdown.model.MarkConstants;
import xdean.markdown.model.MarkContent;
import xdean.markdown.model.MarkNode;

public class MarkContentTableCreator implements MarkConstants {

  MarkContentReader reader = new MarkContentReader();

  public void createContentTable(MarkNode node) throws IOException {
    if (node.isLeaf()) {
      createLeafContentTable(node);
    } else {
      createFolderContentTable(node);
    }
  }

  private void createLeafContentTable(MarkNode node) throws IOException {
    List<MarkContent> contents = reader.read(node);
    List<String> lines = createContentLines(contents);
    insertContentTable(node.getPath(), lines);
  }

  private void createFolderContentTable(MarkNode node) {

  }

  private List<String> createContentLines(List<MarkContent> contents) {
    return contents.stream()
        .map(c -> {
          if (c.getLevel() == 0) {
            return "# " + CONTENT_TABLE_TITLE;
          } else {
            return StringUtil.repeat("  ", c.getLevel() - 1) + "- " + c.getName();
          }
        })
        .collect(Collectors.toList());
  }

  private void insertContentTable(Path file, List<String> contents) throws IOException {
    List<String> lines = new ArrayList<>(Files.readAllLines(file));
    String stubStr = String.format(COMMENT_PATTERN, "CONTENT");
    String startStr = String.format(COMMENT_PATTERN, "CONTENT START");
    String endStr = String.format(COMMENT_PATTERN, "CONTENT END");
    int stubIndex = lines.indexOf(stubStr);
    int startIndex = lines.indexOf(startStr);
    int endIndex = lines.indexOf(endStr);
    int insertIndex;
    if (stubIndex != -1) {
      lines.remove(stubIndex);
      insertIndex = stubIndex;
    } else if (startIndex != -1 && endIndex != -1) {
      lines.removeAll(lines.subList(startIndex, endIndex + 1));
      insertIndex = startIndex;
    } else if (startIndex != -1 || endIndex != -1) {
      throw new IllegalArgumentException(
          String.format("Illegal stub comment, '%s' and '%s' should be present together", startStr, endStr));
    } else {
      insertIndex = 0;
    }
    lines.addAll(insertIndex, contents);
  }
}
