package xdean.markdown.feature;

import static xdean.jex.util.lang.ExceptionUtil.uncheck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import xdean.jex.util.string.StringUtil;
import xdean.markdown.model.MarkConstants;
import xdean.markdown.model.MarkContent;
import xdean.markdown.model.MarkNode;
import xdean.markdown.reader.MarkContentReader;

public class MarkContentTableCreator implements MarkConstants {

  MarkContentReader reader = new MarkContentReader();

  public void createContentTableToFile(MarkNode node) throws IOException {
    List<String> lines = createContentTable(node);
    node.getContentFile().ifPresent(p -> uncheck(() -> Files.write(p, lines)));
  }

  public List<String> createContentTable(MarkNode node) throws IOException {
    if (node.isLeaf()) {
      return createLeafContentTable(node);
    } else {
      return createFolderContentTable(node);
    }
  }

  private List<String> createLeafContentTable(MarkNode node) throws IOException {
    return node.getContentFile().map(p -> uncheck(() -> {
      List<MarkContent> contents = reader.read(node);
      List<String> lines = createContentLinesByContent(contents);
      return insertContentTable(p, lines);
    }))
        .orElseThrow(() -> new IllegalArgumentException());
  }

  private List<String> createFolderContentTable(MarkNode node) throws IOException {
    Path contentFile = node.getContentFile().orElseGet(() -> {
      Path p = node.getPath().resolve(README_FILE);
      uncheck(() -> Files.createFile(p));
      return p;
    });
    if (node.getChildren().isEmpty()) {
      return createLeafContentTable(node);
    } else {
      List<String> lines = createContentLinesByStrcture(node);
      return insertContentTable(contentFile, lines);
    }
  }

  private List<String> createContentLinesByContent(List<MarkContent> contents) {
    return contents.stream()
        .map(c -> {
          if (c.getLevel() == 0) {
            return "# " + CONTENT_TABLE_TITLE;
          } else {
            return StringUtil.repeat("  ", c.getLevel() - 1) + "- "
                + String.format(LINK_PATTERN, c.getName(), titleToLink(c.getName()));
          }
        })
        .collect(Collectors.toList());
  }

  private String titleToLink(String title) {
    return "#" + title.toLowerCase().replace(" ", "-").replace("`", "").replace("*", "");
  }

  private List<String> createContentLinesByStrcture(MarkNode node) {
    List<String> lines = new ArrayList<>();
    lines.add("# " + CONTENT_TABLE_TITLE);
    node.getChildren().forEach(n -> addContent(node, n, 0, lines));
    return lines;
  }

  private void addContent(MarkNode root, MarkNode node, int level, List<String> lines) {
    lines.add(StringUtil.repeat("  ", level) + "- "
        + String.format(LINK_PATTERN, node.getTitle(), root.getPath().relativize(node.getPath())));
    node.getChildren().forEach(n -> addContent(root, n, level + 1, lines));
  }

  private List<String> insertContentTable(Path file, List<String> contents) throws IOException {
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
      lines.subList(startIndex, endIndex + 1).clear();;
      insertIndex = startIndex;
    } else if (startIndex != -1 || endIndex != -1 || endIndex < startIndex) {
      throw new IllegalArgumentException(
          String.format("Illegal stub comment, '%s' and '%s' should be present together and ordered", startStr, endStr));
    } else {
      insertIndex = 0;
    }
    lines.add(insertIndex, "");
    lines.addAll(insertIndex, contents);
    return lines;
  }
}
