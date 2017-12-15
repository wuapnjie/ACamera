package com.xiaopo.flying.acamera.model;

import android.hardware.camera2.CameraMetadata;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 相关聚焦模式的枚举，与系统相关常量一一对应
 *
 * @author wupanjie
 */
public enum AutoFocusMode {
  FIXED(CameraMetadata.CONTROL_AF_MODE_OFF),
  AUTO(CameraMetadata.CONTROL_AF_MODE_AUTO),
  CONTINUOUS_PICTURE(CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE),
  CONTINUOUS_VIDEO(CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO),
  EDOF(CameraMetadata.CONTROL_AF_MODE_EDOF),
  MACRO(CameraMetadata.CONTROL_AF_MODE_MACRO);


  public final int cameraFocusConstant;

  AutoFocusMode(int cameraFocusConstant) {
    this.cameraFocusConstant = cameraFocusConstant;
  }

  public static AutoFocusMode of(@CameraFocusConstant int cameraFocusConstant) {
    switch (cameraFocusConstant) {
      case CameraMetadata.CONTROL_AF_MODE_AUTO:
        return AUTO;
      case CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE:
        return CONTINUOUS_PICTURE;
      case CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO:
        return CONTINUOUS_VIDEO;
      case CameraMetadata.CONTROL_AF_MODE_EDOF:
        return EDOF;
      case CameraMetadata.CONTROL_AF_MODE_MACRO:
        return MACRO;
      case CameraMetadata.CONTROL_AF_MODE_OFF:
        return FIXED;
    }

    return FIXED;
  }

  @IntDef({
      CameraMetadata.CONTROL_AF_MODE_OFF,
      CameraMetadata.CONTROL_AF_MODE_AUTO,
      CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE,
      CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO,
      CameraMetadata.CONTROL_AF_MODE_EDOF,
      CameraMetadata.CONTROL_AF_MODE_MACRO
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface CameraFocusConstant {

  }
}
