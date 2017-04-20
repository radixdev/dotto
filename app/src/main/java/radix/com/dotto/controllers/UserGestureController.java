package radix.com.dotto.controllers;

/**
 *
 */
public class UserGestureController {

  private float mScaleFactor;
  private int mScreenOffsetX, mScreenOffsetY;

  public UserGestureController() {
    mScaleFactor = 1f;
    mScreenOffsetX = 0;
    mScreenOffsetY = 0;
  }

  public float getScaleFactor() {
    return mScaleFactor;
  }

  public int getScreenOffsetX() {
    return mScreenOffsetX;
  }

  public int getScreenOffsetY() {
    return mScreenOffsetY;
  }
}
