package radix.com.dotto.controllers.haptic;

import android.content.Context;
import android.os.Vibrator;

/**
 *
 */
public class VibrateHandler {

  private final Context mContext;
  private final Vibrator mVibrateService;

  // Off - On - etc.
  private final long[] SUCCESS_VIBRATE_PATTERN = new long[] {
      0, 100
  };

  private final long[] SUCCESS_FAILURE_PATTERN = new long[] {
      0, 100,
      120, 100,
      120, 100
  };

  public VibrateHandler(Context context) {
    this.mContext = context;
    mVibrateService = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
  }

  public void performSuccess() {
    mVibrateService.vibrate(SUCCESS_VIBRATE_PATTERN, -1);
  }

  public void performFailure() {
    mVibrateService.vibrate(SUCCESS_FAILURE_PATTERN, -1);
  }
}
