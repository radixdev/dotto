package radix.com.dotto.views.animator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import radix.com.dotto.views.containers.BitmapContainer;

/**
 * Breathing circle around the center of the canvas
 */
public class CircleFocuser extends TimeAnimatorBase {
  private final BitmapContainer bitmapContainer;
  private final long animationFrequency;

  private final Paint paint;
  private int halfWidth;
  private int halfHeight;

  public CircleFocuser(BitmapContainer bitmapContainer, long frequency) {
    this.bitmapContainer = bitmapContainer;
    this.animationFrequency = frequency;

    paint = bitmapContainer.getPaint();
    halfWidth = bitmapContainer.getBitmapWidth() / 2;
    halfHeight = bitmapContainer.getBitmapHeight() / 2;
  }

  public void draw(Canvas actualScreenCanvas, float centerX, float centerY) {
    // Clear the background
    Canvas containerCanvas = bitmapContainer.getCanvas();
    containerCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

    // draw the thing
    // get the radius
    float radius = 100;//getElapsed()
    containerCanvas.drawCircle(halfWidth, halfHeight, radius, paint);

    // Back draw
    Matrix matrix = bitmapContainer.getViewTransform();
    matrix.reset();
    matrix.setTranslate(centerX - halfWidth, centerY - halfHeight);
    actualScreenCanvas.drawBitmap(bitmapContainer.getBitmap(), bitmapContainer.getViewTransform(), paint);
  }
}
