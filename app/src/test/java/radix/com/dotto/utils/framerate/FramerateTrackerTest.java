package radix.com.dotto.utils.framerate;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class FramerateTrackerTest {

  @Test
  public void basicTest() {
    int num = 4;
    FramerateTracker f = new FramerateTracker(num);
    f.addFrameTime(99);
    f.addFrameTime(100);
    f.addFrameTime(100);
    f.addFrameTime(101);

    // 4 frames in 400 ms = 4 frames / 0.4 seconds = 10 fps
    Assert.assertEquals(10, f.getFps(), 1e-6);
  }
}
