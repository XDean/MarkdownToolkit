package xdean.markdown.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode(exclude = "parent")
@ToString(exclude = "parent")
public class MarkNode implements MarkConstants {
  MarkNode parent;

  @Singular
  List<MarkNode> children;

  String title;

  Path path;

  public MarkNode(MarkNode parent, List<MarkNode> children, String title, Path path) {
    this.parent = parent;
    this.children = new ArrayList<>(children);
    this.title = title;
    this.path = path;
  }

  public boolean isLeaf() {
    return !Files.isDirectory(path);
  }

  public boolean isRoot() {
    return parent == null;
  }

  public boolean isEmpty() {
    if (isLeaf()) {
      return false;
    } else if (children.isEmpty()) {
      return !getContentFile().isPresent();
    } else {
      return false;
    }
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
