package radix.com.dotto.controllers;

import android.graphics.Point;
import android.support.annotation.ColorInt;

import radix.com.dotto.utils.enums.GameColor;

/**
 * A class for parcelling info to the model from the controller
 */
public class DotInfo {
  private final GameColor color;
  private final int pointX, pointY;

  public DotInfo(GameColor color, Point pointLocation) {
    this.color = color;
    this.pointX = pointLocation.x;
    this.pointY = pointLocation.y;
  }

  public DotInfo(GameColor color, int pointX, int pointY) {
    this.color = color;
    this.pointX = pointX;
    this.pointY = pointY;
  }

  /**
   * Copies the location info but uses the new color
   * @param color
   * @param otherInfo
   */
  public DotInfo(GameColor color, DotInfo otherInfo) {
    this.color = color;
    this.pointX = otherInfo.pointX;
    this.pointY = otherInfo.pointY;
  }

  public GameColor getColor() {
    return color;
  }

  public @ColorInt int getColorInt() {
    return color.getColor();
  }

  public int getPointX() {
    return pointX;
  }

  public int getPointY() {
    return pointY;
  }

  @Override
  public String toString() {
    return "DotInfo{" +
        "color=" + color.getColor() +
        ", pointX=" + pointX +
        ", pointY=" + pointY +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DotInfo info = (DotInfo) o;

    if (pointX != info.pointX) return false;
    if (pointY != info.pointY) return false;
    return color == info.color;

  }

  @Override
  public int hashCode() {
    int result = color.hashCode();
    result = 31 * result + pointX;
    result = 31 * result + pointY;
    return result;
  }
}
