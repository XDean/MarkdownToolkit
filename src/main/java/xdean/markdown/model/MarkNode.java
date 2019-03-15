package xdean.markdown.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class MarkNode {
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
}
