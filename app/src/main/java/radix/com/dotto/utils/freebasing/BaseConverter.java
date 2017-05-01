package radix.com.dotto.utils.freebasing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import radix.com.dotto.BuildConfig;

/**
 * Converts any decimal number into another as a String.
 */
public class BaseConverter {
  private static final char[] NUMBER_BASE_VALUES = new char[]{
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
      'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
      'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
      '(', ')', '@', '!', '%', '^', '&', '*', '<', '>', ';', '`'
  };

  private static Map<Character, Integer> BASE_VALUE_TO_DECIMAL = new HashMap<>();

  // Populate the decimal mapping
  static {
    for (int i = 0; i < NUMBER_BASE_VALUES.length; i++) {
      char key = NUMBER_BASE_VALUES[i];
      BASE_VALUE_TO_DECIMAL.put(key, i);
    }
  }

  public static final int BASE_MAXIMUM = NUMBER_BASE_VALUES.length;

  public static String decimalToBaseValue(int val, int base) {
    if (BuildConfig.DEBUG) {
      if (base > BASE_MAXIMUM) {
        throw new IllegalArgumentException("base too large fam: " + base);
      }
    }

    String result = "";
    while (val != 0) {
      int modulo = val % base;
      char numValue = NUMBER_BASE_VALUES[modulo];
      result = numValue + result;

      // now get the next val
      val = (int) Math.floor(val / base);
    }

    return result;
  }

  public static int baseValueToDecimal(String baseValue, int base) {
    int valueLen = baseValue.length();
    if (valueLen <= 0) {
      return 0;
    }

    // 1, 1*base, 1*base^2, etc.
    int result = 0;

    for (int index = valueLen - 1, baseMultiplier = 1; index >= 0; index--, baseMultiplier *= base) {
      // get the char
      char charAtIndex = baseValue.charAt(index);
      // get the value
      int decimalValue = BASE_VALUE_TO_DECIMAL.get(charAtIndex);
      result += decimalValue * baseMultiplier;
    }

    return result;
  }

  // random asserts lol
  static {
    if (BuildConfig.DEBUG) {
      // verify no repeats
      Set<Character> hash = new HashSet<>();
      for (char value : NUMBER_BASE_VALUES) {
        if (hash.contains(value)) {
          // not good!
          throw new RuntimeException("Base values contains repeats. Saw twice: " + value);
        } else {
          hash.add(value);
        }
      }
    }
  }
}
