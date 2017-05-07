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
   * Performs zoom from the user. Transitions the controller state.
   *
   * @param zoomFactor
   * @param zoomCenterScreen where the user is zooming on screen
   */
  public void onUserZoom(float zoomFactor, PointF zoomCenterScreen) {
    changeControllerState(ControllerState.PANNING);
    doZoom(zoomFactor, zoomCenterScreen);
  }

  /**
   * Performs a zoom. No state change is made.
   * @param zoomFactor
   * @param zoomCenterScreen
   */
  private void doZoom(float zoomFactor, PointF zoomCenterScreen) {
    if (mScaleFactor <= MIN_ZOOM && zoomFactor < 1f ||
        mScaleFactor >= MAX_ZOOM && zoomFactor > 1f) {
      // Don't allow for over zoom or under zoom
      // A zoom factor < 1 means a zoom out
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
    Point localPoint = mGameView.convertScreenPointToLocalPoint(touch);
    if (mWorldMap.isLocalPointOutsideWorldBounds(localPoint)) {
      Log.d(TAG, "Focus point was outside map bounds");
      return;
    }
    doZoom(MAX_ZOOM / mScaleFactor, touch);
    changeControllerState(ControllerState.USER_FOCUSING);

    // This process of screen -> local -> screen effectively "snaps" to the center of a dot on screen
    mUserFocusInfoLocation = new PixelInfo(mColorChoice, new Point(localPoint.x, localPoint.y));
  }

  public void onUserLongTap(PointF touch) {
    final Point localPoint = mGameView.convertScreenPointToLocalPoint(touch);
    if (mWorldMap.isLocalPointOutsideWorldBounds(localPoint)) {
      Log.d(TAG, "Long tap point was outside map bounds");
      return;
    }
    changeControllerState(ControllerState.PANNING);
    // Pass the touch to the model
    PixelInfo info = new PixelInfo(mColorChoice, localPoint);
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
