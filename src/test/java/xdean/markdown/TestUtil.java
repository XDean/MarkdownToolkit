package xdean.markdown;

import static xdean.jex.util.lang.ExceptionUtil.uncheck;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;

import xdean.jex.extra.json.JsonPrinter;
import xdean.jex.util.reflect.ReflectUtil;

public enum TestUtil {
  ;

  private static final Path GOLDEN_PATH = Paths.get("src/test/resources/golden");

  public static void assertWithDefaultFile(String actual) throws IOException {
    assertWithFile(actual, getDefaultPath(null));
  }

  public static void assertWithDefaultFile(String actual, String suffix) throws IOException {
    assertWithFile(actual, getDefaultPath(suffix));
  }

  public static void assertWithDefultJson(Object actual) throws IOException {
    assertWithFile(JsonPrinter.getDefault().toString(actual), getDefaultPath("json"));
  }

  public static void assertWithFile(String actual, Path expectPath) throws IOException {
    if (Files.exists(expectPath) == false) {
      uncheck(() -> Files.createDirectories(expectPath.getParent()));
      uncheck(() -> Files.write(expectPath, actual.getBytes()));
      System.out.printf("Create absent golden file by actual value: %s\n", expectPath);
    }
    try {
      Assert.assertEquals(
          Files.readAllLines(expectPath, Charset.defaultCharset()),
          actual);
    } catch (AssertionError e) {
      Path actualPath = expectPath.resolveSibling(expectPath.getFileName().toString() + ".actual");
      System.err.printf("Assert fail. Actual file has been created: %s\n", actualPath);
      uncheck(() -> Files.write(actualPath, actual.getBytes()));
      throw e;
    }
  }

  private static Path getDefaultPath(String suffix) {
    StackTraceElement caller = ReflectUtil.getCaller();
    return GOLDEN_PATH
        .resolve(caller.getClassName().replace('.', '\\'))
        .resolve(suffix == null ? caller.getMethodName() : caller.getMethodName() + "." + suffix)
        .toAbsolutePath();
  }
}
