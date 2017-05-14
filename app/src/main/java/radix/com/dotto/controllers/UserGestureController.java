package radix.com.dotto.controllers;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import radix.com.dotto.models.abstractors.IModelInterface;
import radix.com.dotto.models.WorldModel;
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
  private DotInfo mUserFocusInfoLocation;

  public UserGestureController(WorldModel worldModel) {
    mScaleFactor = 20f;
    mScreenOffsetX = 0;
    mScreenOffsetY = 0;
    mWorldMap = worldModel;
    mColorChoice = GameColor.CHAMBRAY;

    // TODO: 5/6/2017 test line. Remove this
    mUserFocusInfoLocation = new DotInfo(GameColor.DARK_GREEN, 100, 100);

    // TODO: 5/14/2017 Add a listener for model updates and lock out the screen
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

  public void onUserRequestFocus(PointF touch) {
    Point localPoint = mGameView.convertScreenPointToLocalPoint(touch);
    if (mWorldMap.isLocalPointOutsideWorldBounds(localPoint)) {
      Log.d(TAG, "Focus point was outside map bounds");
      return;
    }

    boolean stateChanged = changeControllerState(ControllerState.USER_FOCUSING);
    doZoom(MAX_ZOOM / mScaleFactor, touch);

    // If the previous focus location was at the same point
    DotInfo previousInfo = mUserFocusInfoLocation;
    mUserFocusInfoLocation = new DotInfo(mColorChoice, new Point(localPoint.x, localPoint.y));

    if (!stateChanged && mUserFocusInfoLocation.equals(previousInfo)) {
      // The state went from focusing to focusing again but at the same position
      Log.d(TAG, "User requested focus at the same location. Applying dot");
      applyDotFromUser(mUserFocusInfoLocation);
      changeControllerState(ControllerState.PANNING);
    }
  }

  public void onUserLongTap(PointF touch) {
    changeControllerState(ControllerState.PANNING);
    final Point localPoint = mGameView.convertScreenPointToLocalPoint(touch);
    if (mWorldMap.isLocalPointOutsideWorldBounds(localPoint)) {
      Log.d(TAG, "Long tap point was outside map bounds");
      return;
    }
    // Pass the touch to the model
    DotInfo info = new DotInfo(mColorChoice, localPoint);
    applyDotFromUser(info);
  }

  private void applyDotFromUser(DotInfo info) {
    mWorldMap.onWriteDotInfo(info);
    changeControllerState(ControllerState.PANNING);
  }

  public void setUserColorChoice(GameColor colorChoice) {
    mColorChoice = colorChoice;

    if (mControllerState == ControllerState.USER_FOCUSING) {
      // Assign the color using the focus point
      if (mUserFocusInfoLocation != null) {
        applyDotFromUser(new DotInfo(colorChoice, mUserFocusInfoLocation));
      } else {
        Log.d(TAG, "Tried to apply null pixel info from the user focus!");
      }
    }
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

  public DotInfo getUserFocusInfo() {
    return mUserFocusInfoLocation;
  }

  /**
   * Changes the state
   *
   * @param newState
   * @return true if the state changed
   */
  private boolean changeControllerState(ControllerState newState) {
    if (mControllerState == newState) {
      return false;
    }
    Log.d(TAG, "Changing state from " + mControllerState + " to " + newState);
    mControllerState = newState;

    mGameView.onControllerStateChange(newState);
    return true;
  }

  public ControllerState getControllerState() {
    return mControllerState;
  }
}
