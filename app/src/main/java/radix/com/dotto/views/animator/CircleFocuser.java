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
  private static final String TAG = CircleFocuser.class.toString();

  private final BitmapContainer bitmapContainer;
  private final long animationFrequency;

  private final Paint paint;
  private int halfWidth;
  private int halfHeight;

  private final double WAVE_CONSTANT;

  public CircleFocuser(BitmapContainer bitmapContainer, long frequency) {
    this.bitmapContainer = bitmapContainer;
    this.animationFrequency = frequency;

    paint = bitmapContainer.getPaint();
    halfWidth = bitmapContainer.getBitmapWidth() / 2;
    halfHeight = bitmapContainer.getBitmapHeight() / 2;

    WAVE_CONSTANT = Math.PI * 2d / (double) animationFrequency;
  }

  public void draw(Canvas actualScreenCanvas, float centerX, float centerY, float zoomFactor) {
    // Clear the background
    Canvas containerCanvas = bitmapContainer.getCanvas();
    containerCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

    // draw the thing
    // get the radius
    double low = 5d;
    double high = 50d;
    double radius = -Math.cos(WAVE_CONSTANT * getElapsed()) * (high - low)/2 + (high - low)/2;
//    Log.d(TAG, "radius: " + radius);
    containerCanvas.drawCircle(halfWidth, halfHeight, (float) radius, paint);

    // Back draw
    Matrix matrix = bitmapContainer.getViewTransform();
    matrix.reset();
    matrix.setTranslate(centerX - halfWidth, centerY - halfHeight);
    actualScreenCanvas.drawBitmap(bitmapContainer.getBitmap(), bitmapContainer.getViewTransform(), paint);
  }
}
