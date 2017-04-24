package radix.com.dotto.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.Random;

import radix.com.dotto.controllers.UserGestureController;
import radix.com.dotto.controllers.UserTapInfo;
import radix.com.dotto.models.IModelInterface;
import radix.com.dotto.models.WorldMap;
import radix.com.dotto.utils.FramerateUtils;
import radix.com.dotto.utils.GameColor;

public class PixelGridSurfaceView extends SurfaceView implements IViewInterface, Runnable, SurfaceHolder.Callback {
  private static final String TAG = PixelGridSurfaceView.class.toString();

  private volatile boolean mIsGamePlaying;
  private Thread mGameThread = null;

  // The drawing properties
  private Bitmap mCanvasBitmap = null;
  private final SurfaceHolder mSurfaceHolder;
  private int screenWidth, screenHeight;

  private Canvas mBackingCanvas = null;
  private Matrix mTransformMatrix;
  private final Paint mPixelPaint;

  // Drawing the background
  private Bitmap mBackgroundBitmap = null;
  private Matrix mBackgroundTransform;

  // Controller interface
  private UserGestureController mUserGestureController;
  private final IModelInterface mWorldMap;

  private Random random;

  public PixelGridSurfaceView(Context context, WorldMap map, UserGestureController userGestureController) {
    super(context);
    this.mUserGestureController = userGestureController;
    mWorldMap = map;

    mSurfaceHolder = getHolder();
    mSurfaceHolder.addCallback(this);
    random = new Random();
    mPixelPaint = new Paint();
  }

  @Override
  public void run() {
    while (mIsGamePlaying) {
      long start = System.currentTimeMillis();
      executeDraw();
      long end = System.currentTimeMillis();
      controlFramerate(end - start);
      Log.d(TAG, "frame took: " + (end - start));
    }
  }

  private void controlFramerate(long frameTime) {
    // Sleep a bit maybe
    long sleepTime = FramerateUtils.getRefreshIntervalFromFramerate(20) - frameTime;
    if (sleepTime <= 0) {
      return;
    }
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      Log.e(TAG, "Exception while sleeping in game loop", e);
    }
  }

  private void executeDraw() {
    if (mSurfaceHolder.getSurface().isValid()) {
//      Canvas canvas = mSurfaceHolder.lockCanvas();
      Canvas canvas = mSurfaceHolder.getSurface().lockHardwareCanvas();

      boolean HWA = canvas.isHardwareAccelerated();

      this.draw(canvas);
      mSurfaceHolder.getSurface().unlockCanvasAndPost(canvas);
    }
  }

  private void drawBackground(Canvas canvas) {
    // transform it and write-back the changes to the actual canvas
    mBackgroundTransform.setScale(screenWidth, screenHeight);
    canvas.drawBitmap(mBackgroundBitmap, mBackgroundTransform, null);
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);

    drawBackground(canvas);

    mPixelPaint.setColor(GameColor.getRandomColor());
    mPixelPaint.setAntiAlias(true);
    mPixelPaint.setStyle(Paint.Style.STROKE);
    for (int i = 0; i < 128; i++) {
      mBackingCanvas.drawPoint(random.nextInt(1000), random.nextInt(1000), mPixelPaint);
    }

    // Pop from the model
    List<UserTapInfo> taps = mWorldMap.getGridInfo(25);
    for (UserTapInfo info : taps) {
      mPixelPaint.setColor(info.getColor().getColor());
      mBackingCanvas.drawPoint(info.getPointLocation().x, info.getPointLocation().y, mPixelPaint);
    }

    // TODO: 4/19/2017 scale where the gesture takes place
    float scaleFactor = mUserGestureController.getScaleFactor();
    int screenOffsetX = mUserGestureController.getScreenOffsetX();
    int screenOffsetY = mUserGestureController.getScreenOffsetY();

    mTransformMatrix.reset();

    mTransformMatrix.setScale(scaleFactor, scaleFactor);
    mTransformMatrix.postTranslate(screenOffsetX, screenOffsetY);
    canvas.drawBitmap(mCanvasBitmap, mTransformMatrix, null);
  }

  @Override
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
    screenWidth = getWidth();
    screenHeight = getHeight();
    mCanvasBitmap = Bitmap.createBitmap(mWorldMap.getWorldWidth(), mWorldMap.getWorldHeight(), Bitmap.Config.ARGB_8888);
    mBackingCanvas = new Canvas();
    mBackingCanvas.setBitmap(mCanvasBitmap);

    mTransformMatrix = new Matrix();

    // Create the background variables
    createBackground();
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}

  @Override
  public Point convertScreenPointToLocalPoint(PointF screenCoordinate) {
    Matrix inverse = new Matrix();
    mTransformMatrix.invert(inverse);
    float[] screenPts = new float[]{screenCoordinate.x, screenCoordinate.y};
    inverse.mapPoints(screenPts);

    return new Point((int) screenPts[0], (int) screenPts[1]);
  }

  private void createBackground() {
    // Create a very large bitmap
    mBackgroundBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

    // Make a new canvas for it
    Canvas backgroundCanvas = new Canvas();
    backgroundCanvas.setBitmap(mBackgroundBitmap);

    // Draw the background to the canvas
    backgroundCanvas.drawColor(Color.rgb(220, 220, 220));

    // transform it and write-back the changes to the actual canvas
    mBackgroundTransform = new Matrix();
//    mBackgroundTransform.setScale(1, 1);
//    mBackgroundTransform.postTranslate(0, 0);
  }
}
