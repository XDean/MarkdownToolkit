package xdean.markdown.handler;

import static xdean.jex.util.lang.ExceptionUtil.uncheck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import xdean.markdown.model.MarkConstants;
import xdean.markdown.model.MarkNode;

public class MarkNodeReader implements MarkConstants {

  public MarkNode read(Path path) throws IOException {
    MarkNode node = MarkNode.builder()
        .path(path)
        .title(getTitle(path))
        .build();
    if (Files.isDirectory(path)) {
      List<MarkNode> children = new ArrayList<>();
      StreamSupport.stream(Files.newDirectoryStream(path).spliterator(), false)
          .filter(p -> {
            if (Files.isDirectory(p)) {
              return true;
            }
            String fileName = p.getFileName().toString();
            return fileName.endsWith(".md") && !fileName.equals(README_FILE);
          })
          .sorted(Comparator.comparing(p -> p.getFileName().toString()))
          .forEach(child -> {
            MarkNode childNode = uncheck(() -> read(child));
            if (!childNode.isEmpty()) {
              childNode.setParent(node);
              children.add(childNode);
            }
          });
      node.setChildren(children);
      return node;
    } else {
      return node;
    }
  }

  public String getTitle(Path path) throws IOException {
    if (Files.isDirectory(path)) {
      Path readme = path.resolve(README_FILE);
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
