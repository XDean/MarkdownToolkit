package xdean.markdown.model;

public interface MarkConstants {
  String README_FILE = "README.md";
  String CONTENT_TABLE_TITLE = "Contents";

  String COMMENT_PATTERN = "[](%s)";
  String LINK_PATTERN = "[%s](%s)";
  String PAGE_TABLE_PATTERN = "| Previous | Next |\n" +
      "| --- | --- |\n" +
      "| %s | %s |";
  String BACK_TO_PARENT = "Back to parent";
}
