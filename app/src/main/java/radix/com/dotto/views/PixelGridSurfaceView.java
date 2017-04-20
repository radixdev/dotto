package radix.com.dotto.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

import radix.com.dotto.models.WorldMap;
import radix.com.dotto.utils.FramerateUtils;

public class PixelGridSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
  private static final String TAG = PixelGridSurfaceView.class.toString();

  private final WorldMap mWorldMap;

  // The
  private final SurfaceHolder mSurfaceHolder;
  private volatile boolean mIsGamePlaying;
  private Thread mGameThread = null;

  // The drawing properties
  private Bitmap mCanvasBitmap = null;
  private Canvas mBackingCanvas = null;
  private Matrix mTransformMatrix;

  private Random random;

  public PixelGridSurfaceView(Context context, WorldMap map) {
    super(context);
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
      Thread.sleep(FramerateUtils.getRefreshIntervalFromFramerate(90));
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

  public volatile int tx=0, ty=0;

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);

    final Paint pixelPaint = new Paint();
    pixelPaint.setColor(Color.WHITE);
    for (int i = 0; i < 32; i++) {
      mBackingCanvas.drawPoint(random.nextInt(1000), random.nextInt(1000), pixelPaint);
    }

    mTransformMatrix.setTranslate(tx, ty);
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

    mTransformMatrix = new Matrix();
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}
}
