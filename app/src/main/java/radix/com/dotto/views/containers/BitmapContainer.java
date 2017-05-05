package radix.com.dotto.views.containers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Holder for a canvas-backed bitmap and view matrix
 */
public class BitmapContainer {
  private final Bitmap mBitmap;
  private final Matrix mViewTransform;
  private final Canvas mCanvas;
  private final Paint mPaint;

  public BitmapContainer(int bitmapWidth, int bitmapHeight, int transformInitialScaleX, int transformInitialScaleY,
                         Integer initialBgColor) {
    this.mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
    this.mViewTransform = new Matrix();

    // init our canvas and add the bitmap to it
    mCanvas = new Canvas();
    mCanvas.setBitmap(mBitmap);

    if (initialBgColor != null) {
      mCanvas.drawColor(initialBgColor);
    }

    // apply our transforms
    mViewTransform.setScale(transformInitialScaleX, transformInitialScaleY);
    mPaint = new Paint();
  }

  /**
   * @return the view matrix.
   */
  public Matrix getViewTransform() {
    return mViewTransform;
  }

  public Bitmap getBitmap() {
    return mBitmap;
  }

  public Paint getPaint() {
    return mPaint;
  }

  public Canvas getCanvas() {
    return mCanvas;
  }

  public void recycle() {
    mBitmap.recycle();
  }
}
