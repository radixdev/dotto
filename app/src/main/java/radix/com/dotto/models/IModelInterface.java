package radix.com.dotto.models;

import java.util.List;

import radix.com.dotto.controllers.PixelInfo;

/**
 * Interface for interacting with the model
 */
public interface IModelInterface {
  int getWorldWidth();

  int getWorldHeight();

  void onPixelInfoChange(PixelInfo info);

  /**
   * Gets a list of elements for the view to render.
   *
   * @param maxElements
   * @return
   */
  List<PixelInfo> getGridInfo(int maxElements);
}
