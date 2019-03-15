package xdean.markdown.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class MarkNode implements MarkConstants {
  MarkNode parent;

  @Singular
  List<MarkNode> children;

  String title;

  Path path;

  public boolean isLeaf() {
    return !Files.isDirectory(path);
  }

  public boolean isRoot() {
    return parent == null;
  }

  public boolean isEmpty() {
    return !getContentFile().isPresent();
  }

  public Optional<Path> getContentFile() {
    if (isLeaf()) {
      return Optional.of(path);
    } else {
      Path contentFile = path.resolve(README_FILE);
      if (Files.exists(contentFile)) {
        return Optional.of(contentFile);
      } else {
        return Optional.empty();
      }
    }
  }
}
