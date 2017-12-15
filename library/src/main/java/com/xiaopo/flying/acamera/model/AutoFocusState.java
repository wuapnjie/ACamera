package com.xiaopo.flying.acamera.model;

import android.hardware.camera2.CaptureResult;

/**
 * @author wupanjie
 */
public enum AutoFocusState {
  /**
   * Indicates AF system is inactive for some reason (could be an error).
   */
  INACTIVE,
  /**
   * Indicates active scan in progress.
   */
  ACTIVE_SCAN,
  /**
   * Indicates active scan success (in focus).
   */
  ACTIVE_FOCUSED,
  /**
   * Indicates active scan failure (not in focus).
   */
  ACTIVE_UNFOCUSED,
  /**
   * Indicates passive scan in progress.
   */
  PASSIVE_SCAN,
  /**
   * Indicates passive scan success (in focus).
   */
  PASSIVE_FOCUSED,
  /**
   * Indicates passive scan failure (not in focus).
   */
  PASSIVE_UNFOCUSED;

  public static AutoFocusState from(int stateConstant) {
    switch (stateConstant) {
      case CaptureResult.CONTROL_AF_STATE_ACTIVE_SCAN:
        return ACTIVE_SCAN;
      case CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN:
        return PASSIVE_SCAN;
      case CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED:
        return PASSIVE_FOCUSED;
      case CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED:
        return ACTIVE_FOCUSED;
      case CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED:
        return PASSIVE_UNFOCUSED;
      case CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED:
        return ACTIVE_UNFOCUSED;
      default:
        return INACTIVE;
    }
  }
}
