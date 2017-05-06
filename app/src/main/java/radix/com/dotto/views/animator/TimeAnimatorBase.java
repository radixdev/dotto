package radix.com.dotto.views.animator;

/**
 *
 */
public class TimeAnimatorBase {
  private long mStartTimeMs;
  private boolean mIsRunning;

  public void start() {
    if (!mIsRunning) {
      mIsRunning = true;
      mStartTimeMs = getTime();
    }
  }

  public boolean isRunning() {
    return mIsRunning;
  }

  public void end() {
    mIsRunning = false;
  }

  public long getElapsed() {
    return getTime() - mStartTimeMs;
  }

  private long getTime() {
    return System.currentTimeMillis();
  }
}
