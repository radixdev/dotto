package radix.com.dotto.views.animator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import radix.com.dotto.views.containers.BitmapContainer;

/**
 *
 */
public class SquareFocuser extends TimeAnimatorBase {
  private static final String TAG = CircleFocuser.class.toString();

  private final BitmapContainer bitmapContainer;

  private final Paint paint;
  private int halfWidth;
  private int halfHeight;

  public SquareFocuser(BitmapContainer bitmapContainer) {
    this.bitmapContainer = bitmapContainer;

    paint = bitmapContainer.getPaint();
    halfWidth = bitmapContainer.getBitmapWidth() / 2;
    halfHeight = bitmapContainer.getBitmapHeight() / 2;
  }

  public void draw(Canvas actualScreenCanvas, float centerX, float centerY, float scaleValue) {
    // Clear the background
    Canvas containerCanvas = bitmapContainer.getCanvas();
    containerCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//    containerCanvas.drawColor(Color.argb(200, 255, 255, 255));

    // draw a square
    Rect rect = new Rect(0, 0, bitmapContainer.getBitmapWidth(), bitmapContainer.getBitmapHeight());

    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(50f);
    paint.setColor(Color.HSVToColor(new float[]{(getElapsed() / 16) % 360, 1, 1}));
    containerCanvas.drawRect(rect, paint);

    // Back draw
    Matrix matrix = bitmapContainer.getViewTransform();
    matrix.reset();
    matrix.setTranslate(centerX - halfWidth, centerY - halfHeight);

    // The scale is a relation between the bitmap size and the size of a dot on screen
    float scale = (scaleValue + paint.getStrokeWidth()) / bitmapContainer.getBitmapWidth();
    matrix.setScale(scale, scale);
    matrix.postTranslate(centerX - halfWidth * scale, centerY - halfHeight * scale);
    actualScreenCanvas.drawBitmap(bitmapContainer.getBitmap(), bitmapContainer.getViewTransform(), paint);
  }
}
