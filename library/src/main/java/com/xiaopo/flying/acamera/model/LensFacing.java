package com.xiaopo.flying.acamera.model;

import android.hardware.camera2.CameraMetadata;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 镜头方向，前置或后置，与系统相关常量一一对应
 * @author wupanjie
 */
public enum LensFacing {
  BACK(CameraMetadata.LENS_FACING_BACK),
  FRONT(CameraMetadata.LENS_FACING_FRONT);

  public final int lensFacingConstant;

  LensFacing(int lensFacingConstant){
    this.lensFacingConstant = lensFacingConstant;
  }

  public static LensFacing of(@LensFacingConstant int lensFacingConstant){
    switch (lensFacingConstant) {
      case CameraMetadata.LENS_FACING_BACK:
        return BACK;
      case CameraMetadata.LENS_FACING_FRONT:
        return FRONT;
    }

    return BACK;
  }

  @IntDef({
      CameraMetadata.LENS_FACING_BACK,
      CameraMetadata.LENS_FACING_FRONT
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface LensFacingConstant {

  }
}
