package xdean.markdown;

import static xdean.jex.util.lang.ExceptionUtil.uncheck;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;

import org.junit.Assert;

import xdean.jex.extra.json.JsonPrinter;
import xdean.jex.util.reflect.ReflectUtil;

public enum TestUtil {
  ;

  private static final Path INPUT_PATH = Paths.get("src/test/resources/input");
  private static final Path GOLDEN_PATH = Paths.get("src/test/resources/golden");

  public static Path getInputFile(String name) {
    StackTraceElement caller = ReflectUtil.getCaller();
    return INPUT_PATH.resolve(caller.getClassName().replace('.', '\\')).resolve(caller.getMethodName()).resolve(name);
  }

  public static void assertWithDefaultFile(String actual) throws IOException {
    assertWithFile(actual, getDefaultGoldenPath(null, null));
  }

  public static void assertWithDefaultFile(String actual, String name) throws IOException {
    assertWithFile(actual, getDefaultGoldenPath(name, null));
  }

  public static void assertWithDefultJson(Object actual) throws IOException {
    assertWithDefultJson(actual, null);
  }

  public static void assertWithDefultJson(Object actual, String name) throws IOException {
    assertWithFile(JsonPrinter.getDefault().toString(actual), getDefaultGoldenPath(name, "json"));
  }

  public static void assertWithFile(String actual, Path expectPath) throws IOException {
    if (Files.exists(expectPath) == false) {
      uncheck(() -> Files.createDirectories(expectPath.getParent()));
      uncheck(() -> Files.write(expectPath, actual.getBytes()));
      System.out.printf("Create absent golden file by actual value: %s\n", expectPath);
    }
    try {
      Assert.assertEquals(
          removeEndingEmptyLine(Files.readAllLines(expectPath, Charset.defaultCharset()).stream()
              .collect(Collectors.joining(System.lineSeparator()))),
          removeEndingEmptyLine(actual.replaceAll("\\R", System.lineSeparator())));
    } catch (AssertionError e) {
      Path actualPath = expectPath.resolveSibling(expectPath.getFileName().toString() + ".actual");
      System.err.printf("Assert fail. Actual file has been created: %s\n", actualPath);
      uncheck(() -> Files.write(actualPath, actual.getBytes()));
      throw e;
    }
  }

  public static Path getDefaultGoldenPath(@CheckForNull String name, @CheckForNull String suffix) {
    StackTraceElement caller = ReflectUtil.getCaller();
    return GOLDEN_PATH
        .resolve(caller.getClassName().replace('.', '\\'))
        .resolve(caller.getMethodName())
        .resolve((name == null ? "golden" : name) + (suffix == null ? "" : ("." + suffix)))
        .toAbsolutePath();
  }

  private static String removeEndingEmptyLine(String str) {
    String result = str;
    while (result.endsWith(System.lineSeparator())) {
      result = result.substring(0, result.length() - System.lineSeparator().length());
    }
    return result;
  }
}
