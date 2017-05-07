package radix.com.dotto.controllers;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import radix.com.dotto.models.IModelInterface;
import radix.com.dotto.models.WorldMap;
import radix.com.dotto.utils.enums.GameColor;
import radix.com.dotto.views.IViewInterface;

/**
 *
 */
public class UserGestureController {
  private static final String TAG = UserGestureController.class.toString();

  private static final float MIN_ZOOM = 1.1f;
  private static final float MAX_ZOOM = 150f;

  private float mScaleFactor;
  private float mScreenOffsetX, mScreenOffsetY;
  private final IModelInterface mWorldMap;
  private IViewInterface mGameView;

  private GameColor mColorChoice;
  // Controller state
  private ControllerState mControllerState = ControllerState.PANNING;
  private PixelInfo mUserFocusInfoLocation;

  public UserGestureController(WorldMap worldMap) {
    mScaleFactor = 20f;
    mScreenOffsetX = 0;
    mScreenOffsetY = 0;
    mWorldMap = worldMap;
    mColorChoice = GameColor.CHAMBRAY;

    // TODO: 5/6/2017 test line. Remove this
    mUserFocusInfoLocation = new PixelInfo(GameColor.DARK_GREEN, 100, 100);
  }

  public void setViewInterface(IViewInterface viewInterface) {
    mGameView = viewInterface;
  }

  /**
   * @param zoomFactor
   * @param zoomCenterScreen where the user is zooming on screen
   */
  public void onUserZoom(float zoomFactor, PointF zoomCenterScreen) {
    changeControllerState(ControllerState.PANNING);

    if (mScaleFactor <= MIN_ZOOM && zoomFactor < 1f) {
      // Don't allow for over zoom
      // A zoom factor < 1 means a zoom out
      return;
    }

    if (mScaleFactor >= MAX_ZOOM && zoomFactor > 1f) {
      return;
    }
    mScaleFactor *= zoomFactor;

    // see http://stackoverflow.com/a/13962157
    float sx = mScreenOffsetX;
    float sy = mScreenOffsetY;
    float fx = zoomCenterScreen.x;
    float fy = zoomCenterScreen.y;

    mScreenOffsetX -= (sx - fx) * (1 - zoomFactor);
    mScreenOffsetY -= (sy - fy) * (1 - zoomFactor);
  }

  public void onUserScroll(float scrollDistanceX, float scrollDistanceY) {
    changeControllerState(ControllerState.PANNING);
    mScreenOffsetX -= scrollDistanceX * 1;
    mScreenOffsetY -= scrollDistanceY * 1;
  }

  public void onUserSingleTap(PointF touch) {
    onUserZoom(MAX_ZOOM / mScaleFactor, touch);
    changeControllerState(ControllerState.PANNING);
    changeControllerState(ControllerState.TEST_TAP);


    // This process of screen -> local -> screen effectively "snaps" to the center of a dot on screen
    Point localPoint = mGameView.convertScreenPointToLocalPoint(touch);
    Point screenPoint = mGameView.convertLocalPointToScreenPoint(localPoint);
    mUserFocusInfoLocation = new PixelInfo(mColorChoice, new Point(screenPoint.x, screenPoint.y));
  }

  public void onUserLongTap(PointF touch) {
    changeControllerState(ControllerState.PANNING);
    // Pass the touch to the model
    PixelInfo info = new PixelInfo(mColorChoice, mGameView.convertScreenPointToLocalPoint(touch));
    mWorldMap.onPixelInfoChange(info);
  }

  public void setUserColorChoice(GameColor colorChoice) {
    mColorChoice = colorChoice;
  }

  public GameColor getColorChoice() {
    return mColorChoice;
  }

  public float getScaleFactor() {
    return mScaleFactor;
  }

  public float getScreenOffsetX() {
    return mScreenOffsetX;
  }

  public float getScreenOffsetY() {
    return mScreenOffsetY;
  }

  public PixelInfo getUserFocusInfo() {
    return mUserFocusInfoLocation;
  }

  private void changeControllerState(ControllerState newState) {
    if (mControllerState == newState) {
//      Log.d(TAG, "Got call to cycle state from " + newState);
      return;
    }
    Log.d(TAG, "Changing state from " + mControllerState + " to " + newState);
    mControllerState = newState;

    mGameView.onControllerStateChange(newState);
  }

  public ControllerState getControllerState() {
    return mControllerState;
  }
}
