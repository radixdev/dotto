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
  private static final String TAG = SquareFocuser.class.toString();

  private final BitmapContainer bitmapContainer;

  private final Paint paint;
  private int halfWidth;
  private int halfHeight;
  private final Matrix matrix;

  public SquareFocuser(BitmapContainer bitmapContainer) {
    this.bitmapContainer = bitmapContainer;
    this.matrix = bitmapContainer.getViewTransform();

    paint = bitmapContainer.getPaint();
    halfWidth = bitmapContainer.getBitmapWidth() / 2;
    halfHeight = bitmapContainer.getBitmapHeight() / 2;
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(40f);
  }

  public void draw(Canvas actualScreenCanvas, float centerX, float centerY, int pixelLengthOfDot) {
    // Clear the background
    Canvas containerCanvas = bitmapContainer.getCanvas();
    containerCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

    // draw a square
    Rect rect = new Rect(0, 0, bitmapContainer.getBitmapWidth(), bitmapContainer.getBitmapHeight());


    paint.setColor(Color.HSVToColor(new float[]{(getElapsed() / 16f) % 360, 1, 1}));
    containerCanvas.drawRect(rect, paint);

    // Back draw
    // The scale is a relation between the bitmap size and the size of a dot on screen
    float scale = (pixelLengthOfDot + paint.getStrokeWidth()) / bitmapContainer.getBitmapWidth();

    matrix.reset();
    matrix.setScale(scale, scale);
    matrix.postTranslate(centerX - halfWidth * scale, centerY - halfHeight * scale);

    actualScreenCanvas.drawBitmap(bitmapContainer.getBitmap(), bitmapContainer.getViewTransform(), paint);
  }
}
