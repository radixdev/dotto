package radix.com.dotto.controllers;

import android.graphics.Matrix;
import android.graphics.PointF;

import radix.com.dotto.models.IModelInterface;
import radix.com.dotto.models.WorldMap;
import radix.com.dotto.utils.GameColor;
import radix.com.dotto.views.IViewInterface;

/**
 *
 */
public class UserGestureController {
  private static final String TAG = UserGestureController.class.toString();

  private float mScaleFactor;
  private int mScreenOffsetX, mScreenOffsetY;
  private PointF mLastZoomCenter, mLastTouch;
  private Matrix mViewTransform;
  private final IModelInterface mWorldMap;
  private IViewInterface mGameView;

  public UserGestureController(WorldMap worldMap) {
    mScaleFactor = 1f;
    mScreenOffsetX = 0;
    mScreenOffsetY = 0;
    mLastZoomCenter = new PointF();
    mLastTouch = new PointF();
    mViewTransform = new Matrix();
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
//    Log.d(TAG, "zoom: " + mScaleFactor);
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
    UserTapInfo info = new UserTapInfo(GameColor.CHAMBRAY, mGameView.convertScreenPointToLocalPoint(touch));
    mWorldMap.onUserTapInfo(info);
  }

  public PointF getLastZoomCenter() {
    return mLastZoomCenter;
  }

  public PointF getLastTouch() {
    return mLastTouch;
  }

  /**
   * Sets the current view transform for this controller
   * @param viewTransform
   */
  public void setViewTransform(Matrix viewTransform) {
    mViewTransform.set(viewTransform);
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
