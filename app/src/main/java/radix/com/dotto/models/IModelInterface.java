package radix.com.dotto.models;

import android.graphics.Point;

import java.util.List;

import radix.com.dotto.controllers.DotInfo;

/**
 * Interface for interacting with the model
 */
public interface IModelInterface {
  int getWorldWidth();

  int getWorldHeight();

  /**
   * Attempts to write a dot to the model. The dot may not be written based on the current timeout.
   *
   * @param info
   */
  void onWriteDotInfo(DotInfo info);

  /**
   * Gets a list of elements for the view to render.
   *
   * @param maxElements
   * @return
   */
  List<DotInfo> getGridInfo(int maxElements);

  boolean isLocalPointOutsideWorldBounds(Point localPoint);

  /**
   * @return true if the model has any pixel infos available
   */
  boolean hasGridInfo();

  void setIsPlaying(boolean isPlaying);
}
