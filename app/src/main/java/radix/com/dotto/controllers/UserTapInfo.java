package radix.com.dotto.controllers;

import android.graphics.Point;

import radix.com.dotto.utils.enums.GameColor;

/**
 * A class for parcelling info to the model from the controller
 */
public class UserTapInfo {
  private final GameColor color;
  private final Point pointLocation;

  public UserTapInfo(GameColor color, Point pointLocation) {
    this.color = color;
    this.pointLocation = pointLocation;
  }

  public GameColor getColor() {
    return color;
  }

  public Point getPointLocation() {
    return pointLocation;
  }
}
