package radix.com.dotto.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
      executeDraw();
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

  private void executeDraw() {
    if (mSurfaceHolder.getSurface().isValid()) {
      Canvas canvas = mSurfaceHolder.lockCanvas();
      drawBackground(canvas);
      this.draw(canvas);
      mSurfaceHolder.unlockCanvasAndPost(canvas);
    }
  }

  private void drawBackground(Canvas canvas) {
    canvas.drawBitmap(mBackgroundBitmap, mBackgroundTransform, null);
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    mPixelPaint.setColor(GameColor.getRandomColor());
    for (int i = 0; i < 32; i++) {
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

//    mTransformMatrix = new Matrix();
    mTransformMatrix.reset();

//    Log.d(TAG, "screen pts " + screenPts[0] + "  " + screenPts[1]);
//    Log.d(TAG, "screenOffsetX " + screenOffsetX + "  " + screenOffsetY + "  " + scaleFactor);
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
    mCanvasBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
    mBackingCanvas = new Canvas();
    mBackingCanvas.setBitmap(mCanvasBitmap);

    mTransformMatrix = new Matrix();

    // Create the background variables
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

  private void createBackground(Canvas screenCanvas, int color, int screenWidth, int screenHeight) {
    // Create a very large bitmap
    int width = screenWidth * 2;
    int height = screenHeight * 2;
    Bitmap backgroundBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

    // Make a new canvas for it
    Canvas backgroundCanvas = new Canvas();
    backgroundCanvas.setBitmap(backgroundBitMap);

    // Draw the background to the canvas
    backgroundCanvas.drawColor(color);

    // transform it and write-back the changes to the actual canvas
    Matrix transform = new Matrix();
    transform.setScale(1, 1);
    transform.postTranslate(0, 0);
    screenCanvas.drawBitmap(backgroundBitMap, transform, null);
  }
}
