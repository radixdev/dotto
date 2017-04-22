package radix.com.dotto;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import radix.com.dotto.controllers.UserGestureController;
import radix.com.dotto.models.WorldMap;
import radix.com.dotto.views.PixelGridSurfaceView;

public class MainActivity extends AppCompatActivity  implements GestureDetector.OnGestureListener {
  private static final String TAG = MainActivity.class.toString();

  private PixelGridSurfaceView gameView;
  private WorldMap mWorldMap;
  private GestureDetectorCompat mDetector;
  private ScaleGestureDetector mScaleGestureDetector;
  private UserGestureController mUserGestureController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mWorldMap = new WorldMap();
    mUserGestureController = new UserGestureController();
    gameView = new PixelGridSurfaceView(this, mWorldMap, mUserGestureController);
    setContentView(gameView);

    mDetector = new GestureDetectorCompat(this, this);
    mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
  }

  @Override
  protected void onPause() {
    super.onPause();
    gameView.setPlaying(false);
  }

  @Override
  protected void onResume() {
    super.onResume();
    gameView.setPlaying(true);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    boolean handled = this.mScaleGestureDetector.onTouchEvent(event);
//    if (!mScaleGestureDetector.isInProgress()) {
      handled = this.mDetector.onTouchEvent(event);
//    }
    return handled;
  }

  @Override
  public boolean onDown(MotionEvent motionEvent) {
    mUserGestureController.onUserTouch(new PointF(motionEvent.getX(), motionEvent.getY()));
    return true;
  }

  @Override
  public void onShowPress(MotionEvent motionEvent) {}

  @Override
  public boolean onSingleTapUp(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    mUserGestureController.onUserScroll(distanceX, distanceY);
    return false;
  }

  @Override
  public void onLongPress(MotionEvent motionEvent) {}

  @Override
  public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
    return false;
  }

  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    float focusX, focusY;
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
