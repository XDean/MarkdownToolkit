package xdean.markdown.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import xdean.markdown.model.MarkContent;
import xdean.markdown.model.MarkNode;

public class MarkContentReader {
  public List<MarkContent> read(MarkNode node) throws IOException {
    Path content = node.getContentFile()
        .orElseThrow(() -> new IllegalArgumentException("The markdown node doesn't have content: " + node));
    Deque<MarkContent> stack = new ArrayDeque<>();
    MarkContent root = MarkContent.builder().level(0).name("root").build();
    stack.push(root);
    List<MarkContent> collect = new ArrayList<>(Files.lines(content)
        .filter(s -> s.matches("#+.*"))
        .map(s -> MarkContent.builder().level(getHashTagCount(s)).name(getTitleName(s)).build())
        .peek(c -> {
          MarkContent parent = stack.peek();
          while (stack.peek().getLevel() >= c.getLevel()) {
            stack.pop();
            parent = stack.peek();
          }
          parent.getChildren().add(c);
          c.setParent(parent);
          stack.push(c);
        })
        .collect(Collectors.toList()));
    collect.add(0, root);
    return collect;
  }

  private int getHashTagCount(String line) {
    for (int i = 0; i < line.length(); i++) {
      if (line.charAt(i) != '#') {
        return i;
      }
    }
    return 0;
  }

  private String getTitleName(String line) {
    for (int i = 0; i < line.length(); i++) {
      if (line.charAt(i) != '#') {
        return line.substring(i).trim();
      }
    }
    return "";
  }
}
