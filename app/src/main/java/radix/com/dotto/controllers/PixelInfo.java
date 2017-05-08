package radix.com.dotto.controllers;

import android.graphics.Point;

import radix.com.dotto.utils.enums.GameColor;

/**
 * A class for parcelling info to the model from the controller
 */
public class PixelInfo {
  private final GameColor color;
  private final int pointX, pointY;

  public PixelInfo(GameColor color, Point pointLocation) {
    this.color = color;
    this.pointX = pointLocation.x;
    this.pointY = pointLocation.y;
  }

  public PixelInfo(GameColor color, int pointX, int pointY) {
    this.color = color;
    this.pointX = pointX;
    this.pointY = pointY;
  }

  /**
   * Copies the location info but uses the new color
   * @param color
   * @param otherInfo
   */
  public PixelInfo(GameColor color, PixelInfo otherInfo) {
    this.color = color;
    this.pointX = otherInfo.pointX;
    this.pointY = otherInfo.pointY;
  }

  public GameColor getColor() {
    return color;
  }

  public int getPointX() {
    return pointX;
  }

  public int getPointY() {
    return pointY;
  }

  @Override
  public String toString() {
    return "PixelInfo{" +
        "color=" + color.getColor() +
        ", pointX=" + pointX +
        ", pointY=" + pointY +
        '}';
  }
}
