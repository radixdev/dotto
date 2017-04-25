package radix.com.dotto.models;

import java.util.ArrayList;
import java.util.List;

import radix.com.dotto.controllers.UserTapInfo;

public class WorldMap implements IModelInterface {

  private final List<UserTapInfo> mTapInfos;
  private List<UserTapInfo> result = new ArrayList<>();

  public WorldMap() {
    mTapInfos = new ArrayList<>();
    result = new ArrayList<>();
  }

  @Override
  public int getWorldWidth() {
    return 1000;
  }

  @Override
  public int getWorldHeight() {
    return 1000;
  }

  @Override
  public void onUserTapInfo(UserTapInfo info) {
    mTapInfos.add(info);
  }

  /**
   * @param maxElements max elements to return
   * @return a list of tap infos. These objects are not guaranteed to exist beyond the current frame!
   */
  @Override
  public List<UserTapInfo> getGridInfo(int maxElements) {
    result.clear();

    // "It's time to pop off" - Obama
    while (!mTapInfos.isEmpty() && result.size() < maxElements) {
      result.add(mTapInfos.remove(0));
    }
    return result;
  }
}
