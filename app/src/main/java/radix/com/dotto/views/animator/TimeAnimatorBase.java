package radix.com.dotto.views.animator;

import android.util.Log;

/**
 *
 */
public class TimeAnimatorBase {
  private static final String TAG = TimeAnimatorBase.class.toString();

  private long mStartTimeMs;
  private boolean mIsRunning = false;

  public void start() {
    if (!mIsRunning) {
      Log.d(TAG, "start called");
      mIsRunning = true;
      mStartTimeMs = getTime();
    }
  }

  public boolean isRunning() {
    return mIsRunning;
  }

  public void end() {
    Log.d(TAG, "end called");

    mIsRunning = false;
  }

  public long getElapsed() {
    return getTime() - mStartTimeMs;
  }

  private long getTime() {
    return System.currentTimeMillis();
  }
}
