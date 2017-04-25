package radix.com.dotto;

import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Window;
import android.view.WindowManager;

import radix.com.dotto.controllers.UserGestureController;
import radix.com.dotto.models.WorldMap;
import radix.com.dotto.views.PixelGridSurfaceView;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.toString();

  private PixelGridSurfaceView mGameView;
  private WorldMap mWorldMap;
  private GestureDetectorCompat mGestureDetector;
  private ScaleGestureDetector mScaleGestureDetector;
  private UserGestureController mUserGestureController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    mWorldMap = new WorldMap();
    mUserGestureController = new UserGestureController(mWorldMap);
    mGameView = new PixelGridSurfaceView(this, mWorldMap, mUserGestureController);
    mUserGestureController.setViewInterface(mGameView);

    mGestureDetector = new GestureDetectorCompat(this, new GestureListener());
    mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      mScaleGestureDetector.setQuickScaleEnabled(false);
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      mScaleGestureDetector.setStylusScaleEnabled(false);
    }
    setContentView(mGameView);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mGameView.setPlaying(false);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mGameView.setPlaying(true);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    boolean scaleHandled = mScaleGestureDetector.onTouchEvent(event);
//    if (!mScaleGestureDetector.isInProgress()) {
    boolean touchHandled = this.mGestureDetector.onTouchEvent(event);
//    }
    return scaleHandled || touchHandled;
  }

  private class GestureListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      mUserGestureController.onUserScroll(distanceX, distanceY);
      return true;
    }

//    @Override
//    public boolean onDown(MotionEvent motionEvent) {
//      mUserGestureController.onUserTouch(new PointF(motionEvent.getX(), motionEvent.getY()));
//      return true;
//    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
      mUserGestureController.onUserTouch(new PointF(motionEvent.getX(), motionEvent.getY()));
    }
  }

  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    private float focusX, focusY;

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
      focusX = detector.getFocusX();
      focusY = detector.getFocusY();
      return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      mUserGestureController.onUserZoom(detector.getScaleFactor(), new PointF(focusX, focusY));
      return true;
    }
  }
}
