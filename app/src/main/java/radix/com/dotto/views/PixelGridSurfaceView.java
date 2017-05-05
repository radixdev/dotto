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
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import radix.com.dotto.controllers.PixelInfo;
import radix.com.dotto.controllers.UserGestureController;
import radix.com.dotto.models.IModelInterface;
import radix.com.dotto.models.WorldMap;
import radix.com.dotto.utils.enums.GameColor;
import radix.com.dotto.utils.framerate.FramerateUtils;
import radix.com.dotto.views.containers.BitmapContainer;
import radix.com.dotto.views.containers.BitmapContainerBuilder;

@SuppressLint("ViewConstructor")
public class PixelGridSurfaceView extends SurfaceView implements IViewInterface, Runnable, SurfaceHolder.Callback {
  private static final String TAG = PixelGridSurfaceView.class.toString();

  private volatile boolean mIsGamePlaying;
  private Thread mGameThread = null;

  // The drawing properties
  private Bitmap mCanvasBitmap = null;
  private SurfaceHolder mSurfaceHolder;
  private int mScreenWidth, mScreenHeight;

  private Canvas mBackingCanvas = null;
  private Matrix mTransformMatrix;
  private Paint mPixelPaint;

  // Drawing the other stuff
  private BitmapContainer mBackgroundContainer;
  private BitmapContainer mCenterOfScreenContainer;

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
    canvas.drawBitmap(mBackgroundContainer.getBitmap(), mBackgroundContainer.getViewTransform(), null);
  }

  private void drawCenterOfScreenHUD(Canvas drawContextCanvas) {
    // Re-draw the canvas then apply the text
    Canvas containerCanvas = mCenterOfScreenContainer.getCanvas();
    containerCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    containerCanvas.drawColor(Color.argb(25, 255, 255, 255));

    final Paint paint = mCenterOfScreenContainer.getPaint();

    Point centerPoint = convertScreenPointToLocalPoint(new PointF(mScreenWidth / 2, mScreenHeight / 2));
    final String hudText = String.format("(%s, %s)", centerPoint.x, centerPoint.y);
    containerCanvas.drawText(hudText, 150, hudHeight - 5, paint);

    drawContextCanvas.drawBitmap(mCenterOfScreenContainer.getBitmap(), mCenterOfScreenContainer.getViewTransform(), paint);
  }

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
    if (mWorldMap.hasGridInfo()) {
      List<PixelInfo> taps = mWorldMap.getGridInfo(1000);
      for (PixelInfo info : taps) {
        mPixelPaint.setColor(info.getColor().getColor());
        mBackingCanvas.drawPoint(info.getPointX(), info.getPointY(), mPixelPaint);
      }
    }

    matchScreenSizeMatrix(mTransformMatrix);

    // HW canvases need a paint here for some reason
    canvas.drawBitmap(mCanvasBitmap, mTransformMatrix, mPixelPaint);

    drawCenterOfScreenHUD(canvas);
  }

  /**
   * Match a screen width/height matrix to the actual screen
   *
   * @param matrix
   */
  private void matchScreenSizeMatrix(Matrix matrix) {
    float scaleFactor = mUserGestureController.getScaleFactor();
    float screenOffsetX = mUserGestureController.getScreenOffsetX();
    float screenOffsetY = mUserGestureController.getScreenOffsetY();

    matrix.reset();
    matrix.setScale(scaleFactor, scaleFactor);
    matrix.postTranslate(screenOffsetX, screenOffsetY);
  }

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {
    mScreenWidth = getWidth();
    mScreenHeight = getHeight();
    mCanvasBitmap = Bitmap.createBitmap(mWorldMap.getWorldWidth(), mWorldMap.getWorldHeight(), Bitmap.Config.ARGB_8888);
    mBackingCanvas = new Canvas();
    mBackingCanvas.setBitmap(mCanvasBitmap);

    mTransformMatrix = new Matrix();

    // Create the background
    mBackgroundContainer = new BitmapContainerBuilder()
        .setBitmapWidth(1, 1)
        .setTransformInitialScale(mScreenWidth, mScreenHeight)
        .setInitialBgColor(Color.rgb(220, 220, 220))
        .build();

    createCenterOfScreenHud();
  }

  int hudHeight;
  private void createCenterOfScreenHud() {
    hudHeight = 50;
    mCenterOfScreenContainer = new BitmapContainerBuilder()
        .setBitmapWidth(300, hudHeight)
        .build();

    // de-offset this to the screen bottom left
    Matrix matrix = mCenterOfScreenContainer.getViewTransform();
    matrix.setTranslate(0, mScreenHeight - hudHeight);

    final Paint paint = mCenterOfScreenContainer.getPaint();
    paint.setTextAlign(Paint.Align.CENTER);
    paint.setColor(Color.BLACK);
    paint.setTypeface(Typeface.SERIF);
    paint.setTextSize(50f);
    paint.setAntiAlias(true);
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
}
