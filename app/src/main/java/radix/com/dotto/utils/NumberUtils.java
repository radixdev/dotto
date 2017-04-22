package radix.com.dotto.utils;

/**
 *
 */
public class NumberUtils {

  public static int clamp(int x, int min, int max) {
    return Math.max(min, Math.min(x, max));
  }

  public static float clamp(float x, float min, float max) {
    return Math.max(min, Math.min(x, max));
  }
}
