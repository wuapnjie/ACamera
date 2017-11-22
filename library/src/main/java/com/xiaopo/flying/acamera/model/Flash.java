package com.xiaopo.flying.acamera.model;

import android.support.annotation.NonNull;

/**
 * @author wupanjie
 */
public enum Flash {
  AUTO("auto"), OFF("off"), ON("on");

  private final String mSettingsString;

  Flash(@NonNull String settingsString) {
    mSettingsString = settingsString;
  }

  @NonNull
  public String encodeSettingsString() {
    return mSettingsString;
  }

  @NonNull
  public static Flash decodeSettingsString(@NonNull String setting) {
    if (AUTO.encodeSettingsString().equals(setting)) {
      return AUTO;
    } else if (OFF.encodeSettingsString().equals(setting)) {
      return OFF;
    } else if (ON.encodeSettingsString().equals(setting)) {
      return ON;
    }
    throw new IllegalArgumentException("Not a valid setting");
  }
}
