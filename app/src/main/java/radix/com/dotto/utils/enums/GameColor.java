package radix.com.dotto.utils.enums;

import android.graphics.Color;

import java.util.Random;

/**
 * The available colors to the game. See https://raw.githubusercontent.com/schochastics/rplace/master/colordistribution.png
 */
public enum GameColor {
  DARK_GREY(Color.rgb(34, 34, 34)),
  DARK_GREEN(Color.rgb(2, 190, 1)),
  RED(Color.rgb(229, 0, 0)),
  DARK_BLUE(Color.rgb(1, 1, 1)),
  DARK_PURPLE(Color.rgb(1, 1, 1)),
  CHAMBRAY(Color.rgb(0, 131, 199)),
  BROWN(Color.rgb(160, 106, 66)),
  GOLD(Color.rgb(229, 149, 0)),
  LIGHT_GREY(Color.rgb(136, 136, 136)),
  LIGHT_BLUE(Color.rgb(0, 211, 221)),
  LIGHT_GREEN(Color.rgb(148, 224, 68)),
  YELLOW(Color.rgb(229, 217, 0)),
  MAGENTA(Color.rgb(207, 110, 228)),
  PINK(Color.rgb(255, 167, 209)),
  SLATE(Color.rgb(228, 228, 220)),
  WHITE(Color.rgb(255, 255, 255));

  private final int color;
  private static final Random random = new Random();
  private static final GameColor[] colors = GameColor.values();

  /**
   * Color/int array of all possible game colors
   */
  public static final int[] ALL_COLOR_VALUES = new int[GameColor.values().length];
  static {
    for (int i = 0; i < colors.length; i++) {
      ALL_COLOR_VALUES[i] = colors[i].getColor();
    }
  }

  GameColor(int color) {
    this.color = color;
  }

  /**
   * @return the color int as returned from the {@link Color} class
   */
  public int getColor() {
    return color;
  }

  /**
   * Gets the index of this color.
   */
  public int getCode() {
    return this.ordinal();
  }

  public static int getRandomColor() {
    return colors[random.nextInt(colors.length)].getColor();
  }

  public static GameColor getGameColorByCode(int code) {
    if (code > colors.length) {
      return WHITE;
    }
    return colors[code];
  }
}
