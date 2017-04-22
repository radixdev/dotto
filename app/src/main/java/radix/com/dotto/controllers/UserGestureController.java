package radix.com.dotto.controllers;

import android.graphics.PointF;
import android.util.Log;

/**
 *
 */
public class UserGestureController {
  private static final String TAG = UserGestureController.class.toString();

  private float mScaleFactor;
  private int mScreenOffsetX, mScreenOffsetY;
  private PointF mLastZoomCenter, mLastTouch;

  public UserGestureController() {
    mScaleFactor = 1f;
    mScreenOffsetX = 0;
    mScreenOffsetY = 0;
    mLastZoomCenter = new PointF();
    mLastTouch = new PointF();
  }

  /**
   *
   * @param zoomFactor
   * @param zoomCenterScreen where the user is zooming on screen
   */
  public void onUserZoom(float zoomFactor, PointF zoomCenterScreen) {
    Log.d(TAG, "zoom f " + zoomFactor);
    mScaleFactor *= zoomFactor;
//    mScaleFactor = NumberUtils.clamp(mScaleFactor, 0.4f, 500f);

    // see http://stackoverflow.com/a/13962157
    float sx = mScreenOffsetX;
    float sy = mScreenOffsetY;
    float fx = zoomCenterScreen.x;
    float fy = zoomCenterScreen.y;

    mScreenOffsetX -= (int) ((sx - fx) * (1 - zoomFactor));
    mScreenOffsetY -= (int) ((sy - fy) * (1 - zoomFactor));

    mLastZoomCenter = zoomCenterScreen;
  }

  public void onUserScroll(float scrollDistanceX, float scrollDistanceY) {
    mScreenOffsetX -= scrollDistanceX*1;
    mScreenOffsetY -= scrollDistanceY*1;
  }

  public void onUserTouch(PointF touch) {
    mLastTouch = touch;
  }

  public PointF getLastZoomCenter() {
    return mLastZoomCenter;
  }

  public PointF getLastTouch() {
    return mLastTouch;
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
