package radix.com.dotto.utils.freebasing;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class BaseConverterTest {
  @Test
  public void testHex() {
    int base = 16;
    Map<Integer, String> map = new HashMap<>();
    map.put(5, "5");
    map.put(10, "a");
    map.put(17, "11");

    for (Integer decValue : map.keySet()) {
      String hex = BaseConverter.decimalToBaseValue(decValue, base);
      assertEquals(map.get(decValue), hex);
    }
  }

  @Test
  public void testDec() {
    int base = 10;
    Map<Integer, String> map = new HashMap<>();
    map.put(5, "5");
    map.put(10, "10");
    map.put(17, "17");

    for (Integer decValue : map.keySet()) {
      String baseValue = BaseConverter.decimalToBaseValue(decValue, base);
      assertEquals(map.get(decValue), baseValue);
    }
  }

  @Test
  public void testDecToDec() {
    int base = 10;
    int num = 32424;
    String decValue = BaseConverter.decimalToBaseValue(num, base);
    int decToDecValue = BaseConverter.baseValueToDecimal(decValue, base);

    assertEquals(num, decToDecValue);
  }

  @Test
  public void testBaseToBase() {
    int[] bases = new int[]{4, 10, 15, BaseConverter.BASE_MAXIMUM};
    int value = 690013434;

    for (int base : bases) {
      assertEquals(value, BaseConverter.baseValueToDecimal(BaseConverter.decimalToBaseValue(value, base), base));
    }
  }
}
