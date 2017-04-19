package radix.com.dotto.views;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import radix.com.dotto.models.WorldMap;

public class PixelGridSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

  private final WorldMap worldMap;
  private final SurfaceHolder holder;

  public PixelGridSurfaceView(Context context, WorldMap map) {
    super(context);
    worldMap = map;

    holder = getHolder();
    holder.addCallback(this);
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
  }

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {

  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

  }

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

  }
}
