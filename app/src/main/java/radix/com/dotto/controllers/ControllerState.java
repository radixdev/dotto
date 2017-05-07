package radix.com.dotto.controllers;

/**
 * Different states
 */
public enum ControllerState {
  /**
   * Regular mode, just viewing what's going on
   */
  PANNING,
  /**
   * Single tap of the screen. Shows the player what's going on.
   */
  USER_FOCUSING
}
