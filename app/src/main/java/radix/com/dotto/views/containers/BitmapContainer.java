package radix.com.dotto.views.containers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Holder for a canvas-backed bitmap and view matrix
 */
public class BitmapContainer {
  private final Bitmap mBitmap;
  private final Matrix mViewTransform;
  private final Canvas mCanvas;

  public BitmapContainer(int bitmapWidth, int bitmapHeight, int transformInitialScaleX, int transformInitialScaleY,
                         int initialBgColor) {
    this.mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
    this.mViewTransform = new Matrix();

    // init our canvas and add the bitmap to it
    mCanvas = new Canvas();
    mCanvas.setBitmap(mBitmap);

    if (initialBgColor != -1) {
      mCanvas.drawColor(initialBgColor);
    }

    // apply our transforms
    mViewTransform.setScale(transformInitialScaleX, transformInitialScaleY);
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

  public Canvas getCanvas() {
    return mCanvas;
  }

  public void recycle() {
    mBitmap.recycle();
  }
}
