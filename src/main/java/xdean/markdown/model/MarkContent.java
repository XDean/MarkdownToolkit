package xdean.markdown.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class MarkContent {
  int level;
  String name;
  MarkContent parent;
  @Singular
  List<MarkContent> children;
}
