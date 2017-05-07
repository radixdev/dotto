package radix.com.dotto.views;

import android.graphics.Point;
import android.graphics.PointF;

import radix.com.dotto.controllers.ControllerState;

/**
 *
 */
public interface IViewInterface {
  void setPlaying(boolean playing);

  Point convertScreenPointToLocalPoint(PointF screenCoordinate);
  Point convertLocalPointToScreenPoint(int localX, int localY);

  void onControllerStateChange(ControllerState newState);
}
