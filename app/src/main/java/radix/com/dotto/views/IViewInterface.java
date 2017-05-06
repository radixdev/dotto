package radix.com.dotto.views;

import android.graphics.Point;
import android.graphics.PointF;

/**
 *
 */
public interface IViewInterface {
  void setPlaying(boolean playing);

  Point convertScreenPointToLocalPoint(PointF screenCoordinate);
  Point convertLocalPointToScreenPoint(Point localCoordinate);
}
