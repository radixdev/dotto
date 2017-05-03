package radix.com.dotto.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import radix.com.dotto.controllers.UserGestureController;
import radix.com.dotto.controllers.PixelInfo;
import radix.com.dotto.models.IModelInterface;
import radix.com.dotto.models.WorldMap;
import radix.com.dotto.utils.enums.GameColor;
import radix.com.dotto.utils.framerate.FramerateUtils;

@SuppressLint("ViewConstructor")
public class PixelGridSurfaceView extends SurfaceView implements IViewInterface, Runnable, SurfaceHolder.Callback {
  private static final String TAG = PixelGridSurfaceView.class.toString();

  private volatile boolean mIsGamePlaying;
  private Thread mGameThread = null;

  // The drawing properties
  private Bitmap mCanvasBitmap = null;
  private SurfaceHolder mSurfaceHolder;
  private int screenWidth, screenHeight;

  private Canvas mBackingCanvas = null;
  private Matrix mTransformMatrix;
  private Paint mPixelPaint;

  // Drawing the background
  private Bitmap mBackgroundBitmap = null;
  private Matrix mBackgroundTransform;

  // Controller interface
  private UserGestureController mUserGestureController;
  private IModelInterface mWorldMap;

  public PixelGridSurfaceView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PixelGridSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public PixelGridSurfaceView(Context context) {
    super(context);
  }

  public void setModelAndController(WorldMap map, UserGestureController userGestureController) {
    this.mUserGestureController = userGestureController;
    mWorldMap = map;

    mSurfaceHolder = getHolder();
    mSurfaceHolder.addCallback(this);
    mPixelPaint = new Paint();
  }

  @Override
  public void run() {
    while (mIsGamePlaying) {
      long start = System.currentTimeMillis();
      executeDraw();
      long end = System.currentTimeMillis();
      controlFramerate(end - start);
    }
  }

  private void controlFramerate(long frameTime) {
    // Sleep a bit maybe
    long sleepTime = FramerateUtils.getRefreshIntervalFromFramerate(60) - frameTime;
    if (sleepTime <= 0) {
      return;
    }
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      Log.e(TAG, "Exception while sleeping in game loop", e);
    }
  }

  @SuppressLint("NewApi")
  private void executeDraw() {
    boolean useSW = Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    if (!mSurfaceHolder.getSurface().isValid()) {
      return;
    }

    Canvas canvas;
    if (useSW) {
      canvas = mSurfaceHolder.lockCanvas();
    } else {
      canvas = mSurfaceHolder.getSurface().lockHardwareCanvas();
    }

    // Draw to the canvas
    this.draw(canvas);

    if (useSW) {
      mSurfaceHolder.unlockCanvasAndPost(canvas);
    } else {
      mSurfaceHolder.getSurface().unlockCanvasAndPost(canvas);
    }
  }

  private void drawBackground(Canvas canvas) {
    canvas.drawBitmap(mBackgroundBitmap, mBackgroundTransform, null);
  }

//  private void drawDebugHud(Canvas canvas) {
//    canvas.drawBitmap(mDebugHudBitmap, mDebugHudTransform, null);
//  }

  int where = 0;
  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);

    drawBackground(canvas);

//    mPixelPaint.setColor(GameColor.getRandomColor());
//    for (int i = 0; i < 128; i++) {
//      mBackingCanvas.drawPoint(random.nextInt(1000), random.nextInt(1000), mPixelPaint);
//    }

    final int size = 1000;
    for (int i = 0; i < 1; i++) {
      mPixelPaint.setColor(GameColor.getRandomColor());
      mBackingCanvas.drawPoint(where % size, 0, mPixelPaint);
      mBackingCanvas.drawPoint(0, where % size, mPixelPaint);
      where++;
    }

    // Pop from the model
    List<PixelInfo> taps = mWorldMap.getGridInfo(25);
    for (PixelInfo info : taps) {
      mPixelPaint.setColor(info.getColor().getColor());
      mBackingCanvas.drawPoint(info.getPointX(), info.getPointY(), mPixelPaint);
    }

    float scaleFactor = mUserGestureController.getScaleFactor();
    int screenOffsetX = mUserGestureController.getScreenOffsetX();
    int screenOffsetY = mUserGestureController.getScreenOffsetY();

    mTransformMatrix.reset();
    mTransformMatrix.setScale(scaleFactor, scaleFactor);
    mTransformMatrix.postTranslate(screenOffsetX, screenOffsetY);

    // HW canvases need a paint here for some reason
    canvas.drawBitmap(mCanvasBitmap, mTransformMatrix, mPixelPaint);

//    drawDebugHud(canvas);
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
    createDebugHUD();
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int newWidth, int newHeight) {
    Log.d(TAG, "surfaceChanged called");
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    Log.d(TAG, "surfaceDestroyed called");
  }

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
    mBackgroundTransform.setScale(screenWidth, screenHeight);
  }

  private Bitmap mDebugHudBitmap;
  private Matrix mDebugHudTransform;
  private void createDebugHUD() {
    mDebugHudBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

    // Make a new canvas for it
    Canvas canvas = new Canvas();
    canvas.setBitmap(mDebugHudBitmap);

    // Draw the background to the canvas
    canvas.drawColor(Color.BLACK);

    // transform it and write-back the changes to the actual canvas
    mDebugHudTransform = new Matrix();
    mDebugHudTransform.setScale(20, 100);
  }
}
