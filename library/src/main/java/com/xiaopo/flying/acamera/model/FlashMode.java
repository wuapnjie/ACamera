package com.xiaopo.flying.acamera.model;

import android.hardware.camera2.CameraMetadata;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 相关闪光灯模式的枚举，与系统相关常量一一对应
 *
 * @author wupanjie
 */
public enum FlashMode {
  ON(CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH),
  OFF(CameraMetadata.CONTROL_AE_MODE_OFF),
  AUTO(CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH);

  public final int cameraFlashConstant;

  FlashMode(int cameraFlashConstant) {
    this.cameraFlashConstant = cameraFlashConstant;
  }

  public static FlashMode of(@CameraFlashConstant int cameraFlashConstant) {
    switch (cameraFlashConstant) {
      case CameraMetadata.CONTROL_AE_MODE_OFF:
        return OFF;
      case CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH:
        return ON;
      case CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH:
        return AUTO;
    }

    return OFF;
  }

  @IntDef({
      CameraMetadata.CONTROL_AE_MODE_OFF,
      CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH,
      CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH,
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface CameraFlashConstant {

  }
}
