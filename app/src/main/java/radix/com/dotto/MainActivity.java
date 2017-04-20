package radix.com.dotto;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import radix.com.dotto.models.WorldMap;
import radix.com.dotto.views.PixelGridSurfaceView;

public class MainActivity extends AppCompatActivity  implements GestureDetector.OnGestureListener {
  private static final String TAG = MainActivity.class.toString();

  private PixelGridSurfaceView gameView;
  private WorldMap worldMap;
  private GestureDetectorCompat mDetector;
  private ScaleGestureDetector mScaleGestureDetector;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    worldMap = new WorldMap();
    gameView = new PixelGridSurfaceView(this, worldMap);
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
    this.mDetector.onTouchEvent(event);
    this.mScaleGestureDetector.onTouchEvent(event);
    return super.onTouchEvent(event);
  }

  @Override
  public boolean onDown(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public void onShowPress(MotionEvent motionEvent) {}

  @Override
  public boolean onSingleTapUp(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    int ty = (int) e2.getY();
    int tx = (int) e2.getX();

    gameView.tx -= distanceX*1;
    gameView.ty -= distanceY*1;

//    Log.v(TAG, "onScroll: " + e1.toString()+e2.toString());
    return false;
  }

  @Override
  public void onLongPress(MotionEvent motionEvent) {

  }

  @Override
  public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
    return false;
  }

  private class ScaleListener  extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      float scaleFactor = gameView.scaleFactor * mScaleGestureDetector.getScaleFactor();

      gameView.scaleFactor = Math.min(500f, Math.max(scaleFactor, 0.1f));
      Log.d(TAG, "scale: " + gameView.scaleFactor);
      return false;
    }
  }
}
