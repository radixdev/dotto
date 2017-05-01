package radix.com.dotto.models;

import radix.com.dotto.BuildConfig;
import radix.com.dotto.controllers.PixelInfo;
import radix.com.dotto.utils.enums.GameColor;
import radix.com.dotto.utils.freebasing.BaseConverter;

/**
 * Data keys are of the form "xxx:yyy"
 */
public class ProtocolHandler {

  public static String getKeyFromCoordinates(int x, int y) {
    String xPart = BaseConverter.decimalToBaseValue(x, BaseConverter.BASE_MAXIMUM);
    String yPart = BaseConverter.decimalToBaseValue(y, BaseConverter.BASE_MAXIMUM);
    return xPart + ProtocolConstants.PIXEL_SEPARATOR + yPart;
  }

  public static PixelInfo getPixelInfoFromData(String key, int colorCode) {
    if (BuildConfig.DEBUG) {
      if (!key.contains(ProtocolConstants.PIXEL_SEPARATOR)) {
        throw new RuntimeException("key doesn't contain separator " + key + " and sep " + ProtocolConstants.PIXEL_SEPARATOR);
      }
    }
    // find the xxx
    String xPart = key.substring(0, key.indexOf(ProtocolConstants.PIXEL_SEPARATOR));
    String yPart = key.substring(key.indexOf(ProtocolConstants.PIXEL_SEPARATOR) + 1, key.length());

    int xValue = BaseConverter.baseValueToDecimal(xPart, BaseConverter.BASE_MAXIMUM);
    int yValue = BaseConverter.baseValueToDecimal(yPart, BaseConverter.BASE_MAXIMUM);
    return new PixelInfo(GameColor.getGameColorByCode(colorCode), xValue, yValue);
  }
}
