package radix.com.dotto.models;

import java.util.ArrayList;
import java.util.List;

import radix.com.dotto.controllers.UserTapInfo;

public class WorldMap implements IModelInterface {

  private final List<UserTapInfo> mTapInfos;

  public WorldMap() {
    mTapInfos = new ArrayList<>();
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

  @Override
  public List<UserTapInfo> getGridInfo(int maxElements) {
    List<UserTapInfo> result = new ArrayList<>();

    // "It's time to pop off" - Obama
    while (!mTapInfos.isEmpty() && result.size() < maxElements) {
      result.add(mTapInfos.remove(0));
    }
    return result;
  }
}
