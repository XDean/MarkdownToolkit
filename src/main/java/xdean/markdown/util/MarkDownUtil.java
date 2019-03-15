package xdean.markdown.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import xdean.markdown.model.MarkConstants;

public class MarkDownUtil implements MarkConstants {
  public static List<String> insertContent(String mark, Path file, List<String> contents) throws IOException {
    List<String> lines = new ArrayList<>(Files.readAllLines(file));
    if (contents.isEmpty()) {
      return lines;
    }
    String stubStr = String.format(COMMENT_PATTERN, mark);
    String startStr = String.format(COMMENT_PATTERN, mark + " START");
    String endStr = String.format(COMMENT_PATTERN, mark + " END");
    int stubIndex = lines.indexOf(stubStr);
    int startIndex = lines.indexOf(startStr);
    int endIndex = lines.indexOf(endStr);
    int insertIndex;
    if (stubIndex != -1) {
      lines.remove(stubIndex);
      insertIndex = stubIndex;
    } else if (startIndex != -1 && endIndex != -1) {
      lines.subList(startIndex, endIndex + 1).clear();
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
