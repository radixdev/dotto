package radix.com.dotto.utils;

public class FramerateUtils {

  public static int getRefreshIntervalFromFramerate(int targetFramerate) {
    return 1000 / targetFramerate;
  }
}
