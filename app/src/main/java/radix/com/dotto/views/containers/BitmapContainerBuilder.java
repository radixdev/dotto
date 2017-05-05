package radix.com.dotto.views.containers;

public class BitmapContainerBuilder {
  private int bitmapWidth = 1;
  private int bitmapHeight = 1;
  private int transformInitialScaleX;
  private int transformInitialScaleY;
  private Integer initialBgColor = null;

  public BitmapContainerBuilder setBitmapWidth(int bitmapWidth, int bitmapHeight) {
    this.bitmapWidth = bitmapWidth;
    this.bitmapHeight = bitmapHeight;
    return this;
  }

  public BitmapContainerBuilder setTransformInitialScale(int transformInitialScaleX, int transformInitialScaleY) {
    this.transformInitialScaleX = transformInitialScaleX;
    this.transformInitialScaleY = transformInitialScaleY;
    return this;
  }

  public BitmapContainerBuilder setInitialBgColor(int initialBgColor) {
    this.initialBgColor = initialBgColor;
    return this;
  }

  public BitmapContainer build() {
    return new BitmapContainer(bitmapWidth, bitmapHeight, transformInitialScaleX, transformInitialScaleY, initialBgColor);
  }
}