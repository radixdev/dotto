package radix.com.dotto.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

import radix.com.dotto.controllers.UserGestureController;
import radix.com.dotto.models.WorldMap;
import radix.com.dotto.utils.FramerateUtils;

public class PixelGridSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
  private static final String TAG = PixelGridSurfaceView.class.toString();

  private final WorldMap mWorldMap;

  private final SurfaceHolder mSurfaceHolder;
  private volatile boolean mIsGamePlaying;
  private Thread mGameThread = null;

  // The drawing properties
  private Bitmap mCanvasBitmap = null;
  private Canvas mBackingCanvas = null;
  private Matrix mTransformMatrix;

  // Controller interface
  private UserGestureController mUserGestureController;

  private Random random;

  public PixelGridSurfaceView(Context context, WorldMap map, UserGestureController userGestureController) {
    super(context);
    this.mUserGestureController = userGestureController;
    mWorldMap = map;

    mSurfaceHolder = getHolder();
    mSurfaceHolder.addCallback(this);
    random = new Random();
  }

  @Override
  public void run() {
    while (mIsGamePlaying) {
      draw();
      controlFramerate();
    }
  }

  private void controlFramerate() {
    // Sleep a bit maybe
    try {
      Thread.sleep(FramerateUtils.getRefreshIntervalFromFramerate(100));
    } catch (InterruptedException e) {
      Log.e(TAG, "Exception while sleeping in game loop", e);
    }
  }

  private void draw() {
    if (mSurfaceHolder.getSurface().isValid()) {
      Canvas canvas = mSurfaceHolder.lockCanvas();
      this.draw(canvas);
      mSurfaceHolder.unlockCanvasAndPost(canvas);
    }
  }

  Matrix mPreviousTransform = new Matrix();
  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);

    final Paint pixelPaint = new Paint();
    pixelPaint.setColor(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
    for (int i = 0; i < 8; i++) {
      mBackingCanvas.drawPoint(random.nextInt(1000), random.nextInt(1000), pixelPaint);
    }

    // TODO: 4/19/2017 scale where the gesture takes place
    float scaleFactor = mUserGestureController.getScaleFactor();
    int screenOffsetX = mUserGestureController.getScreenOffsetX();
    int screenOffsetY = mUserGestureController.getScreenOffsetY();
    PointF zoomCenter = mUserGestureController.getLastZoomCenter();
    zoomCenter = mUserGestureController.getLastTouch();

    Matrix inverse = new Matrix();
    mPreviousTransform.invert(inverse);
    float[] screenPts = new float[]{zoomCenter.x, zoomCenter.y};
    inverse.mapPoints(screenPts);

    mTransformMatrix = new Matrix();

//    Log.d(TAG, "screen pts " + screenPts[0] + "  " + screenPts[1]);
//    Log.d(TAG, "screenOffsetX " + screenOffsetX + "  " + screenOffsetY + "  " + scaleFactor);
    mTransformMatrix.setScale(scaleFactor, scaleFactor);
    mTransformMatrix.postTranslate(screenOffsetX, screenOffsetY);
    mPreviousTransform.set(mTransformMatrix);
    canvas.drawBitmap(mCanvasBitmap, mTransformMatrix, null);
  }

  public void setPlaying(boolean playing) {
    Log.d(TAG, "Set playing to : " + playing);
    this.mIsGamePlaying = playing;

    if (playing) {
      mGameThread = new Thread(this);
      mGameThread.start();
    } else {
      try {
        mGameThread.join();
      } catch (InterruptedException e) {
        Log.e(TAG, "Set playing end thread exception", e);
      }
    }
  }

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {
    int width = getWidth();
    int height = getHeight();
    mCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    mBackingCanvas = new Canvas();
    mBackingCanvas.setBitmap(mCanvasBitmap);

//    mBackingCanvas.drawColor(Color.rgb(10, 10, 10));
    mTransformMatrix = new Matrix();
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}
}
