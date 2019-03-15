package xdean.markdown.handler;

import static xdean.jex.util.lang.ExceptionUtil.uncheck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import xdean.markdown.model.MarkNode;

public class MarkNodeReader {

  public MarkNode read(Path path) throws IOException {
    MarkNode node = MarkNode.builder()
        .path(path)
        .title(getTitle(path))
        .build();
    if (Files.isDirectory(path)) {
      List<MarkNode> children = new ArrayList<>();
      Files.newDirectoryStream(path)
          .forEach(child -> {
            MarkNode childNode = uncheck(() -> read(child));
            childNode.setParent(node);
            children.add(childNode);
          });
      node.setChildren(children);
      return node;
    } else {
      return node;
    }
  }

  public String getTitle(Path path) throws IOException {
    if (Files.isDirectory(path)) {
      Path readme = path.resolve("README.md");
      if (Files.exists(readme) && !Files.isDirectory(readme)) {
        return getContentTitle(readme).orElse(path.getFileName().toString());
      } else {
        return path.getFileName().toString();
      }
    } else {
      return getContentTitle(path)
          .orElse(path.getFileName().toString());
    }
  }

  protected Optional<String> getContentTitle(Path path) throws IOException {
    return Files.lines(path).findFirst()
        .flatMap(s -> {
          if (s.startsWith("# ")) {
            return Optional.of(s.substring(2));
          } else {
            return Optional.empty();
          }
        });
  }
}
