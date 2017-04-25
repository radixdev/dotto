package radix.com.dotto.utils.framerate;

/**
 * Tracks the framerate for a constant number of seen frames
 */
public class FramerateTracker {
  private final int mNumFrames;
  private final long[] mFrameTimeArray;

  private long mTotalFrameTimeSum;
  private int mFrameTimeArrayIndex;

  public FramerateTracker(int numFrames) {
    if (numFrames <= 0) {
      throw new IllegalArgumentException("NumFrames cannot be 0");
    }
    this.mNumFrames = numFrames;
    this.mFrameTimeArray = new long[numFrames];
  }

  /**
   * Adds a frame by it's draw time in milliseconds
   * @param frameTime
   */
  public void addFrameTime(long frameTime) {
    long previousTime = mFrameTimeArray[mFrameTimeArrayIndex];
    mFrameTimeArray[mFrameTimeArrayIndex] = frameTime;
    // re-adjust our sum
    mTotalFrameTimeSum -= previousTime;
    mTotalFrameTimeSum += frameTime;

    // Add the frame time back to the array
    mFrameTimeArrayIndex++;
    mFrameTimeArrayIndex = mFrameTimeArrayIndex % mNumFrames;
  }

  public float getFps() {
    return mNumFrames / (mTotalFrameTimeSum / 1000f);
  }
}
