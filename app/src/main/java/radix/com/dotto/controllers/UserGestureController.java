package radix.com.dotto.controllers;

import android.graphics.PointF;

import radix.com.dotto.models.IModelInterface;
import radix.com.dotto.models.WorldMap;
import radix.com.dotto.utils.enums.GameColor;
import radix.com.dotto.views.IViewInterface;

/**
 *
 */
public class UserGestureController {
  private static final String TAG = UserGestureController.class.toString();

  private float mScaleFactor;
  private int mScreenOffsetX, mScreenOffsetY;
  private PointF mLastZoomCenter, mLastTouch;
  private final IModelInterface mWorldMap;
  private IViewInterface mGameView;

  public UserGestureController(WorldMap worldMap) {
    mScaleFactor = 20f;
    mScreenOffsetX = 0;
    mScreenOffsetY = 0;
    mLastZoomCenter = new PointF();
    mLastTouch = new PointF();
    mWorldMap = worldMap;
  }

  public void setViewInterface(IViewInterface viewInterface) {
    mGameView = viewInterface;
  }

  /**
   *
   * @param zoomFactor
   * @param zoomCenterScreen where the user is zooming on screen
   */
  public void onUserZoom(float zoomFactor, PointF zoomCenterScreen) {
    final double MIN_ZOOM = 1.1f;
    if (mScaleFactor <= MIN_ZOOM && zoomFactor < 1f) {
      // Don't allow for over zoom
      // A zoom factor < 1 means a zoom out
      return;
    }
    mScaleFactor *= zoomFactor;

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

    // Pass the touch to the model
    PixelInfo info = new PixelInfo(GameColor.CHAMBRAY, mGameView.convertScreenPointToLocalPoint(touch));
    mWorldMap.onPixelInfoChange(info);
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
