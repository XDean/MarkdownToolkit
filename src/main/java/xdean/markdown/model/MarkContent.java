package xdean.markdown.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;

@Data
@Builder
@ToString(exclude = "parent")
public class MarkContent {
  int level;
  String name;
  MarkContent parent;
  @Singular
  List<MarkContent> children;

  public MarkContent(int level, String name, MarkContent parent, List<MarkContent> children) {
    this.level = level;
    this.name = name;
    this.parent = parent;
    this.children = new ArrayList<>(children);
  }
}
