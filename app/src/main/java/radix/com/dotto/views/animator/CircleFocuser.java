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
    double low = 0d;
    double high = bitmapContainer.getBitmapHeight() * 0.5d;
    double radius = -Math.cos(WAVE_CONSTANT * getElapsed()) * (high - low)/2d + (high - low)/2d;
//    Log.d(TAG, "radius: " + radius);
    paint.setColor(Color.HSVToColor(new float[]{(getElapsed()/32) % 360, 1, 1}));
    containerCanvas.drawCircle(halfWidth, halfHeight, (float) radius, paint);

    // Back draw
    Matrix matrix = bitmapContainer.getViewTransform();
    matrix.reset();
    matrix.setTranslate(centerX - halfWidth, centerY - halfHeight);
    actualScreenCanvas.drawBitmap(bitmapContainer.getBitmap(), bitmapContainer.getViewTransform(), paint);
  }
}
