package radix.com.dotto.models.abstractors;

/**
 *
 */
public interface IModelUpdateListener {
  /**
   * Called when the user timeout might have changed. Either a new write has occurred or the config value for writes
   * has been changed. Call {@link IModelInterface#getTimeUntilNextWrite()} for the timeout.
   */
  void onWriteTimeoutChange();
}
